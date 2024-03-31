package com.pedronveloso.digitalframe.elements.countdown

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.temporal.ChronoUnit
import kotlin.time.Duration.Companion.minutes

class CountdownViewModel : ViewModel() {
    private val _daysUntilEvent = MutableStateFlow(-1L)
    val daysUntilEvent: StateFlow<Long> = _daysUntilEvent.asStateFlow()

    private var executionJob: Job? = null
    private var startedRepeatedExecution = false

    private fun repeatedExecution(countdownPersistence: CountdownPersistence) {
        executionJob?.cancel()
        executionJob = viewModelScope.launch {
            val today = LocalDate.now()
            val targetDate = countdownPersistence.getTargetDate()
            _daysUntilEvent.value = ChronoUnit.DAYS.between(today, targetDate)
            delay(1.minutes)
            repeatedExecution(countdownPersistence)
        }
    }

    fun startRepeatedExecution(countdownPersistence: CountdownPersistence) {
        if (!startedRepeatedExecution) {
            startedRepeatedExecution = true
            repeatedExecution(countdownPersistence)
        }
    }


}
