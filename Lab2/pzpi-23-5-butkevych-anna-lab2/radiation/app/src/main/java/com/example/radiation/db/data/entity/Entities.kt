package com.example.radiation.db.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ColumnInfo
import java.util.Date

/**
 * User Entity - для зберігання користувачів
 */
@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey val id: String,
    val email: String,
    val name: String,
    val role: String? = null
)

/**
 * Device Entity - для зберігання пристроїв
 */
@Entity(tableName = "devices")
data class DeviceEntity(
    @PrimaryKey val id: String,
    val name: String,
    @ColumnInfo(name = "device_token") val deviceToken: String,
    @ColumnInfo(name = "is_active") val isActive: Boolean,
    @ColumnInfo(name = "location_id") val locationId: String? = null
)

/**
 * SensorReading Entity - для зберігання показань датчиків
 */
@Entity(
    tableName = "sensor_readings",
    indices = [androidx.room.Index(value = ["device_id"])]
)
data class SensorReadingEntity(
    @PrimaryKey val id: String,
    @ColumnInfo(name = "device_id") val deviceId: String,
    val value: Double,
    val unit: String,
    @ColumnInfo(name = "measured_at") val measuredAt: Date
)

/**
 * Alert Entity - для зберігання сповіщень
 */
@Entity(
    tableName = "alerts",
    indices = [androidx.room.Index(value = ["device_id"])]
)
data class AlertEntity(
    @PrimaryKey val id: String,
    @ColumnInfo(name = "device_id") val deviceId: String,
    val level: String, // "info", "warning", "critical"
    val message: String,
    val acknowledged: Boolean = false
)

