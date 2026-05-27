import {
  LineChart,
  Line,
  XAxis,
  YAxis,
  CartesianGrid,
  Tooltip,
  Legend,
  ResponsiveContainer,
} from "recharts";
import { useDashboardViewModel } from "../viewmodels/useDashboardViewModel";
import { useI18n } from "../i18n";
import { AlertCircle, RefreshCw, Activity, Cpu, Bell } from "lucide-react";

const LEVEL_COLORS: Record<string, string> = {
  info: "#f8fafc",
  warning: "#fffbeb",
  critical: "#fff1f2",
};
const LEVEL_BORDER: Record<string, string> = {
  info: "#93c5fd",
  warning: "#f59e0b",
  critical: "#ef4444",
};
const LEVEL_LABELS: Record<string, string> = {
  info: "alerts.level.info",
  warning: "alerts.level.warning",
  critical: "alerts.level.critical",
};

export function DashboardPage() {
  const vm = useDashboardViewModel();
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

      {/* Stat Cards */}
      <div className="grid grid-4" style={{ marginBottom: "24px" }}>
        <div className="stat-card">
          <div style={{ display: "flex", alignItems: "center", gap: "12px" }}>
            <Cpu size={28} color="var(--primary)" />
            <div>
              <div className="stat-card-value">{vm.stats.totalDevices}</div>
              <div className="stat-card-label">{t("stat.totalDevices")}</div>
            </div>
          </div>
        </div>
        <div className="stat-card">
          <div style={{ display: "flex", alignItems: "center", gap: "12px" }}>
            <Activity size={28} color="var(--success)" />
            <div>
              <div
                className="stat-card-value"
                style={{ color: "var(--success)" }}
              >
                {vm.stats.activeDevices}
              </div>
              <div className="stat-card-label">{t("stat.activeDevices")}</div>
            </div>
          </div>
        </div>
        <div className="stat-card">
          <div style={{ display: "flex", alignItems: "center", gap: "12px" }}>
            <Bell size={28} color="var(--warning)" />
            <div>
              <div
                className="stat-card-value"
                style={{ color: "var(--warning)" }}
              >
                {vm.stats.unacknowledgedAlerts}
              </div>
              <div className="stat-card-label">
                {t("stat.unacknowledgedAlerts")}
              </div>
            </div>
          </div>
        </div>
        <div className="stat-card">
          <div style={{ display: "flex", alignItems: "center", gap: "12px" }}>
            <AlertCircle size={28} color="var(--danger)" />
            <div>
              <div
                className="stat-card-value"
                style={{ color: "var(--danger)" }}
              >
                {vm.stats.criticalAlerts}
              </div>
              <div className="stat-card-label">{t("stat.criticalAlerts")}</div>
            </div>
          </div>
        </div>
      </div>

      <div className="grid grid-2">
        {/* Chart */}
        <div className="chart-container">
          <div
            style={{
              display: "flex",
              justifyContent: "space-between",
              alignItems: "center",
              marginBottom: "16px",
            }}
          >
            <h3 style={{ margin: 0 }}>{t("chart.readingsTitle")}</h3>
            <button
              className="btn btn-secondary btn-small"
              onClick={vm.refresh}
            >
              <RefreshCw size={14} />
            </button>
          </div>
          {vm.chartData.length > 0 ? (
            <ResponsiveContainer width="100%" height={280}>
              <LineChart data={vm.chartData}>
                <CartesianGrid strokeDasharray="3 3" stroke="#f0f0f0" />
                <XAxis dataKey="time" tick={{ fontSize: 11 }} />
                <YAxis tick={{ fontSize: 11 }} />
                <Tooltip />
                <Legend />
                <Line
                  type="monotone"
                  dataKey="value"
                  stroke="var(--primary)"
                  name="µSv/h"
                  strokeWidth={2}
                  dot={false}
                />
              </LineChart>
            </ResponsiveContainer>
          ) : (
            <div className="empty-state">
              <p>{t("chart.noReadings")}</p>
            </div>
          )}
          <p
            style={{ fontSize: "13px", color: "var(--gray)", marginTop: "8px" }}
          >
            {t("chart.avg")}: <strong>{vm.stats.avgRadiation} µSv/h</strong>
          </p>
        </div>

        {/* Recent Alerts */}
        <div className="card">
          <h3 style={{ marginBottom: "16px" }}>{t("alerts.title")}</h3>
          {vm.recentAlerts.length > 0 ? (
            <div
              style={{ display: "flex", flexDirection: "column", gap: "8px" }}
            >
              {vm.recentAlerts.map((alert) => (
                <div
                  key={alert.id}
                  style={{
                    padding: "12px",
                    borderRadius: "8px",
                    borderLeft: `4px solid ${LEVEL_BORDER[alert.level]}`,
                    backgroundColor: LEVEL_COLORS[alert.level],
                  }}
                >
                  <div
                    style={{
                      display: "flex",
                      justifyContent: "space-between",
                      alignItems: "flex-start",
                    }}
                  >
                    <div>
                      <span style={{ fontWeight: "600", fontSize: "13px" }}>
                        {LEVEL_LABELS[alert.level]}
                      </span>
                      <p
                        style={{
                          margin: "4px 0 0",
                          fontSize: "13px",
                          color: "#374151",
                        }}
                      >
                        <strong>{alert.device?.name}</strong>
                      </p>
                      <p
                        style={{
                          margin: "2px 0 0",
                          fontSize: "12px",
                          color: "#6b7280",
                        }}
                      >
                        {new Date(alert.created_at).toLocaleString("uk-UA")}
                      </p>
                    </div>
                    {alert.acknowledged && (
                      <span
                        className="badge badge-success"
                        style={{ fontSize: "11px" }}
                      >
                        ✓
                      </span>
                    )}
                  </div>
                </div>
              ))}
            </div>
          ) : (
            <div className="empty-state">
              <div className="empty-state-icon">✅</div>
              <p className="empty-state-text">{t("alerts.emptyText")}</p>
            </div>
          )}
        </div>
      </div>

      {/* Devices Table */}
      <div className="card" style={{ marginTop: "24px" }}>
        <h3 style={{ marginBottom: "16px" }}>{t("devices.myDevices")}</h3>
        {vm.devices.length > 0 ? (
          <table className="table">
            <thead>
              <tr>
                <th>{t("devices.table.name")}</th>
                <th>{t("devices.table.status")}</th>
                <th>{t("devices.table.location")}</th>
                <th>{t("devices.table.registered")}</th>
              </tr>
            </thead>
            <tbody>
              {vm.devices.map((device) => (
                <tr key={device.id}>
                  <td>
                    <strong>{device.name}</strong>
                  </td>
                  <td>
                    <span
                      className={`device-status ${device.is_active ? "active" : "inactive"}`}
                    >
                      <span
                        style={{
                          width: 8,
                          height: 8,
                          borderRadius: "50%",
                          backgroundColor: device.is_active
                            ? "var(--success)"
                            : "var(--danger)",
                          display: "inline-block",
                        }}
                      />
                      {device.is_active
                        ? t("devices.active")
                        : t("devices.inactive")}
                    </span>
                  </td>
                  <td>{device.location?.address ?? "—"}</td>
                  <td style={{ fontSize: "13px" }}>
                    {new Date(device.created_at).toLocaleDateString("uk-UA")}
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        ) : (
          <div className="empty-state">
            <p>{t("devices.emptyTitle")}</p>
          </div>
        )}
      </div>
    </div>
  );
}
