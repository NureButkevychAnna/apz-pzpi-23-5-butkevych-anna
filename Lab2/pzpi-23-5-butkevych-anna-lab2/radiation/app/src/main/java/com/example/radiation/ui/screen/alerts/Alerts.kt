package com.example.radiation.ui.screen.alerts

import com.example.radiation.data.models.Alert

object Alerts {
    data class State(
        val isLoading: Boolean = false,
        val alerts: List<Alert> = emptyList(),
        val error: String? = null,
        val filterAcknowledged: Boolean? = null
    )

    sealed interface Action {
        data object Refresh : Action
        data class Acknowledge(val id: String) : Action
        data class SetFilter(val acknowledged: Boolean?) : Action
        data object Back : Action
    }

    sealed interface Event {
        data class ShowMessage(val message: String) : Event
        data object NavigateBack : Event
    }
}

