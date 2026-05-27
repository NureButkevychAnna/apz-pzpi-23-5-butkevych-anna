package com.example.radiation.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.radiation.extension.toDetailedMessage
import com.example.radiation.repository.MainRepository
import com.example.radiation.ui.devices.DeviceDetails
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DeviceDetailsViewModel @Inject constructor(
    private val repository: MainRepository,
) : ViewModel() {
    private val _state = MutableStateFlow(DeviceDetails.State())
    val state = _state.asStateFlow()

    private val _event = MutableSharedFlow<DeviceDetails.Event>()
    val event = _event.asSharedFlow()

    private var currentDeviceId: String? = null

    fun onAction(action: DeviceDetails.Action) {
        when (action) {
            is DeviceDetails.Action.OnLoad -> {
                currentDeviceId = action.deviceId
                load(action.deviceId)
            }
            DeviceDetails.Action.OnBack -> viewModelScope.launch { _event.emit(DeviceDetails.Event.OnBack) }
            DeviceDetails.Action.OnDeleteClick -> deleteDevice()
            is DeviceDetails.Action.OnUpdateActiveStatus -> updateStatus(action.isActive)
            DeviceDetails.Action.OnRefresh -> currentDeviceId?.let { load(it) }
        }
    }

    private fun load(deviceId: String) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            val deviceRes = repository.getDeviceById(deviceId)
            val readingsRes = repository.getReadings(deviceId = deviceId, limit = 20)

            _state.update {
                it.copy(
                    isLoading = false,
                    device = deviceRes.getOrNull(),
                    readings = readingsRes.getOrDefault(emptyList()),
                    error = listOfNotNull(
                        deviceRes.exceptionOrNull()?.toDetailedMessage("Failed to load device"),
                        readingsRes.exceptionOrNull()?.toDetailedMessage("Failed to load readings")
                    ).joinToString("\n").takeIf { s -> s.isNotBlank() }
                )
            }
        }
    }

    private fun deleteDevice() {
        val id = currentDeviceId ?: return
        viewModelScope.launch {
            _state.update { it.copy(isDeleting = true) }
            repository.deleteDevice(id)
                .onSuccess {
                    _state.update { it.copy(isDeleting = false) }
                    _event.emit(DeviceDetails.Event.DeviceDeleted)
                }
                .onFailure { error ->
                    _state.update { it.copy(isDeleting = false) }
                    _event.emit(DeviceDetails.Event.ShowError(error.toDetailedMessage("Failed to delete device")))
                }
        }
    }

    private fun updateStatus(isActive: Boolean) {
        val id = currentDeviceId ?: return
        viewModelScope.launch {
            repository.updateDevice(id, isActive = isActive)
                .onSuccess {
                    load(id)
                }
                .onFailure { error ->
                    _event.emit(DeviceDetails.Event.ShowError(error.toDetailedMessage("Failed to update status")))
                }
        }
    }
}
