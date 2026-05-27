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

import com.example.radiation.ui.screen.alerts.Alerts
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

@HiltViewModel
class AlertsViewModel @Inject constructor(
    private val repository: MainRepository,
) : ViewModel() {
    private val _state = MutableStateFlow(Alerts.State())
    val state = _state.asStateFlow()

    private val _event = MutableSharedFlow<Alerts.Event>()
    val event = _event.asSharedFlow()

    init {
        refresh()
    }

    fun onAction(action: Alerts.Action) {
        when (action) {
            Alerts.Action.Refresh -> refresh()
            is Alerts.Action.Acknowledge -> acknowledgeAlert(action.id)
            is Alerts.Action.SetFilter -> {
                _state.update { it.copy(filterAcknowledged = action.acknowledged) }
                refresh()
            }
            Alerts.Action.Back -> viewModelScope.launch { _event.emit(Alerts.Event.NavigateBack) }
        }
    }

    private fun refresh() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            repository.getAlerts(acknowledged = _state.value.filterAcknowledged)
                .onSuccess { alerts ->
                    _state.update { it.copy(isLoading = false, alerts = alerts) }
                }
                .onFailure { error ->
                    _state.update { it.copy(isLoading = false, error = error.toDetailedMessage("Failed to load alerts")) }
                }
        }
    }

    private fun acknowledgeAlert(id: String) {
        viewModelScope.launch {
            repository.acknowledgeAlert(id)
                .onSuccess {
                    _event.emit(Alerts.Event.ShowMessage("Оброблено"))
                    refresh()
                }
                .onFailure { error ->
                    _event.emit(Alerts.Event.ShowMessage(error.toDetailedMessage("Failed to acknowledge")))
                }
        }
    }
}

