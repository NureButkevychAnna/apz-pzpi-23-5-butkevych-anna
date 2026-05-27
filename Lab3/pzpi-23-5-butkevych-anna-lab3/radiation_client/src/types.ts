export interface User {
  id: string;
  email: string;
  name: string;
  role: "user" | "admin" | "superadmin";
  created_at: string;
  updated_at: string;
}

export interface Device {
  id: string;
  name: string;
  device_token: string;
  is_active: boolean;
  owner_id: string;
  location_id?: string;
  location?: Location;
  owner?: User;
  created_at: string;
  updated_at: string;
}

export interface Location {
  id: string;
  latitude: number;
  longitude: number;
  address: string;
  description?: string;
  created_at: string;
  updated_at: string;
}

export interface Reading {
  id: string;
  device_id: string;
  measured_at: string;
  value: number;
  unit: string;
  metadata?: Record<string, unknown>;
  device?: Device;
  created_at: string;
  updated_at: string;
}

export interface Alert {
  id: string;
  device_id: string;
  reading_id?: string;
  level: "warning" | "danger" | "critical";
  message: string;
  acknowledged: boolean;
  acknowledged_at?: string;
  device?: Device;
  reading?: Reading;
  created_at: string;
  updated_at: string;
}

export interface Subscription {
  id: string;
  user_id: string;
  channel: "email" | "sms" | "push";
  criteria: Record<string, unknown>;
  active: boolean;
  created_at: string;
  updated_at: string;
}

export interface ComputedReading {
  id: string;
  device_id: string;
  window_start: string;
  window_end: string;
  metric_type: "cumulative_dose" | "ewma" | "peaks";
  value: Record<string, unknown>;
  created_at: string;
}

export interface DeviceHealth {
  id: string;
  device_id: string;
  last_seen: string;
  missing_count: number;
  uptime_pct: number;
  avg_battery: number;
  error_count: number;
  notes?: Record<string, unknown>;

  checked_at: string;
}

export interface AuthResponse {
  message: string;
  user: User;
  token: string;
}
