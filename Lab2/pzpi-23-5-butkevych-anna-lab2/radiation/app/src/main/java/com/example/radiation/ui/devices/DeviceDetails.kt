package com.example.radiation.ui.devices

import com.example.radiation.data.models.Device
import com.example.radiation.data.models.SensorReading

object DeviceDetails {
    sealed interface Event {
        data object OnBack : Event
        data class ShowError(val message: String) : Event
        data object DeviceDeleted : Event
    }

    sealed interface Action {
        data class OnLoad(val deviceId: String) : Action
        data object OnBack : Action
        data object OnDeleteClick : Action
        data class OnUpdateActiveStatus(val isActive: Boolean) : Action
        data object OnRefresh : Action
    }

    data class State(
        val isLoading: Boolean = false,
        val device: Device? = null,
        val readings: List<SensorReading> = emptyList(),
        val error: String? = null,
        val isDeleting: Boolean = false
    )
}
