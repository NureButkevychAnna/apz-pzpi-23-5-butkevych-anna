package com.example.radiation.data.models

// ============ AUTH ============

data class RegisterRequest(
    val email: String,
    val password: String,
    val name: String
)

data class RegisterResponse(
    val message: String,
    val user: User,
    val token: String
)

data class LoginRequest(
    val email: String,
    val password: String
)

data class LoginResponse(
    val message: String,
    val user: User,
    val token: String
)

// ============ USER ============

data class User(
    val id: String,
    val email: String,
    val name: String,
    val role: String? = null
)

// ============ DEVICE ============

data class Device(
    val id: String,
    val name: String,
    val device_token: String,
    val is_active: Boolean,
    val location_id: String? = null
)

data class CreateDeviceRequest(
    val name: String,
    val location_id: String? = null
)

data class CreateDeviceResponse(
    val message: String,
    val device: Device
)

data class UpdateDeviceRequest(
    val name: String? = null,
    val location_id: String? = null,
    val is_active: Boolean? = null
)

data class DeviceListResponse(
    val devices: List<Device>
)

// ============ SENSOR READING ============

data class SensorReading(
    val id: String,
    val device_id: String,
    val value: Double,
    val unit: String,
    val measured_at: String
)

data class CreateReadingRequest(
    val measured_at: String,
    val value: Double,
    val unit: String,
    val metadata: Map<String, Any>? = null
)

data class ReadingsListResponse(
    val readings: List<SensorReading>
)

// ============ ALERT ============

data class Alert(
    val id: String,
    val device_id: String,
    val level: String, // "info", "warning", "critical"
    val message: String,
    val acknowledged: Boolean,
    val created_at: String = ""
)

data class AlertsListResponse(
    val alerts: List<Alert>
)

// ============ SUBSCRIPTION ============

data class Subscription(
    val id: String,
    val channel: String, // "email", "sms", "push"
    val criteria: Map<String, Any>? = null,
    val created_at: String = ""
)

data class CreateSubscriptionRequest(
    val channel: String,
    val criteria: Map<String, Any>? = null
)

data class SubscriptionsListResponse(
    val subscriptions: List<Subscription>
)

data class UpdateSubscriptionRequest(
    val criteria: Map<String, Any>? = null
)

// ============ COMPUTED READINGS ============

data class ComputedReading(
    val id: String,
    val device_id: String,
    val metric_type: String, // "cumulative", "ewma"
    val value: Double,
    val computed_at: String
)

data class ComputedReadingsListResponse(
    val readings: List<ComputedReading>
)

// ============ COMPUTATION REQUESTS ============

data class CumulativeDoseRequest(
    val device_id: String,
    val from: String,
    val to: String
)

data class EWMARequest(
    val device_id: String
)

data class DeviceHealthRequest(
    val device_id: String
)

data class DeviceHealthResponse(
    val device_id: String,
    val health_status: String, // "healthy", "warning", "critical"
    val last_reading_at: String,
    val total_readings: Int
)

// ============ GENERIC RESPONSES ============

data class SuccessResponse(
    val message: String
)
