import { useState, useEffect, useCallback } from "react";
import { getDevices, getReadings } from "../api";
import type { Device, Reading } from "../types";

export interface ReadingStats {
  latest: string;
  max: string;
  min: string;
  avg: string;
  count: number;
}

export function useReadingsViewModel() {
  const [devices, setDevices] = useState<Device[]>([]);
  const [readings, setReadings] = useState<Reading[]>([]);
  const [selectedDeviceId, setSelectedDeviceId] = useState<string>("");
  const [isLoading, setIsLoading] = useState(true);
  const [isLoadingReadings, setIsLoadingReadings] = useState(false);
  const [error, setError] = useState("");
  const [sinceDate, setSinceDate] = useState<string>("");
  const [limit, setLimit] = useState<number>(200);

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

  const loadReadings = useCallback(
    async (deviceId: string) => {
      if (!deviceId) return;
      await Promise.resolve();
      setIsLoadingReadings(true);
      setError("");
      try {
        const res = await getReadings(
          deviceId,
          limit,
          sinceDate ? new Date(sinceDate).toISOString() : undefined,
        );
        setReadings(res.readings ?? []);
      } catch (err) {
        setError(
          err instanceof Error ? err.message : "Failed to load readings",
        );
      } finally {
        setIsLoadingReadings(false);
      }
    },
    [limit, sinceDate],
  );

  useEffect(() => {
    const id = setTimeout(() => void loadDevices(), 0);
    return () => clearTimeout(id);
  }, [loadDevices]);
  useEffect(() => {
    const id = setTimeout(() => {
      if (selectedDeviceId) void loadReadings(selectedDeviceId);
    }, 0);
    return () => clearTimeout(id);
  }, [selectedDeviceId, loadReadings]);

  const stats: ReadingStats =
    readings.length > 0
      ? {
          latest: readings[0].value.toFixed(3),
          max: Math.max(...readings.map((r) => r.value)).toFixed(3),
          min: Math.min(...readings.map((r) => r.value)).toFixed(3),
          avg: (
            readings.reduce((s, r) => s + r.value, 0) / readings.length
          ).toFixed(3),
          count: readings.length,
        }
      : { latest: "—", max: "—", min: "—", avg: "—", count: 0 };

  const chartData = [...readings].reverse().map((r) => ({
    time: new Date(r.measured_at).toLocaleTimeString("uk-UA", {
      month: "short",
      day: "numeric",
      hour: "2-digit",
      minute: "2-digit",
    }),
    value: r.value,
  }));

  const selectedDevice = devices.find((d) => d.id === selectedDeviceId);

  return {
    devices,
    readings,
    selectedDeviceId,
    setSelectedDeviceId,
    selectedDevice,
    stats,
    chartData,
    isLoading,
    isLoadingReadings,
    error,
    sinceDate,
    setSinceDate,
    limit,
    setLimit,
    refresh: () => loadReadings(selectedDeviceId),
  };
}
