package com.example.radiation.repository.device

import com.example.radiation.data.models.Device

/**
 * Repository інтерфейс для управління пристроями
 */
interface DeviceRepository {
    suspend fun getDevices(): Result<List<Device>>
    suspend fun createDevice(name: String, locationId: String? = null): Result<Device>
    suspend fun getDeviceById(id: String): Result<Device>
    suspend fun updateDevice(id: String, name: String? = null, isActive: Boolean? = null): Result<Device>
    suspend fun deleteDevice(id: String): Result<Unit>
}

