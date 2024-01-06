package com.pedronveloso.digitalframe.activities

import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import android.os.Bundle
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import com.pedronveloso.digitalframe.ui.MyTypography

class PreferencesActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PreferencesScreen()
        }
    }
}

@Composable
fun PreferencesScreen() {
    Column {
        PreferenceItem("Pick background photos") {
            // Handle click for "Pick background photos"
        }
        Divider()
        PreferenceItem("Countdown Settings") {
            // Handle click for "Countdown Settings"
        }
    }
}

@Composable
fun PreferenceItem(text: String, onClick: () -> Unit) {
    Text(
        text = text,
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(16.dp),
        style = MyTypography.bodyLarge.copy()
    )
}