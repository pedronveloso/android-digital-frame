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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pedronveloso.digitalframe.R
import com.pedronveloso.digitalframe.ui.FadingComposable
import com.pedronveloso.digitalframe.ui.FontStyles
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.temporal.ChronoUnit
import kotlin.time.Duration.Companion.minutes

class CountdownViewModel(
    private val savedState: SavedStateHandle
) : ViewModel() {

    private var daysUntilEvent by mutableLongStateOf(0)

    private fun repeatedExecution(countdownData: CountdownData) {
        viewModelScope.launch {
            val today = LocalDate.now()
            val targetDate = countdownData.getTargetDate()
            daysUntilEvent = ChronoUnit.DAYS.between(today, targetDate)
            delay(1.minutes)
            repeatedExecution(countdownData)
        }
    }


    @Composable
    fun CountdownDisplay(countdownData: CountdownData, backgroundHsl: FloatArray) {
        repeatedExecution(countdownData)
        FadingComposable {
            Column(
                Modifier
                    .padding(32.dp)
                    .fillMaxWidth()
                    .fillMaxHeight(),
                verticalArrangement = Arrangement.Bottom,
                horizontalAlignment = Alignment.Start
            ) {
                CountdownText(
                    daysUntil = daysUntilEvent,
                    countdownData,
                    backgroundHsl = backgroundHsl
                )
            }
        }
    }

    @Composable
    fun CountdownText(daysUntil: Long, countdownData: CountdownData, backgroundHsl: FloatArray) {
        if (daysUntil >= 0) {
            Column(
                Modifier
                    .fillMaxWidth()
            ) {
                Text(
                    text = stringResource(id = R.string.countdown_days_remaining, daysUntil),
                    style = FontStyles.textStyleDisplayMedium(backgroundHsl)
                )
                Text(
                    text = countdownData.getMessage().uppercase(),
                    style = FontStyles.textStyleBodyLarge(backgroundHsl)
                )
            }
        }
    }

}
