package com.example.radiation.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.radiation.extension.toDetailedMessage
import com.example.radiation.repository.MainRepository
import com.example.radiation.ui.screen.readings.Readings
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ReadingsViewModel @Inject constructor(
    private val repository: MainRepository
) : ViewModel() {

    private val _state = MutableStateFlow<Readings.State>(Readings.State.Loading)
    val state = _state.asStateFlow()

    private val _event = MutableSharedFlow<Readings.Event>()
    val event = _event.asSharedFlow()

    private var currentDeviceId: String? = null

    fun onAction(action: Readings.Action) {
        when (action) {
            is Readings.Action.Load -> {
                currentDeviceId = action.deviceId
                loadReadings(action.deviceId)
            }
            Readings.Action.Refresh -> {
                currentDeviceId?.let { loadReadings(it) }
            }
            Readings.Action.Back -> {
                viewModelScope.launch { _event.emit(Readings.Event.NavigateBack) }
            }
        }
    }

    private fun loadReadings(deviceId: String) {
        viewModelScope.launch {
            _state.value = Readings.State.Loading
            repository.getReadings(deviceId = deviceId)
                .onSuccess { readings ->
                    _state.value = Readings.State.Content(
                        readings = readings,
                        deviceId = deviceId
                    )
                }
                .onFailure { error ->
                    _state.value = Readings.State.Error(
                        message = error.toDetailedMessage("Failed to load readings")
                    )
                }
        }
    }
}

