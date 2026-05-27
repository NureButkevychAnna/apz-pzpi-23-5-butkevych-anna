package com.example.radiation.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.radiation.data.models.Alert
import com.example.radiation.data.models.Device
import com.example.radiation.data.models.SensorReading
import com.example.radiation.extension.toDetailedMessage
import com.example.radiation.repository.MainRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class HomeUiState(
    val isLoading: Boolean = true,
    val devices: List<Device> = emptyList(),
    val readings: List<SensorReading> = emptyList(),
    val alerts: List<Alert> = emptyList(),
    val error: String? = null,
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: MainRepository,
) : ViewModel() {
    private val _state = MutableStateFlow(HomeUiState())
    val state = _state.asStateFlow()

    init {
        refresh()
    }

    fun refresh() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }

            val devices = repository.getDevices()
            val readings = repository.getReadings(limit = 10)
            val alerts = repository.getAlerts()

            val errorMessage = listOfNotNull(
                devices.exceptionOrNull()?.toDetailedMessage("Failed to load devices")?.let { "Devices:\n$it" },
                readings.exceptionOrNull()?.toDetailedMessage("Failed to load readings")?.let { "Readings:\n$it" },
                alerts.exceptionOrNull()?.toDetailedMessage("Failed to load alerts")?.let { "Alerts:\n$it" },
            ).joinToString("\n\n")

            _state.update {
                it.copy(
                    isLoading = false,
                    devices = devices.getOrDefault(emptyList()),
                    readings = readings.getOrDefault(emptyList()),
                    alerts = alerts.getOrDefault(emptyList()),
                    error = errorMessage.takeIf { it.isNotBlank() },
                )
            }
        }
    }
}
