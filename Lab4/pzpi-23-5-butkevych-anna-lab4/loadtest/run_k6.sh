#!/usr/bin/env bash
set -euo pipefail

# run_k6.sh - scale backend replicas and run k6 inside Docker for each replica count
# Usage: ./run_k6.sh 1 3 5

if [ "$#" -eq 0 ]; then
  REPLICAS=(1 3 5)
else
  REPLICAS=("$@")
fi
COMPOSE="docker compose"

echo "Bringing up stack (build if needed)..."
$COMPOSE up -d --build

for r in "${REPLICAS[@]}"; do
  printf "\n=== Scaling backend to %s replicas ===\n" "$r"
  $COMPOSE up -d --scale backend=$r
  # give containers a moment to start, then wait until nginx responds
  sleep 5
  echo "Waiting for nginx to become ready (up to 30s)..."
  for i in $(seq 1 30); do
    if curl -sS http://localhost:3001/ >/dev/null 2>&1; then
      echo "nginx is ready"
      break
    fi
    sleep 1
  done
  # Clear users table in Postgres to ensure unique test users for each run
  echo "Clearing users table in Postgres (container: pgdb)..."
  if docker ps --format '{{.Names}}' | grep -q '^pgdb$'; then
    docker exec -i pgdb psql -U postgres -d mydb -c "TRUNCATE TABLE \"users\" RESTART IDENTITY CASCADE;" || echo "Failed to truncate users table"
  else
    echo "Postgres container 'pgdb' not running; skipping truncate"
  fi
  echo "--- Running k6 for replicas=$r ---"

  # Run k6 in Docker; inside the compose network the nginx service is reachable as 'nginx'
  # Use configurable image (default: grafana/k6 which provides multi-arch support)
  K6_IMAGE=${K6_IMAGE:-grafana/k6:latest}
  # When running k6 in a raw docker container, use host.docker.internal to reach services
  # mapped to the host (nginx is exposed on host port 3001).
  # Ensure host.docker.internal resolves by adding host-gateway mapping (Docker 20.10+)
  docker run --rm --add-host=host.docker.internal:host-gateway -v "$PWD/loadtest:/scripts" -e HOST="http://host.docker.internal:3001" "$K6_IMAGE" run /scripts/k6_test.js || true
done

echo "All runs finished."
