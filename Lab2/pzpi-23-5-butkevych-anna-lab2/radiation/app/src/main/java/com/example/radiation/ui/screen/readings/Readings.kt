package com.example.radiation.ui.screen.readings

import com.example.radiation.data.models.SensorReading

object Readings {
    sealed interface State {
        data object Loading : State
        data class Error(val message: String) : State
        data class Content(
            val readings: List<SensorReading> = emptyList(),
            val deviceId: String
        ) : State
    }

    sealed interface Action {
        data class Load(val deviceId: String) : Action
        data object Refresh : Action
        data object Back : Action
    }

    sealed interface Event {
        data object NavigateBack : Event
    }
}

