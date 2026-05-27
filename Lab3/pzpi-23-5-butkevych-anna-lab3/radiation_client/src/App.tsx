import { useState } from "react";
import { AuthProvider, useAuth } from "./context/AuthContext";
import { I18nProvider } from "./i18n.tsx";
import { Layout } from "./components/Layout";
import { LoginPage } from "./pages/LoginPage";
import { DashboardPage } from "./pages/DashboardPage.tsx";
import { DevicesPage } from "./pages/DevicesPage";
import { ReadingsPage } from "./pages/ReadingsPage";
import { AlertsPage } from "./pages/AlertsPage";
import { SubscriptionsPage } from "./pages/SubscriptionsPage";
import { AdminPage } from "./pages/AdminPage";
import { HealthPage } from "./pages/HealthPage";
import "./styles.css";

function AppContent() {
  const { user, isLoading } = useAuth();
  const [currentPage, setCurrentPage] = useState("dashboard");

  if (isLoading) {
    return (
      <div
        style={{
          display: "flex",
          alignItems: "center",
          justifyContent: "center",
          height: "100vh",
          background: "linear-gradient(135deg, #667eea 0%, #764ba2 100%)",
        }}
      >
        <div style={{ textAlign: "center" }}>
          <div className="spinner" style={{ margin: "0 auto 16px" }} />
          <p style={{ color: "white", fontSize: "18px" }}>Loading...</p>
        </div>
      </div>
    );
  }

  if (!user) {
    return (
      <LoginPage
        onSuccess={() => {
          setCurrentPage("dashboard");
        }}
      />
    );
  }

  const renderPage = () => {
    switch (currentPage) {
      case "dashboard":
        return <DashboardPage />;
      case "devices":
        return <DevicesPage />;
      case "readings":
        return <ReadingsPage />;
      case "alerts":
        return <AlertsPage />;
      case "subscriptions":
        return <SubscriptionsPage />;
      case "admin":
        return <AdminPage />;
      case "health":
        return <HealthPage />;
      default:
        return <DashboardPage />;
    }
  };

  return (
    <Layout currentPage={currentPage} onPageChange={setCurrentPage}>
      {renderPage()}
    </Layout>
  );
}

export default function App() {
  return (
    <AuthProvider>
      <I18nProvider>
        <AppContent />
      </I18nProvider>
    </AuthProvider>
  );
}
