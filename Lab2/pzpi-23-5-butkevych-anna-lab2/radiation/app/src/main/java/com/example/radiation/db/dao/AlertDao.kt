package com.example.radiation.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.radiation.db.entity.Alert

@Dao
interface AlertDao {
    @Insert
    suspend fun insert(alert: Alert)
    
    @Update
    suspend fun update(alert: Alert)
    
    @Delete
    suspend fun delete(alert: Alert)
    
    @Query("SELECT * FROM alerts WHERE id = :id")
    suspend fun getAlertById(id: String): Alert?
    
    @Query("SELECT * FROM alerts WHERE device_id = :deviceId")
    suspend fun getAlertsByDevice(deviceId: String): List<Alert>
    
    @Query("SELECT * FROM alerts WHERE acknowledged = 0")
    suspend fun getUnacknowledgedAlerts(): List<Alert>
    
    @Query("SELECT * FROM alerts")
    suspend fun getAllAlerts(): List<Alert>
    
    @Query("DELETE FROM alerts")
    suspend fun deleteAll()
}

