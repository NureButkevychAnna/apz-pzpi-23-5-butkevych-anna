package com.example.radiation.ui.screen.alerts

import com.example.radiation.data.models.Alert

object AlertDetails {
    data class State(
        val isLoading: Boolean = false,
        val alert: Alert? = null,
        val error: String? = null,
    )

    sealed interface Action {
        data class Load(val id: String) : Action
        data object Acknowledge : Action
        data object Back : Action
    }

    sealed interface Event {
        data object NavigateBack : Event
        data class ShowMessage(val message: String) : Event
    }
}

