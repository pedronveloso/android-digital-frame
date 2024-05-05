package com.pedronveloso.digitalframe.ui.preferences

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.pedronveloso.digitalframe.ui.MyTypography

@Composable
fun PreferenceTitle(title: String) {
    Text(text = title, style = MyTypography.titleSmall)
    Spacer(modifier = Modifier.height(4.dp))
}