import { useAuthViewModel } from "../viewmodels/useAuthViewModel";
import { useI18n } from "../i18n";

export function LoginPage({ onSuccess }: { onSuccess: () => void }) {
  const vm = useAuthViewModel();
  const { t } = useI18n();

  return (
    <div
      style={{
        display: "flex",
        alignItems: "center",
        justifyContent: "center",
        height: "100vh",
        background:
          "linear-gradient(135deg, #1e3a8a 0%, #1e40af 50%, #1d4ed8 100%)",
      }}
    >
      <div
        className="card"
        style={{ maxWidth: "420px", width: "100%", margin: "0 16px" }}
      >
        <div style={{ textAlign: "center", marginBottom: "32px" }}>
          <div style={{ fontSize: "56px", marginBottom: "12px" }}>☢️</div>
          <h2 style={{ fontSize: "26px", marginBottom: "4px" }}>
            {t("login.title")}
          </h2>
          <p>
            {vm.isRegister ? t("login.registerCreate") : t("login.loginPrompt")}
          </p>
        </div>

        {vm.error && (
          <div className="alert alert-error" style={{ marginBottom: "20px" }}>
            {vm.error}
          </div>
        )}

        <form onSubmit={(e) => vm.handleSubmit(e, onSuccess)}>
          {vm.isRegister && (
            <div className="form-group">
              <label>{t("login.name")}</label>
              <input
                type="text"
                value={vm.name}
                onChange={(e) => vm.setName(e.target.value)}
                placeholder={t("login.name")}
                required
                minLength={2}
              />
            </div>
          )}
          <div className="form-group">
            <label>{t("login.email")}</label>
            <input
              type="email"
              value={vm.email}
              onChange={(e) => vm.setEmail(e.target.value)}
              placeholder="example@gmail.com"
              required
            />
          </div>
          <div className="form-group">
            <label>{t("login.password")}</label>
            <input
              type="password"
              value={vm.password}
              onChange={(e) => vm.setPassword(e.target.value)}
              placeholder="••••••••"
              required
              minLength={6}
            />
          </div>

          <button
            type="submit"
            className="btn btn-primary btn-block"
            disabled={vm.isLoading}
          >
            {vm.isLoading ? (
              <>
                <span className="spinner" /> {t("login.submit.loading")}
              </>
            ) : vm.isRegister ? (
              t("login.submit.register")
            ) : (
              t("login.submit.login")
            )}
          </button>
        </form>

        <p style={{ textAlign: "center", marginTop: "20px", fontSize: "14px" }}>
          {vm.isRegister
            ? t("login.haveAccount") + " "
            : t("login.noAccount") + " "}
          <button
            onClick={vm.toggleMode}
            style={{
              background: "none",
              border: "none",
              color: "var(--primary)",
              cursor: "pointer",
              fontWeight: "600",
              fontSize: "14px",
            }}
          >
            {vm.isRegister
              ? t("login.switchToLogin")
              : t("login.switchToRegister")}
          </button>
        </p>
      </div>
    </div>
  );
}
