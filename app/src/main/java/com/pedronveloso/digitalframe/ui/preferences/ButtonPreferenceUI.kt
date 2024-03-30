package com.pedronveloso.digitalframe.ui.preferences

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.pedronveloso.digitalframe.preferences.PreferenceItem

@Composable
fun ButtonPreferenceComposable(preference: PreferenceItem.Button) {
    Button(
        onClick = preference.action,
        modifier =
        Modifier
            .fillMaxWidth()
            .padding(16.dp),
    ) {
        Text(preference.label)
    }
}
