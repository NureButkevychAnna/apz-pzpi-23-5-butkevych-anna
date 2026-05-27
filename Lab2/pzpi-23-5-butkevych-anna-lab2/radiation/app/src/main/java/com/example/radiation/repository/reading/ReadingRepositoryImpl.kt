package com.example.radiation.repository.reading

import com.example.radiation.network.ApiService
import com.example.radiation.data.models.CreateReadingRequest
import javax.inject.Inject

class ReadingRepositoryImpl @Inject constructor(
    private val apiService: ApiService
) : ReadingRepository {
    
    override suspend fun submitReading(measuredAt: String, value: Double, unit: String): Result<Unit> {
        return try {
            apiService.submitReading(CreateReadingRequest(measuredAt, value, unit))
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun getReadings(deviceId: String?, limit: Int, since: String?): Result<List<com.example.radiation.data.models.SensorReading>> {
        return try {
            val response = apiService.getReadings(deviceId, limit, since)
            Result.success(response.readings)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

