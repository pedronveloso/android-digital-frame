package com.pedronveloso.digitalframe.ui.preferences

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.pedronveloso.digitalframe.preferences.PreferenceItem
import com.pedronveloso.digitalframe.ui.DigitalFrameTheme
import com.pedronveloso.digitalframe.ui.MyTypography
import com.pedronveloso.digitalframe.ui.PurpleGrey40

@Composable
fun DropdownPreferenceUI(preference: PreferenceItem.DropdownPref) {
    var selectedOption by remember { mutableStateOf(preference.initialValueProvider.invoke()) }
    var expanded by remember { mutableStateOf(false) }

    Column(modifier = Modifier.padding(16.dp)) {
        PreferenceTitle(title = preference.title)

        Box(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .background(PurpleGrey40)
                    .padding(6.dp)
                    .clickable { expanded = !expanded },
        ) {
            Text(
                text = selectedOption,
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                style = MyTypography.bodyLarge,
            )

            Icon(
                imageVector = if (expanded) Icons.Default.KeyboardArrowUp else Icons.Default.ArrowDropDown,
                contentDescription = if (expanded) "Close dropdown" else "Open dropdown",
                modifier =
                    Modifier
                        .align(Alignment.CenterEnd)
                        .padding(end = 8.dp),
            )

            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
            ) {
                preference.options.forEachIndexed { index, option ->
                    DropdownMenuItem(
                        text = { Text(text = option, style = MyTypography.bodyLarge) },
                        onClick = {
                            selectedOption = option
                            preference.onChangeCallback?.invoke(index, option)
                            expanded = false
                        },
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DropdownPreferenceUIPreview() {
    val options = listOf("Option 1", "Option 2")
    val initialValue = options[0]
    val onChangeCallback: (Int, String) -> Unit = { _, _ -> }

    val dropdownPref =
        PreferenceItem.DropdownPref(
            id = "dropdown_pref",
            title = "Title goes here",
            options = options,
            initialValueProvider = { initialValue },
            onChangeCallback = onChangeCallback,
        )

    DigitalFrameTheme {
        DropdownPreferenceUI(preference = dropdownPref)
    }
}
