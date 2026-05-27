package com.example.radiation.db.data

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.radiation.db.entity.User
import com.example.radiation.db.entity.Device
import com.example.radiation.db.entity.SensorReading
import com.example.radiation.db.entity.Alert
import com.example.radiation.db.dao.UserDao
import com.example.radiation.db.dao.DeviceDao
import com.example.radiation.db.dao.SensorReadingDao
import com.example.radiation.db.dao.AlertDao

/**
 * Основний Room Database
 * Об'єднує всі Entity класи та DAOs
 */
@Database(
    entities = [
        User::class,
        Device::class,
        SensorReading::class,
        Alert::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(DbConverters::class)
abstract class AppDb : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun deviceDao(): DeviceDao
    abstract fun sensorReadingDao(): SensorReadingDao
    abstract fun alertDao(): AlertDao
}

