import { useState, useEffect, useCallback } from "react";
import {
  getDevices,
  getComputedReadings,
  computeCumulativeDose,
  computeEWMA,
  detectPeaks,
} from "../api";
import type { Device, ComputedReading } from "../types";

export type AdminTab = "cumulative" | "ewma" | "peaks" | "history";

export interface ComputeForm {
  deviceId: string;
  fromDate: string;
  toDate: string;
  alpha: string;
  clusterWindow: string;
}

export function useAdminViewModel() {
  const [devices, setDevices] = useState<Device[]>([]);
  const [history, setHistory] = useState<ComputedReading[]>([]);
  const [isLoading, setIsLoading] = useState(true);
  const [isComputing, setIsComputing] = useState(false);
  const [error, setError] = useState("");
  const [activeTab, setActiveTab] = useState<AdminTab>("cumulative");

  const [form, setForm] = useState<ComputeForm>(() => ({
    deviceId: "",
    fromDate: new Date(Date.now() - 7 * 86400000).toISOString().split("T")[0],
    toDate: new Date().toISOString().split("T")[0],
    alpha: "0.3",
    clusterWindow: "30",
  }));

  const [results, setResults] = useState<Record<string, unknown>>({});

  const load = useCallback(async () => {
    await Promise.resolve();
    setIsLoading(true);
    setError("");
    try {
      const [devRes, histRes] = await Promise.all([
        getDevices(),
        getComputedReadings(),
      ]);
      const devs = devRes.devices ?? [];
      setDevices(devs);
      setHistory(histRes.data ?? []);
      if (devs.length > 0) {
        setForm((prev) => ({ ...prev, deviceId: prev.deviceId || devs[0].id }));
      }
    } catch (err) {
      setError(err instanceof Error ? err.message : "Failed to load");
    } finally {
      setIsLoading(false);
    }
  }, []);

  useEffect(() => {
    const id = setTimeout(() => void load(), 0);
    return () => clearTimeout(id);
  }, [load]);

  const setFormField = <K extends keyof ComputeForm>(
    key: K,
    value: ComputeForm[K],
  ) => setForm((prev) => ({ ...prev, [key]: value }));

  const from = () => new Date(form.fromDate).toISOString();
  const to = () => new Date(form.toDate).toISOString();

  const runCumulative = async () => {
    if (!form.deviceId) {
      setError("Оберіть пристрій");
      return;
    }
    setIsComputing(true);
    setError("");
    try {
      const res = await computeCumulativeDose(form.deviceId, from(), to());
      setResults((prev) => ({ ...prev, cumulative: res.result }));
      await refreshHistory();
    } catch (err) {
      setError(err instanceof Error ? err.message : "Computation failed");
    } finally {
      setIsComputing(false);
    }
  };

  const runEWMA = async () => {
    if (!form.deviceId) {
      setError("Оберіть пристрій");
      return;
    }
    setIsComputing(true);
    setError("");
    try {
      const res = await computeEWMA(
        form.deviceId,
        from(),
        to(),
        parseFloat(form.alpha),
      );
      setResults((prev) => ({ ...prev, ewma: res.result }));
      await refreshHistory();
    } catch (err) {
      setError(err instanceof Error ? err.message : "Computation failed");
    } finally {
      setIsComputing(false);
    }
  };

  const runPeaks = async () => {
    if (!form.deviceId) {
      setError("Оберіть пристрій");
      return;
    }
    setIsComputing(true);
    setError("");
    try {
      const res = await detectPeaks(
        form.deviceId,
        from(),
        to(),
        parseInt(form.clusterWindow),
      );
      setResults((prev) => ({ ...prev, peaks: res.result }));
      await refreshHistory();
    } catch (err) {
      setError(err instanceof Error ? err.message : "Computation failed");
    } finally {
      setIsComputing(false);
    }
  };

  const refreshHistory = async () => {
    try {
      const res = await getComputedReadings(form.deviceId);
      setHistory(res.data ?? []);
    } catch {
      /* silent */
    }
  };

  const filteredHistory = form.deviceId
    ? history.filter((h) => h.device_id === form.deviceId)
    : history;

  return {
    devices,
    history: filteredHistory,
    isLoading,
    isComputing,
    error,
    activeTab,
    setActiveTab,
    form,
    setFormField,
    results,
    runCumulative,
    runEWMA,
    runPeaks,
    refresh: load,
  };
}
