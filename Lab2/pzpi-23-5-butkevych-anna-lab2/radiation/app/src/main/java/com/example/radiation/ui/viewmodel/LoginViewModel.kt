package com.example.radiation.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.radiation.extension.toDetailedMessage
import com.example.radiation.navigation.Screen
import com.example.radiation.ui.auth.login.Login
import com.example.radiation.repository.MainRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val repository: MainRepository,
) : ViewModel() {
    private val _state = MutableStateFlow(Login.State())
    val state = _state.asStateFlow()

    private val _event = MutableSharedFlow<Login.Event>()
    val event = _event.asSharedFlow()

    fun onAction(action: Login.Action) = viewModelScope.launch {
        when (action) {
            Login.Action.OnBack -> _event.emit(Login.Event.OnBack)
            is Login.Action.OnNavigate -> _event.emit(Login.Event.OnNavigate(route = action.route))
            is Login.Action.OnEmailChange -> {
                _state.update { it.copy(email = action.email, loginError = null) }
            }
            is Login.Action.OnPasswordChange -> {
                _state.update { it.copy(password = action.password, loginError = null) }
            }
            Login.Action.OnLogIn -> login(
                email = state.value.email,
                password = state.value.password,
            )
        }
    }

    private fun login(email: String, password: String) {
        viewModelScope.launch {
            _state.update { it.copy(inProgress = true, loginError = null) }
            repository.login(email, password)
                .onSuccess {
                    _state.update { it.copy(inProgress = false) }
                    _event.emit(Login.Event.OnNavigate(Screen.Main.Home))
                }
                .onFailure { error ->
                    _state.update {
                        it.copy(
                            inProgress = false,
                            loginError = error.toDetailedMessage("Login failed"),
                        )
                    }
                }
        }
    }
}
