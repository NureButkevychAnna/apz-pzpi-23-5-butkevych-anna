package com.example.radiation.ui.auth.register

import com.example.radiation.navigation.Screen

object Register {
    sealed interface Event {
        data class OnNavigate(val route: Screen.Main) : Event
        data object OnBack : Event
    }

    sealed interface Action {
        data object OnBack : Action
        data class OnNavigate(val route: Screen.Main) : Action
        data object OnRegister : Action
        data class OnNameChange(val name: String) : Action
        data class OnEmailChange(val email: String) : Action
        data class OnPasswordChange(val password: String) : Action
    }

    data class State(
        val inProgress: Boolean = false,
        val name: String = "",
        val email: String = "",
        val password: String = "",
        val registerError: String? = null,
    )
}

