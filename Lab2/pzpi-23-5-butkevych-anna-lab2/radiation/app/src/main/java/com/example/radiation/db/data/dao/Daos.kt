package com.example.radiation.db.data.dao

import androidx.room.*
import com.example.radiation.db.data.entity.UserEntity
import com.example.radiation.db.data.entity.DeviceEntity
import com.example.radiation.db.data.entity.SensorReadingEntity
import com.example.radiation.db.data.entity.AlertEntity
import kotlinx.coroutines.flow.Flow

/**
 * DAO для User Entity
 */
@Dao
interface UserDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserEntity)
    
    @Query("SELECT * FROM users WHERE id = :id")
    suspend fun getUserById(id: String): UserEntity?
    
    @Query("SELECT * FROM users")
    fun getAllUsers(): Flow<List<UserEntity>>
    
    @Delete
    suspend fun deleteUser(user: UserEntity)
    
    @Query("DELETE FROM users")
    suspend fun deleteAllUsers()
}

/**
 * DAO для Device Entity
 */
@Dao
interface DeviceDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDevice(device: DeviceEntity)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDevices(devices: List<DeviceEntity>)
    
    @Query("SELECT * FROM devices WHERE id = :id")
    suspend fun getDeviceById(id: String): DeviceEntity?
    
    @Query("SELECT * FROM devices ORDER BY name ASC")
    fun getAllDevices(): Flow<List<DeviceEntity>>
    
    @Query("SELECT * FROM devices WHERE is_active = 1")
    fun getActiveDevices(): Flow<List<DeviceEntity>>
    
    @Update
    suspend fun updateDevice(device: DeviceEntity)
    
    @Delete
    suspend fun deleteDevice(device: DeviceEntity)
    
    @Query("DELETE FROM devices WHERE id = :id")
    suspend fun deleteDeviceById(id: String)
    
    @Query("DELETE FROM devices")
    suspend fun deleteAllDevices()
}

/**
 * DAO для SensorReading Entity
 */
@Dao
interface SensorReadingDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReading(reading: SensorReadingEntity)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReadings(readings: List<SensorReadingEntity>)
    
    @Query("SELECT * FROM sensor_readings WHERE id = :id")
    suspend fun getReadingById(id: String): SensorReadingEntity?
    
    @Query("SELECT * FROM sensor_readings WHERE device_id = :deviceId ORDER BY measured_at DESC")
    fun getReadingsByDevice(deviceId: String): Flow<List<SensorReadingEntity>>
    
    @Query("SELECT * FROM sensor_readings ORDER BY measured_at DESC LIMIT :limit")
    fun getLatestReadings(limit: Int = 100): Flow<List<SensorReadingEntity>>
    
    @Query("SELECT * FROM sensor_readings WHERE measured_at >= :since ORDER BY measured_at DESC")
    fun getReadingsSince(since: Long): Flow<List<SensorReadingEntity>>
    
    @Delete
    suspend fun deleteReading(reading: SensorReadingEntity)
    
    @Query("DELETE FROM sensor_readings WHERE device_id = :deviceId")
    suspend fun deleteReadingsByDevice(deviceId: String)
    
    @Query("DELETE FROM sensor_readings")
    suspend fun deleteAllReadings()
}

/**
 * DAO для Alert Entity
 */
@Dao
interface AlertDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAlert(alert: AlertEntity)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAlerts(alerts: List<AlertEntity>)
    
    @Query("SELECT * FROM alerts WHERE id = :id")
    suspend fun getAlertById(id: String): AlertEntity?
    
    @Query("SELECT * FROM alerts ORDER BY id DESC")
    fun getAllAlerts(): Flow<List<AlertEntity>>
    
    @Query("SELECT * FROM alerts WHERE level = :level ORDER BY id DESC")
    fun getAlertsByLevel(level: String): Flow<List<AlertEntity>>
    
    @Query("SELECT * FROM alerts WHERE acknowledged = 0 ORDER BY id DESC")
    fun getUnacknowledgedAlerts(): Flow<List<AlertEntity>>
    
    @Query("SELECT * FROM alerts WHERE device_id = :deviceId ORDER BY id DESC")
    fun getAlertsByDevice(deviceId: String): Flow<List<AlertEntity>>
    
    @Update
    suspend fun updateAlert(alert: AlertEntity)
    
    @Delete
    suspend fun deleteAlert(alert: AlertEntity)
    
    @Query("DELETE FROM alerts WHERE id = :id")
    suspend fun deleteAlertById(id: String)
    
    @Query("DELETE FROM alerts")
    suspend fun deleteAllAlerts()
}

