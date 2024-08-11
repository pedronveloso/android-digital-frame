package com.pedronveloso.digitalframe.elements.countdown

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.unit.dp
import com.pedronveloso.digitalframe.R
import com.pedronveloso.digitalframe.ui.FadingComposable
import com.pedronveloso.digitalframe.ui.FontStyles

@Composable
fun CountdownDisplay(
    viewModel: CountdownViewModel,
    countdownPersistence: CountdownPersistence,
    hudColor: Color,
) {
    viewModel.startRepeatedExecution(countdownPersistence)
    val daysUntilEvent by viewModel.daysUntilEvent.collectAsState()

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
                text =
                    pluralStringResource(
                        id = R.plurals.countdown_days_remaining,
                        daysUntil.toInt(),
                        daysUntil.toInt(),
                    ),
                style = FontStyles.textStyleDisplayMedium(hudColor),
            )
            Text(
                text = countdownPersistence.getMessage().uppercase(),
                style = FontStyles.textStyleBodyLarge(hudColor),
            )
        }
    }
}
