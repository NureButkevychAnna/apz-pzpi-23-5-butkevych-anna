package com.example.radiation.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.radiation.config.AppLocale
import com.example.radiation.data.models.User
import com.example.radiation.extension.toDetailedMessage
import com.example.radiation.repository.MainRepository
import com.example.radiation.repository.settings.LanguagePreferencesRepository
import com.example.radiation.ui.screen.settings.Settings
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val repository: MainRepository,
    private val languagePreferencesRepository: LanguagePreferencesRepository,
) : ViewModel() {
    private val _state = MutableStateFlow(Settings.State())
    val state = _state.asStateFlow()

    private val _event = MutableSharedFlow<Settings.Event>()
    val event = _event.asSharedFlow()

    init {
        onAction(Settings.Action.Refresh)
    }

    fun onAction(action: Settings.Action) {
        when (action) {
            Settings.Action.Refresh -> loadUser()
            Settings.Action.Logout -> logout()
            is Settings.Action.ChangeLanguage -> changeLanguage(action.language)
        }
    }

    private fun loadUser() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            val userId = repository.authRepository.getUserId() ?: run {
                _state.update { it.copy(isLoading = false, error = "User is not logged in") }
                return@launch
            }

            repository.authRepository.getUserById(userId)
                .onSuccess { user ->
                    _state.update { it.copy(isLoading = false, user = user) }
                }
                .onFailure { error ->
                    _state.update { it.copy(isLoading = false, error = error.toDetailedMessage("Failed to load profile")) }
                }
        }
    }

    private fun logout() {
        viewModelScope.launch {
            repository.logout()
            repository.clearAuth()
            _event.emit(Settings.Event.LoggedOut)
        }
    }

    private fun changeLanguage(language: String) {
        viewModelScope.launch {
            val normalized = AppLocale.normalize(language)
            languagePreferencesRepository.setLanguage(normalized)
            AppLocale.apply(normalized)
        }
    }
}
