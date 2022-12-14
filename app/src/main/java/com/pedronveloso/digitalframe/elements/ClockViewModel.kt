package com.pedronveloso.digitalframe.elements

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pedronveloso.digitalframe.ui.MyTypography
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.Locale
import kotlin.time.Duration.Companion.seconds

class ClockViewModel(
    private val savedState: SavedStateHandle
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
    fun RenderClock(use24HClock: Boolean = false) {
        Column(
            Modifier
                .padding(32.dp)
                .fillMaxWidth()
        ) {
            val formatter: DateTimeFormatter =
                if (use24HClock) {
                    DateTimeFormatter.ofPattern("HH:mm:ss")
                } else {
                    DateTimeFormatter.ofPattern("hh:mm a")
                }
            Text(text = formatter.format(currentTime), style = MyTypography.displayLarge.copy(color = Color.White, shadow = Shadow(color = Color.Black, offset = Offset(0f, 2f), blurRadius = 1f)))
            Text(
                text = currentTime.format(
                    DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM)
                        .withLocale(Locale.getDefault())
                ),
                style = MyTypography.titleLarge.copy(color = Color.White, shadow = Shadow(color = Color.Black, offset = Offset(0f, 2f), blurRadius = 1f))
            )
        }
    }
}
