package com.pedronveloso.digitalframe.elements.clock

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import kotlin.time.Duration.Companion.seconds

class ClockViewModel : ViewModel() {

    private val _currentTime = MutableStateFlow(LocalDateTime.now())
    val currentTime: StateFlow<LocalDateTime> = _currentTime.asStateFlow()

    init {
        repeatedExecution()
    }

    private fun repeatedExecution() {
        viewModelScope.launch {
            _currentTime.value = LocalDateTime.now()
            delay(1.seconds)
            repeatedExecution()
        }
    }

}
