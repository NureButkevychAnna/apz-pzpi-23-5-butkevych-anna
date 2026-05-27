package com.example.radiation.repository.alert

import com.example.radiation.network.ApiService
import javax.inject.Inject

class AlertRepositoryImpl @Inject constructor(
    private val apiService: ApiService
) : AlertRepository {
    
    override suspend fun getAlerts(level: String?, acknowledged: Boolean?): Result<List<com.example.radiation.data.models.Alert>> {
        return try {
            val response = apiService.getAlerts(level, acknowledged)
            Result.success(response.alerts)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun getAlertById(id: String): Result<com.example.radiation.data.models.Alert> {
        return try {
            val alert = apiService.getAlertById(id)
            Result.success(alert)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun acknowledgeAlert(id: String): Result<Unit> {
        return try {
            apiService.acknowledgeAlert(id)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

