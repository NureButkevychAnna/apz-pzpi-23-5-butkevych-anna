import { useI18n } from "../i18n";
import { useDevicesViewModel } from "../viewmodels/useDevicesViewModel";
import {
  Plus,
  Edit2,
  Trash2,
  Copy,
  Check,
  AlertCircle,
  ToggleLeft,
  ToggleRight,
} from "lucide-react";

export function DevicesPage() {
  const vm = useDevicesViewModel();
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
        <h2 style={{ margin: 0 }}>{t("devices.myDevices")}</h2>
        <button className="btn btn-primary" onClick={vm.openCreate}>
          <Plus size={18} /> {t("devices.newDevice")}
        </button>
      </div>

      {/* Form */}
      {vm.showForm && (
        <div className="card" style={{ marginBottom: "24px" }}>
          <h3>
            {vm.editingDevice ? t("devices.edit") : t("devices.newDevice")}
          </h3>
          <form onSubmit={vm.save} style={{ marginTop: "16px" }}>
            <div className="form-group">
              <label>{t("readings.filters.device")}</label>
              <input
                type="text"
                value={vm.formName}
                onChange={(e) => vm.setFormName(e.target.value)}
                placeholder="Radiation Sensor Alpha"
                required
                autoFocus
              />
            </div>
            <div style={{ display: "flex", gap: "12px" }}>
              <button
                type="submit"
                className="btn btn-primary"
                disabled={vm.formSaving}
              >
                {vm.formSaving ? (
                  <>
                    <span className="spinner" /> {t("buttons.save")}
                  </>
                ) : vm.editingDevice ? (
                  t("buttons.update")
                ) : (
                  t("buttons.create")
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

      {vm.devices.length === 0 ? (
        <div className="card">
          <div className="empty-state">
            <div className="empty-state-icon">📭</div>
            <h3 className="empty-state-title">
              {t("subscriptions.emptyTitle")}
            </h3>
            <p className="empty-state-text">{t("subscriptions.emptyText")}</p>
            <button className="btn btn-primary" onClick={vm.openCreate}>
              <Plus size={18} /> {t("devices.createDevice")}
            </button>
          </div>
        </div>
      ) : (
        <div className="grid grid-2">
          {vm.devices.map((device) => (
            <div key={device.id} className="card">
              <div className="card-header">
                <div>
                  <h3 style={{ margin: 0, marginBottom: "6px" }}>
                    {device.name}
                  </h3>
                  <span
                    className={`device-status ${device.is_active ? "active" : "inactive"}`}
                    style={{ fontSize: "13px" }}
                  >
                    <span
                      style={{
                        width: 8,
                        height: 8,
                        borderRadius: "50%",
                        display: "inline-block",
                        backgroundColor: device.is_active
                          ? "var(--success)"
                          : "var(--danger)",
                        marginRight: 6,
                      }}
                    />
                    {device.is_active
                      ? t("devices.active")
                      : t("devices.inactive")}
                  </span>
                </div>
              </div>

              <div className="card-body">
                {/* Token */}
                <p
                  style={{
                    fontSize: "12px",
                    fontWeight: "600",
                    color: "var(--gray)",
                    marginBottom: "6px",
                  }}
                >
                  DEVICE TOKEN
                </p>
                <div
                  style={{
                    display: "flex",
                    gap: "8px",
                    alignItems: "center",
                    backgroundColor: "var(--gray-lighter)",
                    padding: "8px 12px",
                    borderRadius: "6px",
                    marginBottom: "12px",
                  }}
                >
                  <code
                    style={{
                      fontSize: "11px",
                      flex: 1,
                      wordBreak: "break-all",
                    }}
                  >
                    {device.device_token}
                  </code>
                  <button
                    className="btn btn-icon btn-secondary btn-small"
                    onClick={() => vm.copyToken(device.device_token)}
                    title={t("devices.copyToken")}
                  >
                    {vm.copiedToken === device.device_token ? (
                      <Check size={14} color="var(--success)" />
                    ) : (
                      <Copy size={14} />
                    )}
                  </button>
                </div>

                {device.location && (
                  <p style={{ fontSize: "13px", marginBottom: "8px" }}>
                    📍 {device.location.address}
                  </p>
                )}
                <p style={{ fontSize: "12px", color: "var(--gray)" }}>
                  {t("devices.created")}:{" "}
                  {new Date(device.created_at).toLocaleDateString("uk-UA")}
                </p>
              </div>
              <div className="card-footer">
                <button
                  className={`btn btn-small ${device.is_active ? "btn-secondary" : "btn-success"}`}
                  onClick={() => vm.toggleActive(device)}
                  title={
                    device.is_active
                      ? t("devices.deactivate")
                      : t("devices.activate")
                  }
                >
                  {device.is_active ? (
                    <ToggleRight size={16} />
                  ) : (
                    <ToggleLeft size={16} />
                  )}
                  {device.is_active
                    ? t("devices.deactivate")
                    : t("devices.activate")}
                </button>
                <button
                  className="btn btn-secondary btn-small"
                  onClick={() => vm.openEdit(device)}
                >
                  <Edit2 size={14} /> {t("devices.edit")}
                </button>
                <button
                  className="btn btn-danger btn-small"
                  onClick={() => {
                    if (
                      confirm(`${t("devices.confirmDelete")} "${device.name}"?`)
                    )
                      vm.remove(device.id);
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
