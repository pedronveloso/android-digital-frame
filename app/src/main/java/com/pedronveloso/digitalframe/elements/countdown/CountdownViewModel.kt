package com.pedronveloso.digitalframe.elements.countdown

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pedronveloso.digitalframe.R
import com.pedronveloso.digitalframe.ui.FadingComposable
import com.pedronveloso.digitalframe.ui.FontStyles
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.temporal.ChronoUnit
import kotlin.time.Duration.Companion.minutes

class CountdownViewModel : ViewModel() {
    private var daysUntilEvent by mutableLongStateOf(0)

    private var executionJob: Job? = null
    private var startedRepeatedExecution = false

    private fun repeatedExecution(countdownPersistence: CountdownPersistence) {
        executionJob?.cancel()
        executionJob = viewModelScope.launch {
            val today = LocalDate.now()
            val targetDate = countdownPersistence.getTargetDate()
            daysUntilEvent = ChronoUnit.DAYS.between(today, targetDate)
            delay(1.minutes)
            repeatedExecution(countdownPersistence)
        }
    }

    @Composable
    fun CountdownDisplay(
        countdownPersistence: CountdownPersistence,
        hudColor: Color,
    ) {
        if (!startedRepeatedExecution) {
            startedRepeatedExecution = true
            repeatedExecution(countdownPersistence)
        }
        FadingComposable {
            Column(
                Modifier
                    .padding(32.dp)
                    .fillMaxWidth()
                    .fillMaxHeight(),
                verticalArrangement = Arrangement.Bottom,
                horizontalAlignment = Alignment.Start,
            ) {
                CountdownText(
                    daysUntil = daysUntilEvent,
                    countdownPersistence,
                    hudColor = hudColor,
                )
            }
        }
    }

    @Composable
    fun CountdownText(
        daysUntil: Long,
        countdownPersistence: CountdownPersistence,
        hudColor: Color,
    ) {
        if (daysUntil >= 0) {
            Column(
                Modifier
                    .fillMaxWidth(),
            ) {
                Text(
                    text = stringResource(id = R.string.countdown_days_remaining, daysUntil),
                    style = FontStyles.textStyleDisplayMedium(hudColor),
                )
                Text(
                    text = countdownPersistence.getMessage().uppercase(),
                    style = FontStyles.textStyleBodyLarge(hudColor),
                )
            }
        }
    }
}
