import React, { useState } from "react";
import { useAuth } from "../context/AuthContext";
import { LogOut, Menu, X } from "lucide-react";
import { useI18n } from "../i18n";

interface LayoutProps {
  children: React.ReactNode;
  currentPage: string;
  onPageChange: (page: string) => void;
}

export function Layout({ children, currentPage, onPageChange }: LayoutProps) {
  const { user, logout } = useAuth();
  const [sidebarOpen, setSidebarOpen] = useState(false);

  const { t, lang, setLang } = useI18n();

  const menuItems = [
    { id: "dashboard", label: t("menu.dashboard"), icon: "📊" },
    { id: "devices", label: t("menu.devices"), icon: "📱" },
    { id: "readings", label: t("menu.readings"), icon: "📈" },
    { id: "alerts", label: t("menu.alerts"), icon: "🚨" },
    { id: "subscriptions", label: t("menu.subscriptions"), icon: "🔔" },
  ];

  const adminItems = [
    { id: "admin", label: t("menu.admin"), icon: "⚙️" },
    { id: "health", label: t("menu.health"), icon: "❤️" },
  ];

  const handleNavClick = (pageId: string) => {
    onPageChange(pageId);
    setSidebarOpen(false);
  };

  const handleLogout = () => {
    logout();
  };

  return (
    <div className="app-container">
      {/* Sidebar */}
      <aside className={`sidebar ${sidebarOpen ? "open" : ""}`}>
        <div className="sidebar-header">☢️ RadiationMonitor</div>

        <nav className="sidebar-nav">
          {menuItems.map((item) => (
            <button
              key={item.id}
              className={`sidebar-item ${currentPage === item.id ? "active" : ""}`}
              onClick={() => handleNavClick(item.id)}
            >
              <span>{item.icon}</span>
              <span>{item.label}</span>
            </button>
          ))}
        </nav>

        {user?.role === "admin" && (
          <div className="sidebar-section">
            <div className="sidebar-section-title">Адміністрація</div>
            <nav className="sidebar-nav">
              {adminItems.map((item) => (
                <button
                  key={item.id}
                  className={`sidebar-item ${currentPage === item.id ? "active" : ""}`}
                  onClick={() => handleNavClick(item.id)}
                >
                  <span>{item.icon}</span>
                  <span>{item.label}</span>
                </button>
              ))}
            </nav>
          </div>
        )}
      </aside>

      {/* Main Content */}
      <main className="main-content">
        {/* Header */}
        <header className="header">
          <div style={{ display: "flex", alignItems: "center", gap: "16px" }}>
            <button
              className="btn btn-icon mobile-toggle"
              onClick={() => setSidebarOpen(!sidebarOpen)}
            >
              {sidebarOpen ? <X size={20} /> : <Menu size={20} />}
            </button>
            <h2 style={{ margin: 0 }}>
              {menuItems.find((item) => item.id === currentPage)?.label ||
                adminItems.find((item) => item.id === currentPage)?.label ||
                t("app.title")}
            </h2>
          </div>

          <div style={{ display: "flex", alignItems: "center", gap: "16px" }}>
            <div>
              <select
                value={lang}
                onChange={(e) => setLang(e.target.value as any)}
                style={{ marginRight: 8 }}
              >
                <option value="uk">Українська</option>
                <option value="en">English</option>
              </select>
            </div>
            <div>
              <p
                style={{ marginBottom: 0, fontSize: "14px", fontWeight: "600" }}
              >
                {user?.name}
              </p>
              <p
                style={{
                  marginBottom: 0,
                  fontSize: "12px",
                  color: "var(--gray)",
                }}
              >
                {user?.role === "admin" ? t("role.admin") : t("role.user")}
              </p>
            </div>
            <button
              className="btn btn-icon btn-secondary"
              onClick={handleLogout}
            >
              <LogOut size={20} />
            </button>
          </div>
        </header>

        {/* Page Content */}
        <div className="content">{children}</div>
      </main>
    </div>
  );
}
