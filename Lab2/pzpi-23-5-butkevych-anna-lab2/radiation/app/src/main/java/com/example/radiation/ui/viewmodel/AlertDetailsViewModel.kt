package com.example.radiation.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.radiation.data.models.Alert
import com.example.radiation.extension.toDetailedMessage
import com.example.radiation.repository.MainRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

import com.example.radiation.ui.screen.alerts.AlertDetails
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

@HiltViewModel
class AlertDetailsViewModel @Inject constructor(
    private val repository: MainRepository,
) : ViewModel() {
    private val _state = MutableStateFlow(AlertDetails.State())
    val state = _state.asStateFlow()

    private val _event = MutableSharedFlow<AlertDetails.Event>()
    val event = _event.asSharedFlow()

    fun onAction(action: AlertDetails.Action) {
        when (action) {
            is AlertDetails.Action.Load -> load(action.id)
            AlertDetails.Action.Acknowledge -> acknowledge()
            AlertDetails.Action.Back -> viewModelScope.launch { _event.emit(AlertDetails.Event.NavigateBack) }
        }
    }

    private fun load(alertId: String) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            repository.getAlertById(alertId)
                .onSuccess { alert ->
                    _state.update { it.copy(isLoading = false, alert = alert) }
                }
                .onFailure { error ->
                    _state.update { it.copy(isLoading = false, error = error.toDetailedMessage("Failed to load alert")) }
                }
        }
    }

    private fun acknowledge() {
        val currentId = state.value.alert?.id ?: return
        viewModelScope.launch {
            repository.acknowledgeAlert(currentId)
                .onSuccess {
                    _event.emit(AlertDetails.Event.ShowMessage("Оброблено"))
                    load(currentId)
                }
                .onFailure { error ->
                    _event.emit(AlertDetails.Event.ShowMessage(error.toDetailedMessage("Failed to acknowledge")))
                }
        }
    }
}

