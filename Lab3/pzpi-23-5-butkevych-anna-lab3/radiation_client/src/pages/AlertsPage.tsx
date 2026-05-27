import { useAlertsViewModel } from "../viewmodels/useAlertsViewModel";
import type {
  AlertLevelFilter,
  AlertAckFilter,
} from "../viewmodels/useAlertsViewModel";
import { AlertCircle, CheckCircle, Eye, RefreshCw } from "lucide-react";
import { useI18n } from "../i18n";

const LEVEL_STYLES: Record<
  string,
  { bg: string; border: string; badge: string }
> = {
  warning: { bg: "#fffbeb", border: "#f59e0b", badge: "badge-warning" },
  danger: { bg: "#fff1f2", border: "#ff6b6b", badge: "badge-danger" },
  critical: { bg: "#fef2f2", border: "#ef4444", badge: "badge-danger" },
};
const LEVEL_LABELS: Record<string, string> = {
  warning: "alerts.level.warning",
  danger: "alerts.level.danger",
  critical: "alerts.level.critical",
};

export function AlertsPage() {
  const vm = useAlertsViewModel();
  const { t } = useI18n();

  if (vm.isLoading) {
    return (
      <div className="loading">
        <div className="spinner" />
      </div>
    );
  }

  return (
    <div>
      {vm.error && (
        <div className="alert alert-error">
          <AlertCircle size={18} /> {vm.error}
        </div>
      )}

      {/* Filters + counts */}
      <div className="card" style={{ marginBottom: "24px" }}>
        <div
          style={{
            display: "flex",
            gap: "12px",
            flexWrap: "wrap",
            alignItems: "center",
          }}
        >
          <div
            className="form-group"
            style={{ flex: "1 1 160px", marginBottom: 0 }}
          >
            <label>{t("readings.filters.device") /* level label */}</label>
            <select
              value={vm.levelFilter}
              onChange={(e) =>
                vm.setLevelFilter(e.target.value as unknown as AlertLevelFilter)
              }
            >
              <option value="all">{t("filters.all")}</option>
              <option value="warning">
                {t("alerts.level.warning")} ({vm.levelCount.warning})
              </option>
              <option value="danger">
                {t("alerts.level.danger")} ({vm.levelCount.danger})
              </option>
              <option value="critical">
                {t("alerts.level.critical")} ({vm.levelCount.critical})
              </option>
            </select>
          </div>
          <div
            className="form-group"
            style={{ flex: "1 1 160px", marginBottom: 0 }}
          >
            <label>{t("buttons.confirm") /* status label */}</label>
            <select
              value={vm.ackFilter}
              onChange={(e) =>
                vm.setAckFilter(e.target.value as unknown as AlertAckFilter)
              }
            >
              <option value="all">{t("filters.all")}</option>
              <option value="unacknowledged">
                {t("status.unacknowledged")}
              </option>
              <option value="acknowledged">{t("status.acknowledged")}</option>
            </select>
          </div>
          <button
            className="btn btn-secondary btn-small"
            onClick={vm.refresh}
            style={{ alignSelf: "flex-end" }}
          >
            <RefreshCw size={15} /> {t("alerts.refresh")}
          </button>
        </div>
      </div>

      {/* Detail modal */}
      {vm.selectedAlert && (
        <div
          style={{
            position: "fixed",
            inset: 0,
            backgroundColor: "rgba(0,0,0,0.5)",
            display: "flex",
            alignItems: "center",
            justifyContent: "center",
            zIndex: 1000,
          }}
          onClick={vm.closeDetail}
        >
          <div
            className="card"
            style={{ maxWidth: "480px", width: "90%", margin: 0 }}
            onClick={(e) => e.stopPropagation()}
          >
            <h3 style={{ marginBottom: "16px" }}>
              {t(LEVEL_LABELS[vm.selectedAlert.level])}
            </h3>
            <p>
              <strong>{t("readings.filters.device")}:</strong>{" "}
              {vm.selectedAlert.device?.name ?? "—"}
            </p>
            <p>
              <strong>{t("buttons.confirm")}:</strong>{" "}
              {vm.selectedAlert.message}
            </p>
            <p>
              <strong>{t("buttons.refresh")}:</strong>{" "}
              {new Date(vm.selectedAlert.created_at).toLocaleString("uk-UA")}
            </p>
            <p>
              <strong>{t("buttons.confirm")}:</strong>{" "}
              {vm.selectedAlert.acknowledged ? (
                <span className="badge badge-success">
                  ✓ {t("status.acknowledged")}
                </span>
              ) : (
                <span className="badge badge-warning">
                  {t("status.unacknowledged")}
                </span>
              )}
            </p>
            <div style={{ display: "flex", gap: "12px", marginTop: "20px" }}>
              {!vm.selectedAlert.acknowledged && (
                <button
                  className="btn btn-primary"
                  disabled={vm.isAcking === vm.selectedAlert.id}
                  onClick={() => vm.ack(vm.selectedAlert!.id)}
                >
                  {vm.isAcking === vm.selectedAlert.id ? (
                    <>
                      <span className="spinner" /> {t("buttons.save")}
                    </>
                  ) : (
                    <>
                      <CheckCircle size={16} /> {t("buttons.confirm")}
                    </>
                  )}
                </button>
              )}
              <button className="btn btn-secondary" onClick={vm.closeDetail}>
                {t("buttons.close")}
              </button>
            </div>
          </div>
        </div>
      )}

      {/* List */}
      {vm.alerts.length === 0 ? (
        <div className="card">
          <div className="empty-state">
            <div className="empty-state-icon">✅</div>
            <h3 className="empty-state-title">{t("alerts.emptyTitle")}</h3>
            <p className="empty-state-text">{t("alerts.emptyText")}</p>
          </div>
        </div>
      ) : (
        <div style={{ display: "flex", flexDirection: "column", gap: "12px" }}>
          {vm.alerts.map((alert) => {
            const s = LEVEL_STYLES[alert.level] ?? LEVEL_STYLES.warning;
            return (
              <div
                key={alert.id}
                style={{
                  backgroundColor: s.bg,
                  borderLeft: `4px solid ${s.border}`,
                  borderRadius: "8px",
                  padding: "16px",
                  display: "flex",
                  justifyContent: "space-between",
                  alignItems: "flex-start",
                  gap: "12px",
                }}
              >
                <div style={{ flex: 1 }}>
                  <div
                    style={{
                      display: "flex",
                      alignItems: "center",
                      gap: "10px",
                      marginBottom: "6px",
                    }}
                  >
                    <span className={`badge ${s.badge}`}>
                      {t(LEVEL_LABELS[alert.level])}
                    </span>
                    {alert.acknowledged && (
                      <span
                        className="badge badge-success"
                        style={{ fontSize: "11px" }}
                      >
                        ✓ {t("status.acknowledged")}
                      </span>
                    )}
                  </div>
                  <p style={{ margin: "0 0 4px", fontWeight: "600" }}>
                    {alert.device?.name ?? t("device.unknown")}
                  </p>
                  <p style={{ margin: "0 0 4px", fontSize: "14px" }}>
                    {alert.message}
                  </p>
                  <p style={{ margin: 0, fontSize: "12px", color: "#6b7280" }}>
                    {new Date(alert.created_at).toLocaleString("uk-UA")}
                  </p>
                </div>
                <div style={{ display: "flex", gap: "8px" }}>
                  <button
                    className="btn btn-secondary btn-small"
                    onClick={() => vm.viewDetail(alert.id)}
                  >
                    <Eye size={14} />
                  </button>
                  {!alert.acknowledged && (
                    <button
                      className="btn btn-primary btn-small"
                      disabled={vm.isAcking === alert.id}
                      onClick={() => vm.ack(alert.id)}
                      title={t("buttons.confirm")}
                    >
                      {vm.isAcking === alert.id ? (
                        <span
                          className="spinner"
                          style={{ width: 14, height: 14 }}
                        />
                      ) : (
                        <CheckCircle size={14} />
                      )}
                    </button>
                  )}
                </div>
              </div>
            );
          })}
        </div>
      )}
    </div>
  );
}
