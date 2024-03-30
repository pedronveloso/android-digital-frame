package com.pedronveloso.digitalframe.ui.preferences

import android.app.DatePickerDialog
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.pedronveloso.digitalframe.R
import com.pedronveloso.digitalframe.preferences.InputType
import com.pedronveloso.digitalframe.preferences.PreferenceItem
import com.pedronveloso.digitalframe.ui.MyTypography
import java.text.SimpleDateFormat
import java.time.MonthDay
import java.time.Year
import java.util.Calendar
import java.util.Locale

@Composable
fun InputFieldPreferenceComposable(preference: PreferenceItem.InputFieldPref) {
    var text by remember { mutableStateOf(preference.initialValueProvider.invoke()) }
    val context = LocalContext.current

    Column(modifier = Modifier.padding(16.dp)) {
        Text(text = preference.title, style = MyTypography.bodyLarge)

        if (preference.type != InputType.DATE) {
            // Existing TextField for types other than DATE.
            TextField(
                value = text,
                onValueChange = { text = it },
                placeholder = { Text(preference.hint ?: "") },
                keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
                keyboardActions =
                KeyboardActions(onDone = {
                    preference.onChangeCallback?.invoke(text)
                }),
                singleLine = true,
                modifier =
                Modifier
                    .fillMaxWidth()
                    .onFocusChanged { focusState ->
                        if (!focusState.isFocused) {
                            preference.onChangeCallback?.invoke(text)
                        }
                    },
            )
        } else {
            // Button that triggers a DatePicker dialog for DATE type.
            val datePickerDialog =
                remember {
                    DatePickerDialog(context, { _, year, month, dayOfMonth ->
                        val calendar =
                            Calendar.getInstance().apply {
                                set(year, month, dayOfMonth)
                            }
                        text =
                            SimpleDateFormat(
                                "MMM d, yyyy",
                                Locale.getDefault()
                            ).format(calendar.time)
                        preference.onChangeCallback?.invoke(text)
                    }, Year.now().value, MonthDay.now().monthValue - 1, MonthDay.now().dayOfMonth)
                }

            OutlinedButton(onClick = {
                datePickerDialog.show()
            }) {
                Text(text = if (text.isBlank()) stringResource(id = R.string.pick_date_title) else text)
            }
        }
    }
}