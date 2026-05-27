import { useState, useEffect, useCallback } from "react";
import { getAlerts, getAlert, acknowledgeAlert } from "../api";
import type { Alert } from "../types";

export type AlertLevelFilter = "all" | "warning" | "danger" | "critical";
export type AlertAckFilter = "all" | "acknowledged" | "unacknowledged";

export function useAlertsViewModel() {
  const [alerts, setAlerts] = useState<Alert[]>([]);
  const [selectedAlert, setSelectedAlert] = useState<Alert | null>(null);
  const [isLoading, setIsLoading] = useState(true);
  const [isAcking, setIsAcking] = useState<string | null>(null);
  const [error, setError] = useState("");
  const [levelFilter, setLevelFilter] = useState<AlertLevelFilter>("all");
  const [ackFilter, setAckFilter] = useState<AlertAckFilter>("unacknowledged");

  const load = useCallback(async () => {
    await Promise.resolve();
    setIsLoading(true);
    setError("");
    try {
      const level = levelFilter === "all" ? undefined : levelFilter;
      const acknowledged =
        ackFilter === "all" ? undefined : ackFilter === "acknowledged";
      const res = await getAlerts(level, acknowledged, 100);
      setAlerts(res.alerts ?? []);
    } catch (err) {
      setError(err instanceof Error ? err.message : "Failed to load alerts");
    } finally {
      setIsLoading(false);
    }
  }, [levelFilter, ackFilter]);

  useEffect(() => {
    const id = setTimeout(() => void load(), 0);
    return () => clearTimeout(id);
  }, [load]);

  const viewDetail = async (id: string) => {
    setError("");
    try {
      const res = await getAlert(id);
      setSelectedAlert(res.alert);
    } catch (err) {
      setError(err instanceof Error ? err.message : "Failed to load alert");
    }
  };

  const ack = async (id: string) => {
    setIsAcking(id);
    setError("");
    try {
      await acknowledgeAlert(id);
      // update in-place
      setAlerts((prev) =>
        prev.map((a) =>
          a.id === id
            ? {
                ...a,
                acknowledged: true,
                acknowledged_at: new Date().toISOString(),
              }
            : a,
        ),
      );
      if (selectedAlert?.id === id) {
        setSelectedAlert((prev) =>
          prev
            ? {
                ...prev,
                acknowledged: true,
                acknowledged_at: new Date().toISOString(),
              }
            : prev,
        );
      }
    } catch (err) {
      setError(err instanceof Error ? err.message : "Failed to acknowledge");
    } finally {
      setIsAcking(null);
    }
  };

  const closeDetail = () => setSelectedAlert(null);

  const levelCount = {
    warning: alerts.filter((a) => a.level === "warning").length,
    danger: alerts.filter((a) => a.level === "danger").length,
    critical: alerts.filter((a) => a.level === "critical").length,
  };

  return {
    alerts,
    selectedAlert,
    isLoading,
    isAcking,
    error,
    levelFilter,
    setLevelFilter,
    ackFilter,
    setAckFilter,
    levelCount,
    viewDetail,
    ack,
    closeDetail,
    refresh: load,
  };
}
