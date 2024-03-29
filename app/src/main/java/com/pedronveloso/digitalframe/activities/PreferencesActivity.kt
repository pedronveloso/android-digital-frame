package com.pedronveloso.digitalframe.activities

import android.app.DatePickerDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.pedronveloso.digitalframe.BuildConfig
import com.pedronveloso.digitalframe.R
import com.pedronveloso.digitalframe.elements.clock.RealClockData
import com.pedronveloso.digitalframe.elements.countdown.RealCountdownData
import com.pedronveloso.digitalframe.elements.general.RealGeneralData
import com.pedronveloso.digitalframe.elements.weather.RealWeatherData
import com.pedronveloso.digitalframe.persistence.SharedPreferencesPersistence
import com.pedronveloso.digitalframe.preferences.InputType
import com.pedronveloso.digitalframe.preferences.PreferenceItem
import com.pedronveloso.digitalframe.preferences.PreferencesRoot
import com.pedronveloso.digitalframe.preferences.PreferencesSection
import com.pedronveloso.digitalframe.ui.DigitalFrameTheme
import com.pedronveloso.digitalframe.ui.MyTypography
import java.text.SimpleDateFormat
import java.time.MonthDay
import java.time.Year
import java.time.ZoneId
import java.util.Calendar
import java.util.Locale

class PreferencesActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val dataPersistence = SharedPreferencesPersistence(this)

        val topLevelPrefs = PreferencesRoot.Builder()
        addGeneralMenuSection(topLevelPrefs, dataPersistence)
        addBackgroundMenuSection(topLevelPrefs)
        addClockMenuSection(topLevelPrefs, dataPersistence)
        addCountdownMenuSection(topLevelPrefs, dataPersistence)
        addWeatherMenuSection(topLevelPrefs, dataPersistence)
        addAboutSection(topLevelPrefs)

        setContent {
            DigitalFrameTheme {
                PreferencesNavigation(topLevelPrefs.build())
            }
        }
    }

    private fun addClockMenuSection(
        topLevelPrefs: PreferencesRoot.Builder,
        dataPersistence: SharedPreferencesPersistence,
    ) {
        val clockData = RealClockData(dataPersistence)
        val clockSection = PreferencesSection.Builder("clock", getString(R.string.pref_clock_title))

        val use24HClock =
            PreferenceItem.SwitchPref(
                id = "use_24h_format",
                title = getString(R.string.pref_clock_24h_title),
                description = getString(R.string.pref_clock_24h_description),
                initialValueProvider = { clockData.use24HClock() },
            ).apply {
                onChangeCallback = { enabled ->
                    clockData.setUse24HClock(enabled)
                }
            }

        val showYearPreference =
            PreferenceItem.SwitchPref(
                id = "show_year",
                title = getString(R.string.pref_clock_show_year),
                initialValueProvider = { clockData.showYear() },
            ).apply {
                onChangeCallback = { enabled ->
                    clockData.setShowYear(enabled)
                }
            }

        clockSection.addPreference(use24HClock)
        clockSection.addPreference(showYearPreference)
        topLevelPrefs.addSection(clockSection.build())
    }

    private fun addBackgroundMenuSection(topLevelPrefs: PreferencesRoot.Builder) {
        val backgroundSection =
            PreferencesSection.Builder("background", getString(R.string.pref_bg_title))
        val pickBackgroundImagesBtn =
            PreferenceItem.Button(
                id = "pick_background_images",
                label = getString(R.string.pref_bg_photo_picker),
                action = {
                    startActivity(
                        Intent(
                            this@PreferencesActivity,
                            BackgroundPickerActivity::class.java,
                        ),
                    )
                },
            )

        backgroundSection.addPreference(pickBackgroundImagesBtn)
        topLevelPrefs.addSection(backgroundSection.build())
    }

    private fun addCountdownMenuSection(
        topLevelPrefs: PreferencesRoot.Builder,
        dataPersistence: SharedPreferencesPersistence,
    ) {
        val countdownData = RealCountdownData(dataPersistence)
        val countdownSection =
            PreferencesSection.Builder("countdown", getString(R.string.pref_countdown_title))
        val daysRemainingInput =
            PreferenceItem.InputFieldPref(
                id = "days_remaining",
                title = getString(R.string.pref_countdown_days_remaining),
                type = InputType.DATE,
                initialValueProvider = { countdownData.getTargetDate().toString() },
                onChangeCallback = { value ->
                    val dateValue = SimpleDateFormat("MMM d, yyyy", Locale.getDefault()).parse(value)
                    countdownData.setTargetDate(
                        dateValue.toInstant().atZone(ZoneId.systemDefault()).toLocalDate(),
                    )
                },
            )

        val countdownMessageInput =
            PreferenceItem.InputFieldPref(
                id = "countdown_message",
                title = getString(R.string.pref_countdown_message),
                type = InputType.TEXT,
                initialValueProvider = { countdownData.getMessage() },
                onChangeCallback = { value ->
                    countdownData.setMessage(value)
                },
            )

        countdownSection.addPreference(daysRemainingInput)
        countdownSection.addPreference(countdownMessageInput)
        topLevelPrefs.addSection(countdownSection.build())
    }

    private fun addWeatherMenuSection(
        topLevelPrefs: PreferencesRoot.Builder,
        dataPersistence: SharedPreferencesPersistence,
    ) {
        val weatherSection =
            PreferencesSection.Builder("weather", getString(R.string.pref_weather_title))
        val weatherData = RealWeatherData(dataPersistence)

        val useCelsiusPreference =
            PreferenceItem.SwitchPref(
                id = "use_celsius",
                title = getString(R.string.pref_weather_use_celcius),
                description = getString(R.string.pref_weather_use_celcius_description),
                initialValueProvider = { weatherData.useCelsius() },
            ).apply {
                onChangeCallback = { enabled ->
                    weatherData.setUseCelsius(enabled)
                }
            }

        val showWindSpeedPreference =
            PreferenceItem.SwitchPref(
                id = "show_wind_speed",
                title = getString(R.string.pref_weather_show_wind_speed),
                initialValueProvider = { weatherData.showWind() },
            ).apply {
                onChangeCallback = { enabled ->
                    weatherData.setShowWind(enabled)
                }
            }

        weatherSection.addPreference(useCelsiusPreference)
        weatherSection.addPreference(showWindSpeedPreference)
        topLevelPrefs.addSection(weatherSection.build())
    }

    private fun addGeneralMenuSection(
        topLevelPrefs: PreferencesRoot.Builder,
        dataPersistence: SharedPreferencesPersistence,
    ) {
        val generalSection =
            PreferencesSection.Builder("general", getString(R.string.pref_general_title))
        val generalData = RealGeneralData(dataPersistence)

        val allowCrashCollection =
            PreferenceItem.SwitchPref(
                id = "allow_crash_collection",
                title = getString(R.string.pref_general_crash_reports),
                description = getString(R.string.pref_general_crash_reports_description),
                initialValueProvider = { !generalData.explicitlyDisabledCrashCollection() },
            ).apply {
                onChangeCallback = { enabled ->
                    FirebaseCrashlytics.getInstance().setCrashlyticsCollectionEnabled(enabled)
                    generalData.setExplicitlyDisabledCrashCollection(!enabled)
                }
            }

        val locationDescriptionLabel =
            PreferenceItem.Label(
                id = "location_description",
                text = getString(R.string.pref_general_location),
            )

        val latitudeInput =
            PreferenceItem.InputFieldPref(
                id = "latitude",
                title = getString(R.string.pref_general_lat),
                type = InputType.TEXT,
                initialValueProvider = { generalData.lat() },
                onChangeCallback = { value ->
                    generalData.setLat(value)
                },
            )

        val longitudeInput =
            PreferenceItem.InputFieldPref(
                id = "longitude",
                title = getString(R.string.pref_general_lon),
                type = InputType.TEXT,
                initialValueProvider = { generalData.lon() },
                onChangeCallback = { value ->
                    generalData.setLon(value)
                },
            )

        generalSection.addPreference(locationDescriptionLabel)
        generalSection.addPreference(latitudeInput)
        generalSection.addPreference(longitudeInput)
        generalSection.addPreference(allowCrashCollection)
        topLevelPrefs.addSection(generalSection.build())
    }

    private fun addAboutSection(topLevelPrefs: PreferencesRoot.Builder) {
        val aboutSection = PreferencesSection.Builder("about", getString(R.string.pref_about_title))
        val aboutPreference =
            PreferenceItem.Label(
                id = "about",
                text = getString(R.string.pref_about_intro),
            )

        val aboutPageButton =
            PreferenceItem.Button(
                id = "about_page",
                label = getString(R.string.pref_about_button),
                action = {
                    val url = "https://github.com/pedronveloso/android-digital-frame"
                    val intent = Intent(Intent.ACTION_VIEW)
                    intent.data = Uri.parse(url)
                    startActivity(intent)

                },
            )
        aboutSection.addPreference(aboutPreference)
        aboutSection.addPreference(aboutPageButton)
        topLevelPrefs.addSection(aboutSection.build())
    }
}

