package com.pedronveloso.digitalframe.elements

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pedronveloso.digitalframe.ui.FadingComposable
import com.pedronveloso.digitalframe.ui.MyTypography
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.time.temporal.ChronoUnit
import java.util.Locale
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds

class CountdownViewModel(
    private val savedState: SavedStateHandle
) : ViewModel() {

    private var daysUntilEvent by mutableLongStateOf(0)
    private var newXDrift by mutableIntStateOf(0)
    private var newYDrift by mutableIntStateOf(0)

    init {
        repeatedExecution()
    }

    private fun repeatedExecution() {
        viewModelScope.launch {
            val today = LocalDate.now()
            val targetDate = LocalDate.of(today.year, 2, 13)
            daysUntilEvent = ChronoUnit.DAYS.between(today, targetDate)
            delay(1.minutes)
            repeatedExecution()
        }
    }


    @Composable
    fun CountdownDisplay() {
        FadingComposable {
            Column(
                Modifier
                    .padding(32.dp)
                    .fillMaxWidth()
                    .fillMaxHeight()
                    .offset(x = newXDrift.dp, y = newYDrift.dp),
                verticalArrangement = Arrangement.Bottom,
                horizontalAlignment = Alignment.Start
            ) {
                CountdownText(daysUntil = daysUntilEvent)
            }
        }
    }

    @Composable
    fun CountdownText(daysUntil: Long) {
        if (daysUntil >= 0) {
            Column(
                Modifier
                    .fillMaxWidth()
                    .offset(x = newXDrift.dp, y = newYDrift.dp)
            ) {
                Text(
                    text = "DAYS UNTIL 2ND TRIMESTER",
                    style = MyTypography.titleLarge.copy(
                        color = Color.White,
                        shadow = Shadow(
                            color = Color.Black,
                            offset = Offset(0f, 2f),
                            blurRadius = 1f
                        )
                    )
                )
                Text(
                    text = "$daysUntil Days",
                    style = MyTypography.displaySmall.copy(
                        color = Color.White,
                        shadow = Shadow(
                            color = Color.Black,
                            offset = Offset(0f, 2f),
                            blurRadius = 1f
                        )
                    )
                )
            }
        }
    }

}
