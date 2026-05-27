import type {
  AuthResponse,
  Device,
  Reading,
  Alert,
  Subscription,
  ComputedReading,
  DeviceHealth,
} from "./types";

const API_URL = "http://147.15.143.53:3001/api";

// Helper function for API calls
async function apiCall<T>(
  endpoint: string,
  options: RequestInit = {},
): Promise<T> {
  const token = localStorage.getItem("token");
  const optHeaders = options.headers as Record<string, string> | undefined;
  const headers: Record<string, string> = {
    "Content-Type": "application/json",
    ...optHeaders,
  };

  if (token && !(optHeaders && optHeaders["Device-Token"])) {
    headers["Authorization"] = `Bearer ${token}`;
  }

  const res = await fetch(`${API_URL}${endpoint}`, {
    ...options,
    headers,
  });

  if (!res.ok) {
    const error = await res.json().catch(() => ({}));
    throw new Error(error.error || `API Error: ${res.status}`);
  }

  return res.json();
}

// =====================
// AUTHENTICATION
// =====================
export async function register(
  email: string,
  password: string,
  name: string,
): Promise<AuthResponse> {
  return apiCall("/auth/register", {
    method: "POST",
    body: JSON.stringify({ email, password, name }),
  });
}

export async function login(
  email: string,
  password: string,
): Promise<AuthResponse> {
  return apiCall("/auth/login", {
    method: "POST",
    body: JSON.stringify({ email, password }),
  });
}

// =====================
// DEVICES
// =====================
export async function getDevices(): Promise<{ devices: Device[] }> {
  return apiCall("/devices");
}

export async function getDevice(id: string): Promise<{ device: Device }> {
  return apiCall(`/devices/${id}`);
}

export async function createDevice(
  name: string,
  location_id?: string,
): Promise<{ message: string; device: Device }> {
  return apiCall("/devices", {
    method: "POST",
    body: JSON.stringify({ name, location_id }),
  });
}

export async function updateDevice(
  id: string,
  updates: Partial<Device>,
): Promise<{ message: string; device: Device }> {
  return apiCall(`/devices/${id}`, {
    method: "PUT",
    body: JSON.stringify(updates),
  });
}

export async function deleteDevice(id: string): Promise<{ message: string }> {
  return apiCall(`/devices/${id}`, {
    method: "DELETE",
  });
}

// =====================
// READINGS
// =====================
export async function getReadings(
  device_id?: string,
  limit: number = 100,
  since?: string,
): Promise<{ readings: Reading[] }> {
  const params = new URLSearchParams();
  if (device_id) params.append("device_id", device_id);
  if (limit) params.append("limit", String(limit));
  if (since) params.append("since", since);

  return apiCall(`/readings${params.toString() ? "?" + params : ""}`);
}

export async function submitReading(
  deviceToken: string,
  measured_at: string,
  value: number,
  unit: string,
  metadata?: Record<string, unknown>,
): Promise<{
  message: string;
  reading: Reading;
  alert?: Alert;
}> {
  return apiCall("/readings", {
    method: "POST",
    headers: {
      "Device-Token": deviceToken,
    },
    body: JSON.stringify({ measured_at, value, unit, metadata }),
  });
}

// =====================
// ALERTS
// =====================
export async function getAlerts(
  level?: string,
  acknowledged?: boolean,
  limit: number = 50,
): Promise<{ alerts: Alert[] }> {
  const params = new URLSearchParams();
  if (level) params.append("level", level);
  if (acknowledged !== undefined)
    params.append("acknowledged", String(acknowledged));
  if (limit) params.append("limit", String(limit));

  return apiCall(`/alerts${params.toString() ? "?" + params : ""}`);
}

export async function getAlert(id: string): Promise<{ alert: Alert }> {
  return apiCall(`/alerts/${id}`);
}

export async function acknowledgeAlert(
  id: string,
): Promise<{ message: string; alert: Alert }> {
  return apiCall(`/alerts/${id}/ack`, {
    method: "POST",
  });
}

// =====================
// SUBSCRIPTIONS
// =====================
export async function getSubscriptions(): Promise<{
  subscriptions: Subscription[];
}> {
  return apiCall("/subscriptions");
}

export async function createSubscription(
  channel: string,
  criteria: Record<string, unknown>,
  active?: boolean,
): Promise<{ message: string; subscription: Subscription }> {
  return apiCall("/subscriptions", {
    method: "POST",
    body: JSON.stringify({ channel, criteria, active }),
  });
}

export async function updateSubscription(
  id: string,
  updates: Partial<Subscription>,
): Promise<{ message: string; subscription: Subscription }> {
  return apiCall(`/subscriptions/${id}`, {
    method: "PUT",
    body: JSON.stringify(updates),
  });
}

export async function deleteSubscription(
  id: string,
): Promise<{ message: string }> {
  return apiCall(`/subscriptions/${id}`, {
    method: "DELETE",
  });
}

// =====================
// ADMIN - COMPUTED READINGS
// =====================
export async function getComputedReadings(
  device_id?: string,
  metric_type?: string,
): Promise<{ data: ComputedReading[] }> {
  const params = new URLSearchParams();
  if (device_id) params.append("device_id", device_id);
  if (metric_type) params.append("metric_type", metric_type);

  return apiCall(`/admin/computed${params.toString() ? "?" + params : ""}`);
}

export async function computeCumulativeDose(
  device_id: string,
  from: string,
  to: string,
): Promise<{ result: Record<string, unknown> }> {
  return apiCall("/admin/compute/cumulative", {
    method: "POST",
    body: JSON.stringify({ device_id, from, to }),
  });
}

export async function computeEWMA(
  device_id: string,
  from: string,
  to: string,
  alpha?: number,
): Promise<{ result: Record<string, unknown> }> {
  return apiCall("/admin/compute/ewma", {
    method: "POST",
    body: JSON.stringify({ device_id, from, to, alpha }),
  });
}

export async function detectPeaks(
  device_id: string,
  from: string,
  to: string,
  clusterWindowMinutes?: number,
): Promise<{ result: Record<string, unknown> }> {
  return apiCall("/admin/compute/peaks", {
    method: "POST",
    body: JSON.stringify({
      device_id,
      from,
      to,
      clusterWindowMinutes,
    }),
  });
}

// =====================
// ADMIN - DEVICE HEALTH
// =====================
export async function getDeviceHealth(
  id: string,
): Promise<{ health: DeviceHealth }> {
  return apiCall(`/admin/device/${id}/health`);
}

export async function computeDeviceHealth(
  device_id: string,
): Promise<{ health: DeviceHealth }> {
  return apiCall("/admin/compute/health", {
    method: "POST",
    body: JSON.stringify({ device_id }),
  });
}
