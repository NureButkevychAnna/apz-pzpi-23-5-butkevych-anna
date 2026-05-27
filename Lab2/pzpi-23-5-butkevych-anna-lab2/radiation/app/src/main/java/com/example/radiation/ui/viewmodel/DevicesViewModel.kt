package com.example.radiation.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.radiation.data.models.Device
import com.example.radiation.extension.toDetailedMessage
import com.example.radiation.repository.MainRepository
import com.example.radiation.ui.devices.Devices
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DevicesViewModel @Inject constructor(
    private val repository: MainRepository,
) : ViewModel() {
    private val _state = MutableStateFlow(Devices.State())
    val state = _state.asStateFlow()

    private val _event = MutableSharedFlow<Devices.Event>()
    val event = _event.asSharedFlow()

    init {
        refresh()
    }

    fun onAction(action: Devices.Action) {
        when (action) {
            Devices.Action.OnRefresh -> refresh()
            Devices.Action.OnBack -> viewModelScope.launch { _event.emit(Devices.Event.OnBack) }
            is Devices.Action.OnDeviceClick -> viewModelScope.launch {
                _event.emit(Devices.Event.OnNavigateToDetails(action.deviceId))
            }
            is Devices.Action.OnNewDeviceNameChange -> _state.update {
                it.copy(newDeviceName = action.name)
            }
            Devices.Action.OnAddDeviceClick -> createDevice()
        }
    }

    private fun refresh() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            repository.getDevices()
                .onSuccess { devices ->
                    _state.update { it.copy(isLoading = false, devices = devices) }
                }
                .onFailure { error ->
                    _state.update { it.copy(isLoading = false, error = error.toDetailedMessage("Failed to load devices")) }
                }
        }
    }

    private fun createDevice() {
        val name = state.value.newDeviceName
        if (name.isBlank()) return

        viewModelScope.launch {
            _state.update { it.copy(isAddingDevice = true) }
            repository.createDevice(name)
                .onSuccess {
                    _state.update { it.copy(isAddingDevice = false, newDeviceName = "") }
                    refresh()
                }
                .onFailure { error ->
                    val message = error.toDetailedMessage("Failed to create device")
                    _state.update { it.copy(isAddingDevice = false) }
                    _event.emit(Devices.Event.ShowError(message))
                }
        }
    }
}
