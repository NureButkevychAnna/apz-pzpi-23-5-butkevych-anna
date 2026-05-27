import { useState, useEffect, useCallback } from "react";
import {
  getSubscriptions,
  createSubscription,
  updateSubscription,
  deleteSubscription,
} from "../api";
import type { Subscription } from "../types";

export type ChannelType = "email" | "sms" | "push";

export interface SubscriptionForm {
  channel: ChannelType;
  levels: string[];
  threshold: string;
  active: boolean;
}

const defaultForm: SubscriptionForm = {
  channel: "email",
  levels: ["critical", "danger"],
  threshold: "5.0",
  active: true,
};

export function useSubscriptionsViewModel() {
  const [subscriptions, setSubscriptions] = useState<Subscription[]>([]);
  const [isLoading, setIsLoading] = useState(true);
  const [isSaving, setIsSaving] = useState(false);
  const [error, setError] = useState("");
  const [showForm, setShowForm] = useState(false);
  const [form, setForm] = useState<SubscriptionForm>(defaultForm);

  const load = useCallback(async () => {
    await Promise.resolve();
    setIsLoading(true);
    setError("");
    try {
      const res = await getSubscriptions();
      setSubscriptions(res.subscriptions ?? []);
    } catch (err) {
      setError(
        err instanceof Error ? err.message : "Failed to load subscriptions",
      );
    } finally {
      setIsLoading(false);
    }
  }, []);

  useEffect(() => {
    const id = setTimeout(() => void load(), 0);
    return () => clearTimeout(id);
  }, [load]);

  const openForm = () => {
    setForm(defaultForm);
    setShowForm(true);
    setError("");
  };
  const closeForm = () => {
    setShowForm(false);
    setError("");
  };

  const setFormField = <K extends keyof SubscriptionForm>(
    key: K,
    value: SubscriptionForm[K],
  ) => setForm((prev) => ({ ...prev, [key]: value }));

  const toggleLevel = (level: string) =>
    setForm((prev) => ({
      ...prev,
      levels: prev.levels.includes(level)
        ? prev.levels.filter((l) => l !== level)
        : [...prev.levels, level],
    }));

  const create = async (e: React.FormEvent) => {
    e.preventDefault();
    if (form.levels.length === 0) {
      setError("Оберіть хоча б один рівень");
      return;
    }
    setIsSaving(true);
    setError("");
    try {
      await createSubscription(
        form.channel,
        { levels: form.levels, threshold: parseFloat(form.threshold) },
        form.active,
      );
      await load();
      closeForm();
    } catch (err) {
      setError(err instanceof Error ? err.message : "Failed to create");
    } finally {
      setIsSaving(false);
    }
  };

  const toggleActive = async (sub: Subscription) => {
    setError("");
    try {
      await updateSubscription(sub.id, { active: !sub.active });
      setSubscriptions((prev) =>
        prev.map((s) => (s.id === sub.id ? { ...s, active: !s.active } : s)),
      );
    } catch (err) {
      setError(err instanceof Error ? err.message : "Failed to update");
    }
  };

  const remove = async (id: string) => {
    setError("");
    try {
      await deleteSubscription(id);
      setSubscriptions((prev) => prev.filter((s) => s.id !== id));
    } catch (err) {
      setError(err instanceof Error ? err.message : "Failed to delete");
    }
  };

  return {
    subscriptions,
    isLoading,
    isSaving,
    error,
    showForm,
    form,
    openForm,
    closeForm,
    setFormField,
    toggleLevel,
    create,
    toggleActive,
    remove,
    refresh: load,
  };
}
