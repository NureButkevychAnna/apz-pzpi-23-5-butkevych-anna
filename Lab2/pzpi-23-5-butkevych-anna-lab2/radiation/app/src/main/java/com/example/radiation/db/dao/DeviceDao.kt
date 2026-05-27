package com.example.radiation.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.radiation.db.entity.Device

@Dao
interface DeviceDao {
    @Insert
    suspend fun insert(device: Device)
    
    @Update
    suspend fun update(device: Device)
    
    @Delete
    suspend fun delete(device: Device)
    
    @Query("SELECT * FROM devices WHERE id = :id")
    suspend fun getDeviceById(id: String): Device?
    
    @Query("SELECT * FROM devices WHERE is_active = 1")
    suspend fun getActiveDevices(): List<Device>
    
    @Query("SELECT * FROM devices")
    suspend fun getAllDevices(): List<Device>
    
    @Query("DELETE FROM devices")
    suspend fun deleteAll()
}

