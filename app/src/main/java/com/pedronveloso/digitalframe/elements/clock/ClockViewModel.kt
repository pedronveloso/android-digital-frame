package com.pedronveloso.digitalframe.elements.clock

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pedronveloso.digitalframe.ui.FadingComposable
import com.pedronveloso.digitalframe.ui.FontStyles.textStyleDisplayLarge
import com.pedronveloso.digitalframe.ui.FontStyles.textStyleTitleLarge
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.Locale
import kotlin.time.Duration.Companion.seconds

class ClockViewModel(
    private val savedState: SavedStateHandle,
) : ViewModel() {
    private var currentTime by mutableStateOf(LocalDateTime.now())

    init {
        repeatedExecution()
    }

    private fun repeatedExecution() {
        viewModelScope.launch {
            currentTime = LocalDateTime.now()
            delay(1.seconds)
            repeatedExecution()
        }
    }

    @Composable
    fun RenderClock(
        clockData: ClockData,
        hudColor: Color,
    ) {
        FadingComposable {
            Column(
                Modifier
                    .padding(32.dp)
                    .fillMaxWidth(),
            ) {
                val formatter: DateTimeFormatter =
                    if (clockData.use24HClock()) {
                        DateTimeFormatter.ofPattern("HH:mm:ss")
                    } else {
                        DateTimeFormatter.ofPattern("hh:mm a")
                    }
                Text(
                    text = formatter.format(currentTime),
                    style = textStyleDisplayLarge(hudColor),
                )

                if (clockData.showYear()) {
                    Text(
                        text =
                            currentTime.format(
                                DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM)
                                    .withLocale(Locale.getDefault()),
                            ),
                        style = textStyleTitleLarge(hudColor),
                    )
                } else {
                    // A formatter that excludes the year.
                    val formatterWithoutYear =
                        DateTimeFormatter.ofPattern("MMM d").withLocale(Locale.getDefault())
                    Text(
                        text = currentTime.format(formatterWithoutYear),
                        style = textStyleTitleLarge(hudColor),
                    )
                }
            }
        }
    }
}
