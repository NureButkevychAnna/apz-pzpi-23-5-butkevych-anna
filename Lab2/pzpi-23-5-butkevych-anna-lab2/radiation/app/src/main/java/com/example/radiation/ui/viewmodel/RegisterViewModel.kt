package com.example.radiation.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.radiation.extension.toDetailedMessage
import com.example.radiation.navigation.Screen
import com.example.radiation.repository.MainRepository
import com.example.radiation.ui.auth.register.Register
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val repository: MainRepository,
) : ViewModel() {
    private val _state = MutableStateFlow(Register.State())
    val state = _state.asStateFlow()

    private val _event = MutableSharedFlow<Register.Event>()
    val event = _event.asSharedFlow()

    fun onAction(action: Register.Action) = viewModelScope.launch {
        when (action) {
            Register.Action.OnBack -> _event.emit(Register.Event.OnBack)
            is Register.Action.OnNavigate -> _event.emit(Register.Event.OnNavigate(action.route))
            is Register.Action.OnNameChange -> _state.update { it.copy(name = action.name, registerError = null) }
            is Register.Action.OnEmailChange -> _state.update { it.copy(email = action.email, registerError = null) }
            is Register.Action.OnPasswordChange -> _state.update { it.copy(password = action.password, registerError = null) }
            Register.Action.OnRegister -> register()
        }
    }

    private fun register() {
        viewModelScope.launch {
            val current = state.value
            _state.update { it.copy(inProgress = true, registerError = null) }
            repository.register(current.email, current.password, current.name)
                .onSuccess {
                    _state.update { it.copy(inProgress = false) }
                    _event.emit(Register.Event.OnNavigate(Screen.Main.Home))
                }
                .onFailure { error ->
                    _state.update { it.copy(inProgress = false, registerError = error.toDetailedMessage("Registration failed")) }
                }
        }
    }
}
