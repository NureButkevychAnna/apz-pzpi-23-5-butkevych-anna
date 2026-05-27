import { useState, useEffect, useCallback } from "react";
import { getDevices, getDeviceHealth, computeDeviceHealth } from "../api";
import type { Device, DeviceHealth } from "../types";

export function useHealthViewModel() {
  const [devices, setDevices] = useState<Device[]>([]);
  const [health, setHealth] = useState<DeviceHealth | null>(null);
  const [selectedDeviceId, setSelectedDeviceId] = useState<string>("");
  const [isLoading, setIsLoading] = useState(true);
  const [isRecomputing, setIsRecomputing] = useState(false);
  const [error, setError] = useState("");

  const loadDevices = useCallback(async () => {
    await Promise.resolve();
    setIsLoading(true);
    setError("");
    try {
      const res = await getDevices();
      const devs = res.devices ?? [];
      setDevices(devs);
      if (devs.length > 0 && !selectedDeviceId) {
        setSelectedDeviceId(devs[0].id);
      }
    } catch (err) {
      setError(err instanceof Error ? err.message : "Failed to load devices");
    } finally {
      setIsLoading(false);
    }
  }, [selectedDeviceId]);

  const loadHealth = useCallback(async (deviceId: string) => {
    if (!deviceId) return;
    setError("");
    try {
      const res = await getDeviceHealth(deviceId);
      setHealth(res.health);
    } catch (err) {
      setHealth(null);
      setError(err instanceof Error ? err.message : "Failed to load health");
    }
  }, []);

  useEffect(() => {
    const id = setTimeout(() => void loadDevices(), 0);
    return () => clearTimeout(id);
  }, [loadDevices]);
  useEffect(() => {
    const id = setTimeout(() => {
      if (selectedDeviceId) void loadHealth(selectedDeviceId);
    }, 0);
    return () => clearTimeout(id);
  }, [selectedDeviceId, loadHealth]);

  const recompute = async () => {
    if (!selectedDeviceId) return;
    setIsRecomputing(true);
    setError("");
    try {
      const res = await computeDeviceHealth(selectedDeviceId);
      setHealth(res.health);
    } catch (err) {
      setError(err instanceof Error ? err.message : "Recompute failed");
    } finally {
      setIsRecomputing(false);
    }
  };

  const selectedDevice = devices.find((d) => d.id === selectedDeviceId);

  const getUptimeStatus = (pct: number) => {
    if (pct >= 95) return { label: "Відмінно", color: "#22c55e" };
    if (pct >= 80) return { label: "Добре", color: "#2563eb" };
    if (pct >= 50) return { label: "Задовільно", color: "#f59e0b" };
    return { label: "Критично", color: "#ef4444" };
  };

  const getBatteryStatus = (pct: number) => {
    if (pct > 50) return "#22c55e";
    if (pct > 20) return "#f59e0b";
    return "#ef4444";
  };

  return {
    devices,
    health,
    selectedDeviceId,
    setSelectedDeviceId,
    selectedDevice,
    isLoading,
    isRecomputing,
    error,
    recompute,
    getUptimeStatus,
    getBatteryStatus,
    refresh: () => loadHealth(selectedDeviceId),
  };
}
