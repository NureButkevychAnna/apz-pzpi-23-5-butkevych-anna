package com.example.radiation.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.radiation.data.models.Subscription
import com.example.radiation.extension.toDetailedMessage
import com.example.radiation.repository.MainRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

import com.example.radiation.ui.screen.subscriptions.Subscriptions
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

@HiltViewModel
class SubscriptionsViewModel @Inject constructor(
    private val repository: MainRepository,
) : ViewModel() {
    private val _state = MutableStateFlow(Subscriptions.State())
    val state = _state.asStateFlow()

    private val _event = MutableSharedFlow<Subscriptions.Event>()
    val event = _event.asSharedFlow()

    init {
        onAction(Subscriptions.Action.Refresh)
    }

    fun onAction(action: Subscriptions.Action) {
        when (action) {
            Subscriptions.Action.Refresh -> refresh()
            is Subscriptions.Action.Delete -> deleteSubscription(action.id)
            is Subscriptions.Action.OnChannelChange -> _state.update { it.copy(newChannel = action.channel) }
            Subscriptions.Action.Create -> createSubscription()
            Subscriptions.Action.ToggleCreateDialog -> _state.update { it.copy(isCreating = !it.isCreating) }
        }
    }

    private fun refresh() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            repository.getSubscriptions()
                .onSuccess { subscriptions ->
                    _state.update { it.copy(isLoading = false, subscriptions = subscriptions) }
                }
                .onFailure { error ->
                    _state.update { it.copy(isLoading = false, error = error.toDetailedMessage("Failed to load subscriptions")) }
                }
        }
    }

    private fun deleteSubscription(id: String) {
        viewModelScope.launch {
            repository.deleteSubscription(id)
                .onSuccess {
                    _event.emit(Subscriptions.Event.ShowMessage("Успішно видалено"))
                    refresh()
                }
                .onFailure { error ->
                    _event.emit(Subscriptions.Event.ShowMessage(error.toDetailedMessage("Failed to delete")))
                }
        }
    }

    private fun createSubscription() {
        viewModelScope.launch {
            val channel = _state.value.newChannel
            if (channel.isBlank()) return@launch
            
            repository.createSubscription(channel)
                .onSuccess {
                    _state.update { it.copy(isCreating = false, newChannel = "") }
                    _event.emit(Subscriptions.Event.ShowMessage("Підписку створено"))
                    refresh()
                }
                .onFailure { error ->
                    _event.emit(Subscriptions.Event.ShowMessage(error.toDetailedMessage("Creation failed")))
                }
        }
    }
}
