import { useAdminViewModel } from "../viewmodels/useAdminViewModel";
import { useI18n } from "../i18n";
import { AlertCircle, Play } from "lucide-react";
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

const TABS = [
  { id: "cumulative", label: "admin.tabs.cumulative" },
  { id: "ewma", label: "admin.tabs.ewma" },
  { id: "peaks", label: "admin.tabs.peaks" },
  { id: "history", label: "admin.tabs.history" },
] as const;

export function AdminPage() {
  const vm = useAdminViewModel();
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

      <h2 style={{ marginBottom: "24px" }}>{t("admin.title")}</h2>

      {/* Tabs */}
      <div
        style={{
          display: "flex",
          gap: "4px",
          marginBottom: "24px",
          borderBottom: "2px solid var(--border)",
        }}
      >
        {TABS.map((tab) => (
          <button
            key={tab.id}
            onClick={() => vm.setActiveTab(tab.id)}
            style={{
              padding: "10px 18px",
              background: "none",
              border: "none",
              borderBottom:
                vm.activeTab === tab.id
                  ? "2px solid var(--primary)"
                  : "2px solid transparent",
              color: vm.activeTab === tab.id ? "var(--primary)" : "var(--gray)",
              fontWeight: vm.activeTab === tab.id ? "600" : "400",
              cursor: "pointer",
              fontSize: "14px",
              marginBottom: "-2px",
            }}
          >
            {t(tab.label)}
          </button>
        ))}
      </div>

      {/* Cumulative Dose */}
      {vm.activeTab === "cumulative" && (
        <div className="grid grid-2">
          <div className="card">
            <h3>{t("admin.parameters")}</h3>
            <div className="form-group" style={{ marginTop: "16px" }}>
              <label>{t("admin.device")}</label>
              <select
                value={vm.form.deviceId}
                onChange={(e) => vm.setFormField("deviceId", e.target.value)}
                required
              >
                <option value="">{t("select.choose")}</option>
                {vm.devices.map((d) => (
                  <option key={d.id} value={d.id}>
                    {d.name}
                  </option>
                ))}
              </select>
            </div>

            <div className="form-group">
              <label>{t("admin.fromDate")}</label>
              <input
                type="datetime-local"
                value={vm.form.fromDate}
                onChange={(e) => vm.setFormField("fromDate", e.target.value)}
              />
            </div>

            <div className="form-group">
              <label>{t("admin.toDate")}</label>
              <input
                type="datetime-local"
                value={vm.form.toDate}
                onChange={(e) => vm.setFormField("toDate", e.target.value)}
              />
            </div>

            <button
              className="btn btn-primary"
              disabled={vm.isComputing}
              onClick={vm.runCumulative}
            >
              {vm.isComputing ? (
                <>
                  <span className="spinner" /> {t("buttons.save")}
                </>
              ) : (
                <>
                  <Play size={16} /> {t("admin.compute")}
                </>
              )}
            </button>
          </div>

          <div>
            {vm.results.cumulative != null && (
              <div>
                <h3>{t("admin.result")}</h3>
                <div className="stat-card" style={{ marginTop: "20px" }}>
                  <div
                    className="stat-card-value"
                    style={{ fontSize: "36px", color: "var(--primary)" }}
                  >
                    {(() => {
                      const v = vm.results.cumulative as unknown;
                      if (v && typeof v === "object") {
                        const obj = v as Record<string, unknown>;
                        return String(
                          obj["cumulative_dose"] ?? JSON.stringify(obj),
                        );
                      }
                      return String(v ?? "");
                    })()}
                  </div>
                  <div className="stat-card-label">
                    {t("admin.cumulative_label")}
                  </div>
                </div>
              </div>
            )}
          </div>
        </div>
      )}

      {/* EWMA */}
      {vm.activeTab === "ewma" && (
        <div>
          <div className="card" style={{ marginBottom: "24px" }}>
            <h3>{t("admin.ewma.parameters")}</h3>
            <div
              style={{
                display: "flex",
                gap: "16px",
                flexWrap: "wrap",
                marginTop: "16px",
                alignItems: "flex-end",
              }}
            >
              <div
                className="form-group"
                style={{ flex: "1 1 180px", marginBottom: 0 }}
              >
                <label>{t("admin.device")}</label>
                <select
                  value={vm.form.deviceId}
                  onChange={(e) => vm.setFormField("deviceId", e.target.value)}
                >
                  <option value="">{t("select.choose")}</option>
                  {vm.devices.map((d) => (
                    <option key={d.id} value={d.id}>
                      {d.name}
                    </option>
                  ))}
                </select>
              </div>

              <div
                className="form-group"
                style={{ flex: "0 0 120px", marginBottom: 0 }}
              >
                <label>Alpha (0–1)</label>
                <input
                  type="number"
                  min="0.01"
                  max="1"
                  step="0.01"
                  value={vm.form.alpha}
                  onChange={(e) =>
                    vm.setFormField("alpha", String(e.target.value))
                  }
                />
              </div>

              <button
                className="btn btn-primary"
                disabled={vm.isComputing}
                onClick={vm.runEWMA}
              >
                {vm.isComputing ? (
                  <>
                    <span className="spinner" /> {t("buttons.save")}
                  </>
                ) : (
                  <>
                    <Play size={16} /> {t("admin.compute")}
                  </>
                )}
              </button>
            </div>
          </div>

          {Array.isArray(vm.results.ewma) && vm.results.ewma.length > 0 ? (
            <div className="chart-container">
              <h3 style={{ marginBottom: "16px" }}>{t("admin.ewma.title")}</h3>
              <ResponsiveContainer width="100%" height={280}>
                <LineChart
                  data={
                    vm.results.ewma as unknown as Array<Record<string, unknown>>
                  }
                >
                  <CartesianGrid strokeDasharray="3 3" />
                  <XAxis
                    dataKey="measured_at"
                    tickFormatter={(v) =>
                      new Date(String(v)).toLocaleTimeString("uk-UA", {
                        hour: "2-digit",
                        minute: "2-digit",
                      })
                    }
                    tick={{ fontSize: 11 }}
                  />
                  <YAxis tick={{ fontSize: 11 }} />
                  <Tooltip />
                  <Legend />
                  <Line
                    type="monotone"
                    dataKey="value"
                    stroke="#6b7280"
                    name={t("admin.original") as string}
                    strokeWidth={1}
                    dot={false}
                  />
                  <Line
                    type="monotone"
                    dataKey="ewma"
                    stroke="var(--primary)"
                    name="EWMA"
                    strokeWidth={2}
                    dot={false}
                  />
                </LineChart>
              </ResponsiveContainer>
            </div>
          ) : null}
        </div>
      )}

      {/* Peaks */}
      {vm.activeTab === "peaks" && (
        <div>
          <div className="card" style={{ marginBottom: "24px" }}>
            <h3>{t("admin.peaks.title")}</h3>
            <div
              style={{
                display: "flex",
                gap: "16px",
                flexWrap: "wrap",
                marginTop: "16px",
                alignItems: "flex-end",
              }}
            >
              <div
                className="form-group"
                style={{ flex: "1 1 180px", marginBottom: 0 }}
              >
                <label>{t("admin.device")}</label>
                <select
                  value={vm.form.deviceId}
                  onChange={(e) => vm.setFormField("deviceId", e.target.value)}
                >
                  <option value="">{t("select.choose")}</option>
                  {vm.devices.map((d) => (
                    <option key={d.id} value={d.id}>
                      {d.name}
                    </option>
                  ))}
                </select>
              </div>

              <div
                className="form-group"
                style={{ flex: "0 0 160px", marginBottom: 0 }}
              >
                <label>{t("admin.peaks.window")}</label>
                <input
                  type="number"
                  min={1}
                  value={vm.form.clusterWindow}
                  onChange={(e) =>
                    vm.setFormField("clusterWindow", String(e.target.value))
                  }
                />
              </div>

              <button
                className="btn btn-primary"
                disabled={vm.isComputing}
                onClick={vm.runPeaks}
              >
                {vm.isComputing ? (
                  <>
                    <span className="spinner" /> {t("buttons.save")}
                  </>
                ) : (
                  <>
                    <Play size={16} /> {t("admin.peaks.find")}
                  </>
                )}
              </button>
            </div>
          </div>

          {Array.isArray(vm.results.peaks) && vm.results.peaks.length > 0 ? (
            <div className="card">
              <h3 style={{ marginBottom: "16px" }}>
                {t("admin.peaks.found").replace(
                  "{count}",
                  String(vm.results.peaks.length),
                )}
              </h3>
              <div style={{ overflowX: "auto" }}>
                <table className="table">
                  <thead>
                    <tr>
                      <th>#</th>
                      <th>{t("readings.value")}</th>
                      <th>{t("readings.time")}</th>
                    </tr>
                  </thead>
                  <tbody>
                    {vm.results.peaks.map((peak: unknown, i: number) => {
                      const p = peak as Record<string, unknown>;
                      const display =
                        p["value"] ?? p["max_value"] ?? String(peak);
                      return (
                        <tr key={i}>
                          <td>{i + 1}</td>
                          <td>
                            <strong style={{ color: "var(--danger)" }}>
                              {String(display)}
                            </strong>
                          </td>
                          <td style={{ fontSize: "13px" }}>
                            {p["measured_at"]
                              ? new Date(
                                  String(p["measured_at"]),
                                ).toLocaleString("uk-UA")
                              : "—"}
                          </td>
                        </tr>
                      );
                    })}
                  </tbody>
                </table>
              </div>
            </div>
          ) : (
            <div className="card">
              <div className="empty-state">
                <p>{t("admin.peaks.notFound")}</p>
              </div>
            </div>
          )}
        </div>
      )}

      {/* History */}
      {vm.activeTab === "history" && (
        <div className="card">
          <div
            style={{
              display: "flex",
              justifyContent: "space-between",
              alignItems: "center",
              marginBottom: "16px",
            }}
          >
            <h3>{t("admin.history.title")}</h3>
            <button
              className="btn btn-secondary btn-small"
              onClick={vm.refresh}
            >
              {t("buttons.refresh")}
            </button>
          </div>

          {vm.history.length > 0 ? (
            <div style={{ overflowX: "auto" }}>
              <table className="table">
                <thead>
                  <tr>
                    <th>{t("admin.device")}</th>
                    <th>{t("admin.type")}</th>
                    <th>{t("admin.value")}</th>
                    <th>{t("readings.time")}</th>
                  </tr>
                </thead>
                <tbody>
                  {vm.history.map((h) => (
                    <tr key={h.id}>
                      <td>{h.device_id ?? "—"}</td>
                      <td>
                        <span className="badge badge-secondary">
                          {h.metric_type}
                        </span>
                      </td>
                      <td>
                        <strong>
                          {typeof h.value === "object"
                            ? JSON.stringify(h.value).slice(0, 50)
                            : String(h.value)}
                        </strong>
                      </td>
                      <td style={{ fontSize: "13px" }}>
                        {new Date(h.created_at).toLocaleString("uk-UA")}
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          ) : (
            <div className="empty-state">
              <div className="empty-state-icon">📂</div>
              <p className="empty-state-text">{t("admin.history.empty")}</p>
            </div>
          )}
        </div>
      )}
    </div>
  );
}
