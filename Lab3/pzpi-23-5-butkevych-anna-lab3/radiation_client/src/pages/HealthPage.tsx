import { useHealthViewModel } from "../viewmodels/useHealthViewModel";
import { Zap, Activity, RefreshCw } from "lucide-react";
import { useI18n } from "../i18n";

export function HealthPage() {
  const vm = useHealthViewModel();
  const { t } = useI18n();

  if (vm.isLoading)
    return (
      <div className="loading">
        <div className="spinner" />
      </div>
    );

  return (
    <div>
      {vm.error && <div className="alert alert-error">{vm.error}</div>}

      <div
        style={{
          marginBottom: "18px",
          display: "flex",
          justifyContent: "space-between",
          alignItems: "center",
        }}
      >
        <div>
          <h2 style={{ margin: 0 }}>{t("health.title")}</h2>
          <div style={{ color: "var(--gray)", fontSize: "13px" }}>
            {vm.devices.length} {t("health.devicesCount")}
          </div>
        </div>
        <div style={{ display: "flex", gap: "8px" }}>
          <button className="btn" onClick={vm.refresh}>
            <RefreshCw size={14} /> {t("health.refresh")}
          </button>
          <button className="btn btn-primary" onClick={vm.recompute}>
            <Zap size={14} /> {t("health.recompute")}
          </button>
        </div>
      </div>

      <div style={{ marginBottom: "16px" }}>
        <label
          style={{ display: "block", marginBottom: "8px", fontWeight: 600 }}
        >
          {t("health.selectDevice")}
        </label>
        <select
          value={vm.selectedDeviceId}
          onChange={(e) => vm.setSelectedDeviceId(e.target.value)}
        >
          <option value="">{t("select.choose")}</option>
          {vm.devices.map((d) => (
            <option key={d.id} value={d.id}>
              {d.name}
            </option>
          ))}
        </select>
      </div>

      {!vm.health ? (
        <div className="card">
          <div className="empty-state">
            <div className="empty-state-icon">💤</div>
            <p className="empty-state-text">{t("health.noData")}</p>
          </div>
        </div>
      ) : (
        <>
          <div className="card" style={{ marginBottom: "20px" }}>
            <h3 style={{ display: "flex", alignItems: "center", gap: "8px" }}>
              <Activity size={18} /> {t("health.details")}
            </h3>
            <div className="grid grid-4" style={{ marginTop: "12px" }}>
              <div className="stat-card">
                <div
                  className="stat-card-value"
                  style={{
                    color: vm.getUptimeStatus(vm.health.uptime_pct).color,
                  }}
                >
                  {vm.health.uptime_pct.toFixed(1)}%
                </div>
                <div className="stat-card-label">
                  {t("health.uptime")} (
                  {vm.getUptimeStatus(vm.health.uptime_pct).label})
                </div>
              </div>

              <div className="stat-card">
                <div
                  className="stat-card-value"
                  style={{ color: vm.getBatteryStatus(vm.health.avg_battery) }}
                >
                  {vm.health.avg_battery.toFixed(1)}%
                </div>
                <div className="stat-card-label">
                  <Zap size={14} /> {t("health.battery")}
                </div>
              </div>

              <div className="stat-card">
                <div
                  className="stat-card-value"
                  style={{ color: "var(--primary)" }}
                >
                  {vm.health.missing_count}
                </div>
                <div className="stat-card-label">{t("health.missing")}</div>
              </div>

              <div className="stat-card">
                <div
                  className="stat-card-value"
                  style={{
                    color:
                      vm.health.error_count === 0
                        ? "var(--success)"
                        : "var(--warning)",
                  }}
                >
                  {vm.health.error_count}
                </div>
                <div className="stat-card-label">{t("health.errors")}</div>
              </div>
            </div>
          </div>

          <div className="card">
            <h3>{t("health.details")}</h3>
            <table className="table" style={{ marginTop: 12 }}>
              <tbody>
                <tr>
                  <td style={{ fontWeight: 600 }}>{t("health.lastReading")}</td>
                  <td>
                    {vm.health.last_seen
                      ? new Date(vm.health.last_seen).toLocaleString("uk-UA")
                      : "Немає"}
                  </td>
                </tr>
                <tr>
                  <td style={{ fontWeight: 600 }}>{t("health.checked")}</td>
                  <td>
                    {new Date(vm.health.checked_at).toLocaleString("uk-UA")}
                  </td>
                </tr>
                <tr>
                  <td style={{ fontWeight: 600 }}>{t("health.battery")}</td>
                  <td>
                    <div
                      style={{
                        width: 200,
                        height: 20,
                        backgroundColor: "var(--gray-light)",
                        borderRadius: 4,
                        overflow: "hidden",
                      }}
                    >
                      <div
                        style={{
                          width: `${vm.health.avg_battery}%`,
                          height: "100%",
                          backgroundColor: vm.getBatteryStatus(
                            vm.health.avg_battery,
                          ),
                        }}
                      />
                    </div>
                    <small style={{ display: "block", marginTop: 6 }}>
                      {vm.health.avg_battery.toFixed(1)}%
                    </small>
                  </td>
                </tr>
              </tbody>
            </table>
          </div>
        </>
      )}
    </div>
  );
}
