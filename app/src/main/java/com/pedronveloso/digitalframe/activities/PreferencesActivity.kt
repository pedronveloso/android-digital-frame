package com.pedronveloso.digitalframe.activities

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.pedronveloso.digitalframe.BuildConfig
import com.pedronveloso.digitalframe.R
import com.pedronveloso.digitalframe.elements.clock.ClockData
import com.pedronveloso.digitalframe.elements.countdown.CountdownData
import com.pedronveloso.digitalframe.persistence.SharedPreferencesPersistence
import com.pedronveloso.digitalframe.preferences.InputType
import com.pedronveloso.digitalframe.preferences.PreferenceItem
import com.pedronveloso.digitalframe.preferences.PreferencesRoot
import com.pedronveloso.digitalframe.preferences.PreferencesSection
import com.pedronveloso.digitalframe.ui.DigitalFrameTheme
import com.pedronveloso.digitalframe.ui.MyTypography

class PreferencesActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val dataPersistence = SharedPreferencesPersistence(this)

        val topLevelPrefs = PreferencesRoot.Builder()
        backgroundMenuSection(topLevelPrefs)
        clockMenuSection(dataPersistence, topLevelPrefs)
        countdownMenuSection(dataPersistence, topLevelPrefs)

        setContent {
            DigitalFrameTheme {
                PreferencesNavigation(topLevelPrefs.build())
            }
        }
    }

    private fun clockMenuSection(
        dataPersistence: SharedPreferencesPersistence,
        topLevelPrefs: PreferencesRoot.Builder
    ) {
        val clockSection = PreferencesSection.Builder("clock", getString(R.string.pref_clock_title))
        val use24HClock = PreferenceItem.SwitchPref(
            id = "use_24h_format",
            title = getString(R.string.pref_clock_24h_title),
            description = getString(R.string.pref_clock_24h_description),
            initialValueProvider = { ClockData.use24HClock(dataPersistence) }
        ).apply {
            onChangeCallback = { enabled ->
                ClockData.setUse24HClock(dataPersistence, enabled)
            }
        }

        clockSection.addPreference(use24HClock)
        topLevelPrefs.addSection(clockSection.build())
    }

    private fun backgroundMenuSection(
        topLevelPrefs: PreferencesRoot.Builder
    ) {
        val backgroundSection =
            PreferencesSection.Builder("background", getString(R.string.pref_bg_title))
        val pickBackgroundImagesBtn = PreferenceItem.Button(
            id = "pick_background_images",
            label = getString(R.string.pref_bg_photo_picker),
            action = {
                startActivity(
                    Intent(
                        this@PreferencesActivity,
                        BackgroundPickerActivity::class.java
                    )
                )
            }
        )

        backgroundSection.addPreference(pickBackgroundImagesBtn)
        topLevelPrefs.addSection(backgroundSection.build())
    }

    private fun countdownMenuSection(
        dataPersistence: SharedPreferencesPersistence,
        topLevelPrefs: PreferencesRoot.Builder
    ) {
        val countdownSection =
            PreferencesSection.Builder("countdown", getString(R.string.pref_countdown_title))
        val daysRemainingInput = PreferenceItem.InputFieldPref(
            id = "days_remaining",
            sectionId = "countdown",
            title = getString(R.string.pref_countdown_days_remaining),
            type = InputType.INT,
            initialValueProvider = { CountdownData.getDaysRemaining(dataPersistence).toString() },
            onChangeCallback = { value ->
                val intValue = value.toIntOrNull() ?: 0
                CountdownData.setDaysRemaining(dataPersistence, intValue)
            }
        )

        val countdownMessageInput = PreferenceItem.InputFieldPref(
            id = "countdown_message",
            sectionId = "countdown",
            title = getString(R.string.pref_countdown_message),
            type = InputType.TEXT,
            initialValueProvider = { CountdownData.getMessage(dataPersistence) },
            onChangeCallback = { value ->
                CountdownData.setMessage(dataPersistence, value)
            }
        )


        countdownSection.addPreference(daysRemainingInput)
        countdownSection.addPreference(countdownMessageInput)
        topLevelPrefs.addSection(countdownSection.build())
    }
}

