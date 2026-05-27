package com.example.radiation.ui.devices

import com.example.radiation.data.models.Device

object Devices {
    sealed interface Event {
        data object OnBack : Event
        data class OnNavigateToDetails(val deviceId: String) : Event
        data class ShowError(val message: String) : Event
    }

    sealed interface Action {
        data object OnRefresh : Action
        data object OnBack : Action
        data class OnDeviceClick(val deviceId: String) : Action
        
        // Add Device
        data class OnNewDeviceNameChange(val name: String) : Action
        data object OnAddDeviceClick : Action
    }

    data class State(
        val isLoading: Boolean = false,
        val devices: List<Device> = emptyList(),
        val error: String? = null,
        
        // Form for adding new device
        val newDeviceName: String = "",
        val isAddingDevice: Boolean = false
    )
}

