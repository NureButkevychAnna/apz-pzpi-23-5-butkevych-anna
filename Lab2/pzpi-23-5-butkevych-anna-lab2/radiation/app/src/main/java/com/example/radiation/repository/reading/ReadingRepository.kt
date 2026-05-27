package com.example.radiation.repository.reading

import com.example.radiation.data.models.SensorReading

/**
 * Repository інтерфейс для управління показаннями датчиків
 */
interface ReadingRepository {
    suspend fun submitReading(measuredAt: String, value: Double, unit: String): Result<Unit>
    suspend fun getReadings(deviceId: String? = null, limit: Int = 100, since: String? = null): Result<List<SensorReading>>
}

