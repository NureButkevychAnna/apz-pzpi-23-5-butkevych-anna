package com.example.radiation.ui.screen.subscriptions

import com.example.radiation.data.models.Subscription

object Subscriptions {
    data class State(
        val isLoading: Boolean = false,
        val subscriptions: List<Subscription> = emptyList(),
        val error: String? = null,
        val isCreating: Boolean = false,
        val newChannel: String = "",
    )

    sealed interface Action {
        data object Refresh : Action
        data class Delete(val id: String) : Action
        data class OnChannelChange(val channel: String) : Action
        data object Create : Action
        data object ToggleCreateDialog : Action
    }

    sealed interface Event {
        data class ShowMessage(val message: String) : Event
    }
}

