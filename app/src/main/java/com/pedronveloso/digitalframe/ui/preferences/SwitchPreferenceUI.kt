package com.pedronveloso.digitalframe.ui.preferences

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.pedronveloso.digitalframe.preferences.PreferenceItem
import com.pedronveloso.digitalframe.ui.MyTypography

@Composable
fun SwitchPreferenceComposable(preference: PreferenceItem.SwitchPref) {
    var isChecked by remember { mutableStateOf(preference.initialValueProvider.invoke()) }

    Row(
        modifier =
        Modifier
            .padding(16.dp)
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(text = preference.title, style = MyTypography.titleSmall)

            preference.description?.let {
                Spacer(modifier = Modifier.height(6.dp))
                Text(text = it, style = MyTypography.bodyLarge)
            }
        }
        Switch(
            checked = isChecked,
            onCheckedChange = {
                isChecked = it
                preference.onChangeCallback?.invoke(it)
            },
        )
    }
}