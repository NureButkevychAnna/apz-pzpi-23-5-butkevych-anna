package com.example.radiation.location

/**
 * Data клас для зберігання координат
 */
data class LocationData(
    val latitude: Double,
    val longitude: Double,
    val accuracy: Float? = null,
    val altitude: Double? = null
)

/**
 * Інтерфейс для Location сервісу
 */
interface LocationService {
    fun getCurrentLocation(callback: (LocationData?) -> Unit)
    fun startLocationUpdates(callback: (LocationData) -> Unit)
    fun stopLocationUpdates()
}

