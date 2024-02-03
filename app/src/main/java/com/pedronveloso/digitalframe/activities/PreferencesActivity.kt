package com.pedronveloso.digitalframe.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.pedronveloso.digitalframe.BuildConfig
import com.pedronveloso.digitalframe.R
import com.pedronveloso.digitalframe.preferences.PreferenceItem
import com.pedronveloso.digitalframe.preferences.PreferenceSection
import com.pedronveloso.digitalframe.preferences.Preferences
import com.pedronveloso.digitalframe.ui.DigitalFrameTheme
import com.pedronveloso.digitalframe.ui.MyTypography

class PreferencesActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        val preferences = Preferences {
            section(getString(R.string.pref_bg_title)) {
                button(label = getString(R.string.pref_bg_photo_picker)) {
                    startActivity(
                        Intent(
                            this@PreferencesActivity,
                            BackgroundPickerActivity::class.java
                        )
                    )
                }
            }

            // The following section is just for testing purposes.
            section("Notifications") {
                switch(
                    title = "Enable Notifications",
                    description = "Receive notifications for new messages",
                    default = true
                )
                switch(
                    title = "Sound",
                    description = "Play sound on notifications",
                    default = false
                )
                button(label = "Save Notification Settings") {
                    // Handle save action
                    Toast.makeText(
                        this@PreferencesActivity,
                        "Notification settings saved",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }

        setContent {
            DigitalFrameTheme {
                PreferencesNavigation(preferences)
            }
        }
    }

}

@Composable
fun PreferenceSectionsScreen(
    sections: List<PreferenceSection>,
    navigateToSection: (PreferenceSection) -> Unit
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
fun PreferencesNavigation(preferences: List<PreferenceSection>) {
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
                PreferenceSectionsScreen(sections = preferences) { section ->
                    navController.navigate("sectionDetails/${section.title}")
                }
            }
            composable(
                route = "sectionDetails/{sectionTitle}",
                arguments = listOf(navArgument("sectionTitle") { type = NavType.StringType })
            ) { backStackEntry ->
                val sectionTitle = backStackEntry.arguments?.getString("sectionTitle")
                val section = preferences.find { it.title == sectionTitle }
                section?.let {
                    RenderPreferences(it.preferences)
                }
            }
        }
    }
}

@Composable
fun PreferenceSectionItem(section: PreferenceSection, navigateToSection: () -> Unit) {
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
                is PreferenceItem.InputFieldPreference -> InputFieldPreferenceComposable(preference)
                is PreferenceItem.Switch -> SwitchPreferenceComposable(preference)
                is PreferenceItem.Button -> ButtonPreferenceComposable(preference)
            }
        }
    }
}

@Composable
fun InputFieldPreferenceComposable(preference: PreferenceItem.InputFieldPreference) {
    var text by remember { mutableStateOf("") }

    Column(modifier = Modifier.padding(16.dp)) {
        Text(text = preference.title, style = MyTypography.bodyLarge)
        TextField(
            value = text,
            onValueChange = { text = it },
            placeholder = { Text(preference.hint ?: "") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
fun SwitchPreferenceComposable(preference: PreferenceItem.Switch) {
    var isChecked by remember { mutableStateOf(preference.default) }

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
            onCheckedChange = { isChecked = it }
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