@Composable
fun PreferenceSectionsScreen(
    sections: List<PreferencesSection>,
    navigateToSection: (PreferencesSection) -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        LazyColumn(modifier = Modifier.weight(1f)) {
            itemsIndexed(sections) { index, section ->
                PreferenceSectionItem(
                    section = section,
                    navigateToSection = { navigateToSection(section) })
                if (index < sections.size - 1) {
                    Divider()
                }
            }
        }
        AppVersionFooter()
    }
}

@Composable
fun AppVersionFooter() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = BuildConfig.VERSION_NAME,
            style = MyTypography.bodyMedium
        )
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PreferencesNavigation(preferences: PreferencesRoot) {
    val navController = rememberNavController()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = stringResource(id = R.string.preferences_title)) },
            )
        }
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = "sectionsList",
            Modifier.padding(padding)
        ) {
            composable("sectionsList") {
                PreferenceSectionsScreen(sections = preferences.sections) { section: PreferencesSection ->
                    navController.navigate("sectionDetails/${section.title}")
                }
            }
            composable(
                route = "sectionDetails/{sectionTitle}",
                arguments = listOf(navArgument("sectionTitle") { type = NavType.StringType })
            ) { backStackEntry ->
                val sectionTitle = backStackEntry.arguments?.getString("sectionTitle")
                val section = preferences.sections.find { it.title == sectionTitle }
                section?.let {
                    RenderPreferences(it.preferenceItems)
                }
            }
        }
    }
}

@Composable
fun PreferenceSectionItem(section: PreferencesSection, navigateToSection: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = navigateToSection)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = section.title, modifier = Modifier.weight(1f), style = MyTypography.bodyLarge)
        Icon(Icons.Default.ArrowForward, contentDescription = "Go to section")
    }
}

@Composable
fun RenderPreferences(items: List<PreferenceItem>) {
    LazyColumn {
        items(items.size) { item ->
            when (val preference = items[item]) {
                is PreferenceItem.InputFieldPref -> InputFieldPreferenceComposable(preference)
                is PreferenceItem.SwitchPref -> SwitchPreferenceComposable(preference)
                is PreferenceItem.Button -> ButtonPreferenceComposable(preference)
            }
        }
    }
}

@Composable
fun InputFieldPreferenceComposable(preference: PreferenceItem.InputFieldPref) {
    var text by remember { mutableStateOf(preference.initialValueProvider.invoke()) }

    Column(modifier = Modifier.padding(16.dp)) {
        Text(text = preference.title, style = MyTypography.bodyLarge)
        TextField(
            value = text,
            onValueChange = { text = it },
            placeholder = { Text(preference.hint ?: "") },
            keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(
                onDone = {
                    preference.onChangeCallback?.invoke(text)
                }
            ),
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .onFocusChanged { focusState ->
                    if (!focusState.isFocused) {
                        preference.onChangeCallback?.invoke(text)
                    }
                }
        )
    }
}

@Composable
fun SwitchPreferenceComposable(preference: PreferenceItem.SwitchPref) {
    var isChecked by remember { mutableStateOf(preference.initialValueProvider.invoke()) }

    Row(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(text = preference.title, style = MyTypography.titleMedium)
            preference.description?.let {
                Text(text = it, style = MyTypography.bodyLarge)
            }
        }
        Switch(
            checked = isChecked,
            onCheckedChange = {
                isChecked = it
                preference.onChangeCallback?.invoke(it)
            }
        )
    }
}

@Composable
fun ButtonPreferenceComposable(preference: PreferenceItem.Button) {
    Button(
        onClick = preference.action,
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text(preference.label)
    }
}