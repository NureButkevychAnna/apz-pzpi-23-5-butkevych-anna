import { useState, useEffect, useCallback } from "react";
import { getDevices, getReadings, getAlerts } from "../api";
import type { Device, Reading, Alert } from "../types";

export interface DashboardStats {
  totalDevices: number;
  activeDevices: number;
  unacknowledgedAlerts: number;
  criticalAlerts: number;
  avgRadiation: string;
}

export function useDashboardViewModel() {
  const [devices, setDevices] = useState<Device[]>([]);
  const [readings, setReadings] = useState<Reading[]>([]);
  const [alerts, setAlerts] = useState<Alert[]>([]);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState("");

  const load = useCallback(async () => {
    await Promise.resolve();
    setIsLoading(true);
    setError("");
    try {
      const [devicesRes, readingsRes, alertsRes] = await Promise.all([
        getDevices(),
        getReadings(undefined, 100),
        getAlerts(undefined, false, 20),
      ]);
      setDevices(devicesRes.devices ?? []);
      setReadings(readingsRes.readings ?? []);
      setAlerts(alertsRes.alerts ?? []);
    } catch (err) {
      setError(err instanceof Error ? err.message : "Failed to load dashboard");
    } finally {
      setIsLoading(false);
    }
  }, []);

  useEffect(() => {
    const id = setTimeout(() => void load(), 0);
    return () => clearTimeout(id);
  }, [load]);

  const stats: DashboardStats = {
    totalDevices: devices.length,
    activeDevices: devices.filter((d) => d.is_active).length,
    unacknowledgedAlerts: alerts.length,
    criticalAlerts: alerts.filter((a) => a.level === "critical").length,
    avgRadiation:
      readings.length > 0
        ? (readings.reduce((s, r) => s + r.value, 0) / readings.length).toFixed(
            3,
          )
        : "—",
  };

  // Last 50 readings in chronological order for chart
  const chartData = [...readings]
    .reverse()
    .slice(-50)
    .map((r) => ({
      time: new Date(r.measured_at).toLocaleTimeString("uk-UA", {
        hour: "2-digit",
        minute: "2-digit",
      }),
      value: r.value,
      deviceName: r.device?.name ?? "Unknown",
    }));

  // Latest 5 unacknowledged alerts for the panel
  const recentAlerts = alerts.slice(0, 5);

  return {
    devices,
    readings,
    alerts,
    stats,
    chartData,
    recentAlerts,
    isLoading,
    error,
    refresh: load,
  };
}
