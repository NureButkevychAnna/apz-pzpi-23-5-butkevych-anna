package com.example.radiation.repository

import com.example.radiation.repository.auth.AuthRepository
import com.example.radiation.repository.device.DeviceRepository
import com.example.radiation.repository.reading.ReadingRepository
import com.example.radiation.repository.alert.AlertRepository
import com.example.radiation.repository.subscription.SubscriptionRepository
import javax.inject.Inject

/**
 * Головний Repository - фасад, що об'єднує всі окремі репозиторії
 * Надає единий інтерфейс для роботи з усіма операціями
 */
class MainRepository @Inject constructor(
    val authRepository: AuthRepository,
    val deviceRepository: DeviceRepository,
    val readingRepository: ReadingRepository,
    val alertRepository: AlertRepository,
    val subscriptionRepository: SubscriptionRepository,
) {
    // ============ AUTH - делегування ============
    suspend fun register(email: String, password: String, name: String) = 
        authRepository.register(email, password, name)
    
    suspend fun login(email: String, password: String) = 
        authRepository.login(email, password)
    
    suspend fun logout() = 
        authRepository.logout()
    
    suspend fun getUserById(id: String) = 
        authRepository.getUserById(id)
    
    suspend fun saveToken(token: String) = 
        authRepository.saveToken(token)

    suspend fun getToken() = 
        authRepository.getToken()

    suspend fun getUserId() =
        authRepository.getUserId()

    suspend fun clearAuth() =
        authRepository.clearAuth()
    
    // ============ DEVICE - делегування ============
    suspend fun getDevices() = 
        deviceRepository.getDevices()
    
    suspend fun createDevice(name: String, locationId: String? = null) = 
        deviceRepository.createDevice(name, locationId)
    
    suspend fun getDeviceById(id: String) = 
        deviceRepository.getDeviceById(id)
    
    suspend fun updateDevice(id: String, name: String? = null, isActive: Boolean? = null) = 
        deviceRepository.updateDevice(id, name, isActive)
    
    suspend fun deleteDevice(id: String) = 
        deviceRepository.deleteDevice(id)
    
    // ============ READING - делегування ============
    suspend fun submitReading(measuredAt: String, value: Double, unit: String) = 
        readingRepository.submitReading(measuredAt, value, unit)
    
    suspend fun getReadings(deviceId: String? = null, limit: Int = 100, since: String? = null) = 
        readingRepository.getReadings(deviceId, limit, since)
    
    // ============ ALERT - делегування ============
    suspend fun getAlerts(level: String? = null, acknowledged: Boolean? = null) = 
        alertRepository.getAlerts(level, acknowledged)
    
    suspend fun getAlertById(id: String) = 
        alertRepository.getAlertById(id)
    
    suspend fun acknowledgeAlert(id: String) = 
        alertRepository.acknowledgeAlert(id)
    
    // ============ SUBSCRIPTION - делегування ============
    suspend fun createSubscription(channel: String, criteria: Map<String, Any>? = null) = 
        subscriptionRepository.createSubscription(channel, criteria)
    
    suspend fun getSubscriptions() = 
        subscriptionRepository.getSubscriptions()
    
    suspend fun deleteSubscription(id: String) = 
        subscriptionRepository.deleteSubscription(id)
    
    suspend fun updateSubscription(id: String, criteria: Map<String, Any>? = null) = 
        subscriptionRepository.updateSubscription(id, criteria)

}
