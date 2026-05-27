import { useState, useEffect, useCallback } from "react";
import { getDevices, createDevice, updateDevice, deleteDevice } from "../api";
import type { Device } from "../types";

export function useDevicesViewModel() {
  const [devices, setDevices] = useState<Device[]>([]);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState("");

  // Form state
  const [showForm, setShowForm] = useState(false);
  const [editingDevice, setEditingDevice] = useState<Device | null>(null);
  const [formName, setFormName] = useState("");
  const [formSaving, setFormSaving] = useState(false);

  // Copy token feedback
  const [copiedToken, setCopiedToken] = useState<string | null>(null);

  const load = useCallback(async () => {
    await Promise.resolve();
    setIsLoading(true);
    setError("");
    try {
      const res = await getDevices();
      setDevices(res.devices ?? []);
    } catch (err) {
      setError(err instanceof Error ? err.message : "Failed to load devices");
    } finally {
      setIsLoading(false);
    }
  }, []);

  useEffect(() => {
    const id = setTimeout(() => void load(), 0);
    return () => clearTimeout(id);
  }, [load]);

  const openCreate = () => {
    setEditingDevice(null);
    setFormName("");
    setShowForm(true);
  };

  const openEdit = (device: Device) => {
    setEditingDevice(device);
    setFormName(device.name);
    setShowForm(true);
  };

  const closeForm = () => {
    setShowForm(false);
    setEditingDevice(null);
    setFormName("");
    setError("");
  };

  const save = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!formName.trim()) {
      setError("Введіть назву пристрою");
      return;
    }
    setFormSaving(true);
    setError("");
    try {
      if (editingDevice) {
        await updateDevice(editingDevice.id, { name: formName });
      } else {
        await createDevice(formName);
      }
      await load();
      closeForm();
    } catch (err) {
      setError(err instanceof Error ? err.message : "Failed to save");
    } finally {
      setFormSaving(false);
    }
  };

  const toggleActive = async (device: Device) => {
    setError("");
    try {
      await updateDevice(device.id, { is_active: !device.is_active });
      await load();
    } catch (err) {
      setError(err instanceof Error ? err.message : "Failed to update");
    }
  };

  const remove = async (id: string) => {
    setError("");
    try {
      await deleteDevice(id);
      await load();
    } catch (err) {
      setError(err instanceof Error ? err.message : "Failed to delete");
    }
  };

  const copyToken = (token: string) => {
    navigator.clipboard.writeText(token);
    setCopiedToken(token);
    setTimeout(() => setCopiedToken(null), 2000);
  };

  return {
    devices,
    isLoading,
    error,
    showForm,
    editingDevice,
    formName,
    setFormName,
    formSaving,
    openCreate,
    openEdit,
    closeForm,
    save,
    toggleActive,
    remove,
    copiedToken,
    copyToken,
    refresh: load,
  };
}
