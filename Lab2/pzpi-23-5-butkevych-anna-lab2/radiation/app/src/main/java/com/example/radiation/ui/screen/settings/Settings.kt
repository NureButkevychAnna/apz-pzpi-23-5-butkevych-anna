package com.example.radiation.ui.screen.settings

import com.example.radiation.config.AppLocale
import com.example.radiation.data.models.User

object Settings {
    data class State(
        val isLoading: Boolean = true,
        val user: User? = null,
        val error: String? = null,
    )

    sealed interface Action {
        data object Refresh : Action
        data object Logout : Action
        data class ChangeLanguage(val language: String) : Action
    }

    sealed interface Event {
        data object LoggedOut : Event
    }
}

