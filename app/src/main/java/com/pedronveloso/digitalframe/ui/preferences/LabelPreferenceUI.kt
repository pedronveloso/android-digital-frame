package com.pedronveloso.digitalframe.ui.preferences

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.pedronveloso.digitalframe.preferences.PreferenceItem
import com.pedronveloso.digitalframe.ui.MyTypography

@Composable
fun LabelPreferenceComposable(label: PreferenceItem.Label) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label.text,
            textAlign = TextAlign.Center,
            style = MyTypography.bodyLarge
        )
    }
}
