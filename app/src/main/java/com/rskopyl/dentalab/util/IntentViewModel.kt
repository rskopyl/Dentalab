package com.rskopyl.dentalab.util

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.SendChannel
import kotlinx.coroutines.channels.actor
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow

abstract class IntentViewModel<S, E, I>(initialState: S) : ViewModel() {

    protected val _state = MutableStateFlow(initialState)
    val state get() = _state.asStateFlow()

    protected val _events = Channel<E>(capacity = BUFFER_CAPACITY)
    val events get() = _events.receiveAsFlow()

    val intents: SendChannel<I> = viewModelScope.actor(
        capacity = BUFFER_CAPACITY
    ) {
        for (intent in channel) {
            handleIntent(intent)
        }
    }

    protected abstract suspend fun handleIntent(intent: I)

    companion object {

        private const val BUFFER_CAPACITY = 64
    }
}