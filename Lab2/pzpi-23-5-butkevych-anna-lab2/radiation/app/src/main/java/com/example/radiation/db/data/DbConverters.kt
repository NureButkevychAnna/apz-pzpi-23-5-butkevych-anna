package com.example.radiation.db.data

import androidx.room.TypeConverter
import com.google.gson.Gson
import java.util.Date

/**
 * TypeConverters для Room Database
 * Дозволяють зберігати складні типи як рядки в БД
 */
class DbConverters {
    @TypeConverter
    fun fromDate(value: Long?): Date? {
        return value?.let { Date(it) }
    }
    
    @TypeConverter
    fun dateToLong(date: Date?): Long? {
        return date?.time
    }
    
    @TypeConverter
    fun fromJson(value: String?): Map<String, Any>? {
        return value?.let {
            @Suppress("UNCHECKED_CAST")
            Gson().fromJson(it, Map::class.java) as Map<String, Any>
        }
    }
    
    @TypeConverter
    fun toJson(map: Map<String, Any>?): String? {
        return map?.let { Gson().toJson(it) }
    }
}

