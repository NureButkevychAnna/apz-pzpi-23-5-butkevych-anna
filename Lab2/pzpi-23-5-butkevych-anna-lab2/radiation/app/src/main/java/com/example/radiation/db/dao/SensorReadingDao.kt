package com.example.radiation.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.radiation.db.entity.SensorReading

@Dao
interface SensorReadingDao {
    @Insert
    suspend fun insert(reading: SensorReading)
    
    @Update
    suspend fun update(reading: SensorReading)
    
    @Delete
    suspend fun delete(reading: SensorReading)
    
    @Query("SELECT * FROM sensor_readings WHERE id = :id")
    suspend fun getReadingById(id: String): SensorReading?
    
    @Query("SELECT * FROM sensor_readings WHERE device_id = :deviceId ORDER BY measured_at DESC LIMIT :limit")
    suspend fun getReadingsByDevice(deviceId: String, limit: Int = 100): List<SensorReading>
    
    @Query("SELECT * FROM sensor_readings ORDER BY measured_at DESC")
    suspend fun getAllReadings(): List<SensorReading>
    
    @Query("DELETE FROM sensor_readings")
    suspend fun deleteAll()
}

