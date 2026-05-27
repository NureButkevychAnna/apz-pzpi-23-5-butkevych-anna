import { useSubscriptionsViewModel } from "../viewmodels/useSubscriptionsViewModel";
import { Plus, Trash2, AlertCircle, Bell, BellOff } from "lucide-react";
import { useI18n } from "../i18n";

const LEVELS = ["warning", "danger", "critical"] as const;
const LEVEL_LABELS: Record<string, string> = {
  warning: "⚠️ Попередження",
  danger: "🔴 Небезпека",
  critical: "🚨 Критично",
};

export function SubscriptionsPage() {
  const vm = useSubscriptionsViewModel();
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

      <div
        style={{
          display: "flex",
          justifyContent: "space-between",
          alignItems: "center",
          marginBottom: "24px",
        }}
      >
        <h2 style={{ margin: 0 }}>{t("subscriptions.title")}</h2>
        <button className="btn btn-primary" onClick={() => vm.openForm()}>
          <Plus size={18} /> Нова підписка
        </button>
      </div>

      {vm.showForm && (
        <div className="card" style={{ marginBottom: "24px" }}>
          <h3>{t("subscriptions.new")}</h3>
          <form onSubmit={vm.create} style={{ marginTop: "16px" }}>
            <div className="form-group">
              <label>{t("subscriptions.channel")}</label>
              <select
                value={vm.form.channel}
                onChange={(e) =>
                  vm.setFormField("channel", e.target.value as any)
                }
                required
              >
                <option value="">{t("subscriptions.selectChannel")}</option>
                <option value="email">📧 Email</option>
                <option value="sms">📱 SMS</option>
                <option value="push">🔔 Push</option>
              </select>
            </div>

            <div className="form-group">
              <label>{t("subscriptions.levels")}</label>
              <div
                style={{
                  display: "flex",
                  gap: "12px",
                  flexWrap: "wrap",
                  marginTop: "6px",
                }}
              >
                {LEVELS.map((level) => (
                  <label
                    key={level}
                    style={{
                      display: "flex",
                      alignItems: "center",
                      gap: "6px",
                      cursor: "pointer",
                      fontSize: "14px",
                    }}
                  >
                    <input
                      type="checkbox"
                      checked={vm.form.levels.includes(level)}
                      onChange={() => vm.toggleLevel(level)}
                    />
                    {LEVEL_LABELS[level]}
                  </label>
                ))}
              </div>
            </div>

            <div className="form-group">
              <label>{t("subscriptions.threshold")}</label>
              <input
                type="number"
                step="0.01"
                min="0"
                value={vm.form.threshold}
                onChange={(e) =>
                  vm.setFormField("threshold", String(e.target.value))
                }
                placeholder="0.50"
              />
            </div>

            <div style={{ display: "flex", gap: "12px" }}>
              <button
                type="submit"
                className="btn btn-primary"
                disabled={vm.isSaving}
              >
                {vm.isSaving ? (
                  <>
                    <span className="spinner" /> {t("buttons.save")}
                  </>
                ) : (
                  t("subscriptions.create")
                )}
              </button>
              <button
                type="button"
                className="btn btn-secondary"
                onClick={vm.closeForm}
              >
                {t("buttons.cancel")}
              </button>
            </div>
          </form>
        </div>
      )}

      {vm.subscriptions.length === 0 ? (
        <div className="card">
          <div className="empty-state">
            <div className="empty-state-icon">🔕</div>
            <h3 className="empty-state-title">
              {t("subscriptions.emptyTitle")}
            </h3>
            <p className="empty-state-text">{t("subscriptions.emptyText")}</p>
            <button className="btn btn-primary" onClick={() => vm.openForm()}>
              <Plus size={18} /> {t("subscriptions.created")}
            </button>
          </div>
        </div>
      ) : (
        <div className="grid grid-2">
          {vm.subscriptions.map((sub) => (
            <div key={sub.id} className="card">
              <div
                style={{
                  display: "flex",
                  justifyContent: "space-between",
                  alignItems: "flex-start",
                  marginBottom: "12px",
                }}
              >
                <div>
                  <div
                    style={{
                      display: "flex",
                      alignItems: "center",
                      gap: "10px",
                    }}
                  >
                    <span style={{ fontSize: "24px" }}>
                      {sub.channel === "email"
                        ? "📧"
                        : sub.channel === "sms"
                          ? "📱"
                          : "🔗"}
                    </span>
                    <div>
                      <strong
                        style={{
                          fontSize: "15px",
                          textTransform: "capitalize",
                        }}
                      >
                        {sub.channel}
                      </strong>
                      <p
                        style={{
                          margin: 0,
                          fontSize: "13px",
                          color: "var(--gray)",
                        }}
                      >
                        {sub.channel}
                      </p>
                    </div>
                  </div>
                </div>
                <span
                  className={`badge ${sub.active ? "badge-success" : "badge-secondary"}`}
                >
                  {sub.active ? "Активна" : "Вимкнена"}
                </span>
              </div>

              {(() => {
                const criteria = sub.criteria as
                  | { levels?: string[]; threshold?: string | number }
                  | undefined;
                if (!criteria) return null;
                return (
                  <>
                    {criteria.levels && criteria.levels.length > 0 && (
                      <div style={{ marginBottom: "10px" }}>
                        <p
                          style={{
                            fontSize: "12px",
                            color: "var(--gray)",
                            marginBottom: "4px",
                          }}
                        >
                          Рівні:
                        </p>
                        <div
                          style={{
                            display: "flex",
                            gap: "6px",
                            flexWrap: "wrap",
                          }}
                        >
                          {criteria.levels.map((l) => (
                            <span
                              key={l}
                              className={`badge ${l === "warning" ? "badge-warning" : "badge-danger"}`}
                              style={{ fontSize: "11px" }}
                            >
                              {LEVEL_LABELS[l] ?? l}
                            </span>
                          ))}
                        </div>
                      </div>
                    )}

                    {criteria.threshold != null && (
                      <p style={{ fontSize: "13px", marginBottom: "10px" }}>
                        Поріг: <strong>{criteria.threshold} µSv/h</strong>
                      </p>
                    )}
                  </>
                );
              })()}

              <p
                style={{
                  fontSize: "12px",
                  color: "var(--gray)",
                  marginBottom: "12px",
                }}
              >
                Створено: {new Date(sub.created_at).toLocaleDateString("uk-UA")}
              </p>

              <div style={{ display: "flex", gap: "8px" }}>
                <button
                  className={`btn btn-small ${sub.active ? "btn-secondary" : "btn-success"}`}
                  onClick={() => vm.toggleActive(sub)}
                >
                  {sub.active ? (
                    <>
                      <BellOff size={14} /> Вимкнути
                    </>
                  ) : (
                    <>
                      <Bell size={14} /> Увімкнути
                    </>
                  )}
                </button>
                <button
                  className="btn btn-danger btn-small"
                  onClick={() => {
                    if (confirm("Видалити підписку?")) vm.remove(sub.id);
                  }}
                >
                  <Trash2 size={14} />
                </button>
              </div>
            </div>
          ))}
        </div>
      )}
    </div>
  );
}