@Composable
fun PreferenceSectionsScreen(
    sections: List<PreferencesSection>,
    navigateToSection: (PreferencesSection) -> Unit,
) {
    Column(modifier = Modifier.fillMaxSize()) {
        LazyColumn(modifier = Modifier.weight(1f)) {
            itemsIndexed(sections) { index, section ->
                PreferenceSectionItem(
                    section = section,
                    navigateToSection = { navigateToSection(section) },
                )
                if (index < sections.size - 1) {
                    HorizontalDivider()
                }
            }
        }
        AppVersionFooter()
    }
}

@Composable
fun AppVersionFooter() {
    Column(
        modifier =
        Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = BuildConfig.VERSION_NAME,
            style = MyTypography.bodyMedium,
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
        },
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = "sectionsList",
            Modifier.padding(padding),
        ) {
            composable("sectionsList") {
                PreferenceSectionsScreen(sections = preferences.sections) { section: PreferencesSection ->
                    navController.navigate("sectionDetails/${section.title}")
                }
            }
            composable(
                route = "sectionDetails/{sectionTitle}",
                arguments = listOf(navArgument("sectionTitle") { type = NavType.StringType }),
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
fun PreferenceSectionItem(
    section: PreferencesSection,
    navigateToSection: () -> Unit,
) {
    Row(
        modifier =
        Modifier
            .fillMaxWidth()
            .clickable(onClick = navigateToSection)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
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
                is PreferenceItem.Label -> LabelPreferenceComposable(preference)
                is PreferenceItem.InputFieldPref -> InputFieldPreferenceComposable(preference)
                is PreferenceItem.SwitchPref -> SwitchPreferenceComposable(preference)
                is PreferenceItem.Button -> ButtonPreferenceComposable(preference)
            }
        }
    }
}

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
                            SimpleDateFormat("MMM d, yyyy", Locale.getDefault()).format(calendar.time)
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
            Text(text = preference.title, style = MyTypography.titleMedium)
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
