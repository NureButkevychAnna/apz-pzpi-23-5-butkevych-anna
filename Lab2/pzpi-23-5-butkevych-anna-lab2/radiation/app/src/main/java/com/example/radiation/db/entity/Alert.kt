package com.example.radiation.db.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "alerts",
    foreignKeys = [
        ForeignKey(
            entity = Device::class,
            parentColumns = ["id"],
            childColumns = ["device_id"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class Alert(
    @PrimaryKey
    val id: String,
    val device_id: String,
    val level: String,
    val message: String,
    val acknowledged: Boolean = false
)

