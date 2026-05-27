package com.example.radiation.repository.alert

import com.example.radiation.data.models.Alert

/**
 * Repository інтерфейс для управління сповіщеннями
 */
interface AlertRepository {
    suspend fun getAlerts(level: String? = null, acknowledged: Boolean? = null): Result<List<Alert>>
    suspend fun getAlertById(id: String): Result<Alert>
    suspend fun acknowledgeAlert(id: String): Result<Unit>
}

