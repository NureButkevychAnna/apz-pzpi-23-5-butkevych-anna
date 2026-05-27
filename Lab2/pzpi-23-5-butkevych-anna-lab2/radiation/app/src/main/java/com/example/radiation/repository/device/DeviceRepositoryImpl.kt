package com.example.radiation.repository.device

import com.example.radiation.network.ApiService
import com.example.radiation.data.models.*
import javax.inject.Inject

class DeviceRepositoryImpl @Inject constructor(
    private val apiService: ApiService
) : DeviceRepository {
    
    override suspend fun getDevices(): Result<List<Device>> {
        return try {
            val response = apiService.getDevices()
            Result.success(response.devices)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun createDevice(name: String, locationId: String?): Result<Device> {
        return try {
            val response = apiService.createDevice(CreateDeviceRequest(name, locationId))
            Result.success(response.device)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun getDeviceById(id: String): Result<Device> {
        return try {
            val device = apiService.getDeviceById(id)
            Result.success(device)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun updateDevice(id: String, name: String?, isActive: Boolean?): Result<Device> {
        return try {
            val response = apiService.updateDevice(id, UpdateDeviceRequest(name, null, isActive))
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun deleteDevice(id: String): Result<Unit> {
        return try {
            apiService.deleteDevice(id)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

