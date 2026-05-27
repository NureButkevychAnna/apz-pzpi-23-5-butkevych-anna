import { useReadingsViewModel } from "../viewmodels/useReadingsViewModel";
import { useI18n } from "../i18n";
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
import { AlertCircle, RefreshCw } from "lucide-react";

export function ReadingsPage() {
  const vm = useReadingsViewModel();
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

      {/* Filters */}
      <div className="card" style={{ marginBottom: "24px" }}>
        <div
          style={{
            display: "flex",
            gap: "16px",
            flexWrap: "wrap",
            alignItems: "flex-end",
          }}
        >
          <div
            className="form-group"
            style={{ flex: "1 1 200px", marginBottom: 0 }}
          >
            <label>{t("readings.filters.device")}</label>
            <select
              value={vm.selectedDeviceId}
              onChange={(e) => vm.setSelectedDeviceId(e.target.value)}
            >
              <option value="">Всі пристрої</option>
              {vm.devices.map((d) => (
                <option key={d.id} value={d.id}>
                  {d.name}
                </option>
              ))}
            </select>
          </div>
          <div
            className="form-group"
            style={{ flex: "1 1 160px", marginBottom: 0 }}
          >
            <label>{t("readings.filters.since")}</label>
            <input
              type="datetime-local"
              value={vm.sinceDate}
              onChange={(e) => vm.setSinceDate(e.target.value)}
            />
          </div>
          <div
            className="form-group"
            style={{ flex: "0 0 120px", marginBottom: 0 }}
          >
            <label>{t("readings.filters.limit")}</label>
            <input
              type="number"
              min={10}
              max={1000}
              value={vm.limit}
              onChange={(e) => vm.setLimit(Number(e.target.value))}
            />
          </div>
          <button
            className="btn btn-primary"
            onClick={vm.refresh}
            style={{ marginBottom: 0 }}
          >
            <RefreshCw size={16} /> {t("readings.update")}
          </button>
        </div>
      </div>

      {/* Stats */}
      {vm.stats && (
        <div className="grid grid-4" style={{ marginBottom: "24px" }}>
          <div className="stat-card">
            <div className="stat-card-value">{vm.stats.count}</div>
            <div className="stat-card-label">{t("readings.stats.total")}</div>
          </div>
          <div className="stat-card">
            <div className="stat-card-value">{vm.stats.avg} µSv/h</div>
            <div className="stat-card-label">{t("readings.stats.avg")}</div>
          </div>
          <div className="stat-card">
            <div
              className="stat-card-value"
              style={{ color: "var(--success)" }}
            >
              {vm.stats.min} µSv/h
            </div>
            <div className="stat-card-label">{t("readings.stats.min")}</div>
          </div>
          <div className="stat-card">
            <div className="stat-card-value" style={{ color: "var(--danger)" }}>
              {vm.stats.max} µSv/h
            </div>
            <div className="stat-card-label">{t("readings.stats.max")}</div>
          </div>
        </div>
      )}

      {/* Chart */}
      <div className="chart-container" style={{ marginBottom: "24px" }}>
        <h3 style={{ marginBottom: "16px" }}>
          {vm.selectedDevice
            ? `📈 ${vm.selectedDevice.name}`
            : t("readings.chartTitleAll")}
        </h3>
        {vm.isLoadingReadings ? (
          <div
            style={{
              display: "flex",
              justifyContent: "center",
              padding: "40px",
            }}
          >
            <div className="spinner" />
          </div>
        ) : vm.chartData.length > 0 ? (
          <ResponsiveContainer width="100%" height={300}>
            <LineChart data={vm.chartData}>
              <CartesianGrid strokeDasharray="3 3" stroke="#f0f0f0" />
              <XAxis dataKey="time" tick={{ fontSize: 11 }} />
              <YAxis tick={{ fontSize: 11 }} unit=" µSv" />
              <Tooltip
                formatter={(v: number) => [
                  `${v} µSv/h`,
                  t("readings.stats.avg"),
                ]}
              />
              <Legend />
              <Line
                type="monotone"
                dataKey="value"
                stroke="var(--primary)"
                name="µSv/h"
                strokeWidth={2}
                dot={false}
                activeDot={{ r: 5 }}
              />
            </LineChart>
          </ResponsiveContainer>
        ) : (
          <div className="empty-state">
            <div className="empty-state-icon">📊</div>
            <p className="empty-state-text">{t("readings.noDataPeriod")}</p>
          </div>
        )}
      </div>

      {/* Table */}
      <div className="card">
        <h3 style={{ marginBottom: "16px" }}>{t("readings.table.title")}</h3>
        {vm.readings.length > 0 ? (
          <div style={{ overflowX: "auto" }}>
            <table className="table">
              <thead>
                <tr>
                  <th>{t("readings.filters.device")}</th>
                  <th>{"Значення (µSv/h)"}</th>
                  <th>Час вимірювання</th>
                </tr>
              </thead>
              <tbody>
                {vm.readings.map((r) => (
                  <tr key={r.id}>
                    <td>
                      <strong>{r.device?.name ?? "—"}</strong>
                    </td>
                    <td>
                      <span
                        style={{
                          fontWeight: "600",
                          color:
                            r.value > 1
                              ? "var(--danger)"
                              : r.value > 0.5
                                ? "var(--warning)"
                                : "var(--success)",
                        }}
                      >
                        {r.value} µSv/h
                      </span>
                    </td>
                    <td style={{ fontSize: "13px" }}>
                      {new Date(r.measured_at).toLocaleString("uk-UA")}
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        ) : (
          <div className="empty-state">
            <p className="empty-state-text">Немає показань</p>
          </div>
        )}
      </div>
    </div>
  );
}
