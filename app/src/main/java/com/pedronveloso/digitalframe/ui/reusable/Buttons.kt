package com.pedronveloso.digitalframe.ui.reusable

import androidx.compose.material.icons.Icons
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import kotlinx.coroutines.delay
import androidx.compose.foundation.layout.size
import androidx.compose.ui.unit.dp

@Composable
fun SaveButton(
    onSave: () -> Unit,
    enabled: Boolean,
    buttonText: String,
    modifier: Modifier = Modifier
) {
    var showCheckmark by remember { mutableStateOf(false) }

    Button(
        onClick = {
            onSave()
            showCheckmark = true
        },
        enabled = enabled,
        modifier = modifier
    ) {
        if (showCheckmark) {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = "Checkmark",
                modifier = Modifier.size(24.dp)
            )
        } else {
            Text(buttonText)
        }
    }

    // Revert the button contents after a delay.
    LaunchedEffect(showCheckmark) {
        if (showCheckmark) {
            delay(1000)
            showCheckmark = false
        }
    }
}