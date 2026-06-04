#!/usr/bin/env bash
set -euo pipefail

# Safe deploy script: create DB backup, deploy, run migrations, restore on failure

DEPLOY_PATH=${DEPLOY_PATH:-.}
BACKUP_DIR=${BACKUP_DIR:-/var/backups/radiationmonitoring}
POSTGRES_CONTAINER_NAME=${POSTGRES_CONTAINER_NAME:-db}
POSTGRES_HOST=${POSTGRES_HOST:-localhost}
POSTGRES_USER=${POSTGRES_USER:-postgres}
POSTGRES_PASSWORD=${POSTGRES_PASSWORD:-}
POSTGRES_DB=${POSTGRES_DB:-postgres}
MIGRATE_COMMAND=${MIGRATE_COMMAND:-npx sequelize db:migrate}

mkdir -p "$BACKUP_DIR"
TIMESTAMP=$(date -u +"%Y%m%dT%H%M%SZ")
BACKUP_FILE="$BACKUP_DIR/db-backup-$TIMESTAMP.sql"

echo "[deploy_safe] Creating DB backup to $BACKUP_FILE"

# Try containerized pg_dump first
if docker exec "$POSTGRES_CONTAINER_NAME" pg_dump -U "$POSTGRES_USER" "$POSTGRES_DB" > "$BACKUP_FILE" 2>/dev/null; then
  echo "[deploy_safe] Backup saved via container $POSTGRES_CONTAINER_NAME"
else
  echo "[deploy_safe] Container backup failed; attempting docker pg client against host"
  if [ -n "$POSTGRES_PASSWORD" ]; then
    PGPASS_ARG=(--env PGPASSWORD="$POSTGRES_PASSWORD")
  else
    PGPASS_ARG=()
  fi
  docker run --rm "${PGPASS_ARG[@]}" postgres:15 pg_dump -h "$POSTGRES_HOST" -U "$POSTGRES_USER" "$POSTGRES_DB" > "$BACKUP_FILE"
fi

echo "[deploy_safe] Pulling and starting services in $DEPLOY_PATH"
cd "$DEPLOY_PATH"
docker-compose pull --ignore-pull-failures || true
docker-compose up -d --build

echo "[deploy_safe] Running migrations: $MIGRATE_COMMAND"
# Prefer running migration inside app container if exists
if docker-compose ps --services | grep -q '^app$'; then
  if docker-compose exec -T app sh -c "$MIGRATE_COMMAND"; then
    echo "[deploy_safe] Migrations applied successfully"
  else
    echo "[deploy_safe] Migrations failed — restoring DB from $BACKUP_FILE"
    if docker exec -i "$POSTGRES_CONTAINER_NAME" psql -U "$POSTGRES_USER" "$POSTGRES_DB" < "$BACKUP_FILE"; then
      echo "[deploy_safe] Restore succeeded via container"
    else
      echo "[deploy_safe] Container restore failed; attempting psql client restore"
      if [ -n "$POSTGRES_PASSWORD" ]; then
        docker run --rm -i -e PGPASSWORD="$POSTGRES_PASSWORD" --network host postgres:15 psql -h "$POSTGRES_HOST" -U "$POSTGRES_USER" "$POSTGRES_DB" < "$BACKUP_FILE"
      else
        docker run --rm -i postgres:15 psql -h "$POSTGRES_HOST" -U "$POSTGRES_USER" "$POSTGRES_DB" < "$BACKUP_FILE"
      fi
    fi
    echo "[deploy_safe] Restore attempted. Exiting with failure."
    exit 1
  fi
else
  # No `app` service; run migration locally
  if sh -c "$MIGRATE_COMMAND"; then
    echo "[deploy_safe] Migrations applied successfully (local)"
  else
    echo "[deploy_safe] Migrations failed (local) — restoring DB from $BACKUP_FILE"
    if [ -n "$POSTGRES_PASSWORD" ]; then
      docker run --rm -i -e PGPASSWORD="$POSTGRES_PASSWORD" --network host postgres:15 psql -h "$POSTGRES_HOST" -U "$POSTGRES_USER" "$POSTGRES_DB" < "$BACKUP_FILE"
    else
      docker run --rm -i postgres:15 psql -h "$POSTGRES_HOST" -U "$POSTGRES_USER" "$POSTGRES_DB" < "$BACKUP_FILE"
    fi
    echo "[deploy_safe] Restore attempted. Exiting with failure."
    exit 1
  fi
fi

echo "[deploy_safe] Deploy completed successfully"
