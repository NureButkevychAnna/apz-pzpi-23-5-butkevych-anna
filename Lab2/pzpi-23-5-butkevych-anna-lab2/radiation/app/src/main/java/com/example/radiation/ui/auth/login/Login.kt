package com.example.radiation.ui.auth.login

import com.example.radiation.navigation.Screen

object Login {
    sealed interface Event {
        data class OnNavigate(val route: Screen) : Event
        data object OnBack : Event
    }

    sealed interface Action {
        data object OnBack : Action
        data class OnNavigate(val route: Screen) : Action
        data object OnLogIn : Action
        data class OnEmailChange(val email: String) : Action
        data class OnPasswordChange(val password: String) : Action
    }

    data class State(
        val inProgress: Boolean = false,
        val email: String = "",
        val password: String = "",
        val loginError: String? = null,
    )
}



