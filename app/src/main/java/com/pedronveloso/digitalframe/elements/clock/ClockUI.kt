package com.pedronveloso.digitalframe.elements.clock

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.pedronveloso.digitalframe.ui.FadingComposable
import com.pedronveloso.digitalframe.ui.FontStyles
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.Locale

@Composable
fun RenderClock(
    clockViewModel: ClockViewModel,
    clockPersistence: ClockPersistence,
    hudColor: Color,
) {
    val currentTime by clockViewModel.currentTime.collectAsState()

    FadingComposable {
        Column(
            Modifier
                .padding(32.dp)
                .fillMaxWidth(),
        ) {
            val formatter: DateTimeFormatter =
                if (clockPersistence.use24HClock()) {
                    if (clockPersistence.showSeconds()) {
                        DateTimeFormatter.ofPattern("HH:mm:ss")
                    } else {
                        DateTimeFormatter.ofPattern("HH:mm")
                    }
                } else {
                    if (clockPersistence.showSeconds()) {
                        DateTimeFormatter.ofPattern("hh:mm:ss a")
                    } else {
                        DateTimeFormatter.ofPattern("hh:mm a")
                    }
                }
            Text(
                text = formatter.format(currentTime),
                style = FontStyles.textStyleDisplayLarge(hudColor),
            )

            if (clockPersistence.showYear()) {
                Text(
                    text =
                    currentTime.format(
                        DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM)
                            .withLocale(Locale.getDefault()),
                    ),
                    style = FontStyles.textStyleTitleLarge(hudColor),
                )
            } else {
                // A formatter that excludes the year.
                val formatterWithoutYear =
                    DateTimeFormatter.ofPattern("MMM d").withLocale(Locale.getDefault())
                Text(
                    text = currentTime.format(formatterWithoutYear),
                    style = FontStyles.textStyleTitleLarge(hudColor),
                )
            }
        }
    }
}
