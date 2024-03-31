package com.pedronveloso.digitalframe

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowInsets
import android.view.WindowInsetsController
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.pedronveloso.digitalframe.activities.PreferencesActivity
import com.pedronveloso.digitalframe.elements.background.AlbumBackground
import com.pedronveloso.digitalframe.elements.background.BackgroundAlbumViewModel
import com.pedronveloso.digitalframe.elements.clock.ClockViewModel
import com.pedronveloso.digitalframe.elements.clock.RealClockPersistence
import com.pedronveloso.digitalframe.elements.clock.RenderClock
import com.pedronveloso.digitalframe.elements.countdown.CountdownDisplay
import com.pedronveloso.digitalframe.elements.countdown.CountdownViewModel
import com.pedronveloso.digitalframe.elements.countdown.RealCountdownPersistence
import com.pedronveloso.digitalframe.elements.general.RealGeneralDataPersistence
import com.pedronveloso.digitalframe.elements.weather.RealWeatherPersistence
import com.pedronveloso.digitalframe.elements.weather.RenderWeather
import com.pedronveloso.digitalframe.elements.weather.WeatherViewModel
import com.pedronveloso.digitalframe.persistence.SharedPreferencesPersistence
import com.pedronveloso.digitalframe.ui.DigitalFrameTheme
import com.pedronveloso.digitalframe.ui.MyTypography
import com.pedronveloso.digitalframe.ui.deriveHUDColor
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) {
            initialToggleImmersiveMode()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Keep Screen On.
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        setContent {
            DigitalFrameTheme {
                val context = LocalContext.current
                val sharedPrefsPersistence = SharedPreferencesPersistence(context)
                val generalDataPersistence = RealGeneralDataPersistence(sharedPrefsPersistence)
                var userPromptedForCrashCollection by remember {
                    mutableStateOf(
                        generalDataPersistence.userPromptedForCrashCollection()
                    )
                }

                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background,
                ) {
                    hideSystemUI(LocalView.current)
                    if (userPromptedForCrashCollection.not()) {
                        ShowCrashCollectionNotice { userChoice ->
                            userPromptedForCrashCollection = true
                            generalDataPersistence.setUserPromptedForCrashCollection(userChoice)
                        }
                    } else {
                        DigitalAlbumScreen(
                            photosBackgroundViewModel = viewModel(),
                            clockViewModel = viewModel(),
                            weatherViewModel = viewModel(),
                            countdownViewModel = viewModel(),
                            sharedPrefsPersistence,
                            generalDataPersistence
                        )
                    }
                }
            }
        }
    }

    @Suppress("DEPRECATION")
    private fun initialToggleImmersiveMode() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R) {
            window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_FULLSCREEN
                    or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN)
        } else {
            window?.insetsController?.let {
                it.hide(WindowInsets.Type.statusBars() or WindowInsets.Type.navigationBars())
                it.systemBarsBehavior = WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            }
        }
    }

    private fun hideSystemUI(view: View) {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        WindowInsetsControllerCompat(window, view).let { controller ->
            controller.hide(WindowInsetsCompat.Type.systemBars())
            controller.systemBarsBehavior =
                WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }
    }
}

@Composable
fun ShowCrashCollectionNotice(userPickedCrashCollection: (Boolean) -> Unit) {
    var crashCollectionEnabled by remember { mutableStateOf(true) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stringResource(id = R.string.crash_data_collection_title),
                style = MyTypography.titleLarge,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 32.dp)
            )

            Text(
                text = stringResource(id = R.string.crash_data_collection_description),
                style = MyTypography.bodyLarge,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 16.dp)
            )

            Spacer(modifier = Modifier.weight(1f))

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(id = R.string.pref_general_crash_reports),
                    style = MyTypography.bodyLarge,
                    modifier = Modifier.padding(end = 16.dp)
                )

                Switch(
                    checked = crashCollectionEnabled,
                    onCheckedChange = { crashCollectionEnabled = it },
                    modifier = Modifier.padding(start = 16.dp)
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = {
                    userPickedCrashCollection(crashCollectionEnabled)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 32.dp)
            ) {
                Text(text = stringResource(id = R.string.save_btn))
            }
        }
    }
}


@Composable
fun DigitalAlbumScreen(
    photosBackgroundViewModel: BackgroundAlbumViewModel,
    clockViewModel: ClockViewModel,
    weatherViewModel: WeatherViewModel,
    countdownViewModel: CountdownViewModel,
    persistence: SharedPreferencesPersistence,
    generalDataPersistence: RealGeneralDataPersistence
) {
    var showButton by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current

    // Other Plugin data sources.
    val clockPersistence = RealClockPersistence(persistence)
    val countdownPersistence = RealCountdownPersistence(persistence)
    val weatherPersistence = RealWeatherPersistence(persistence)

    val backgroundHsl by photosBackgroundViewModel.hsl.collectAsState()

    Box(
        modifier =
        Modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .pointerInput(Unit) {
                detectTapGestures(
                    onPress = {
                        showButton = true
                    },
                )
            },
    ) {
        val hudColor = deriveHUDColor(backgroundHsl)
        AlbumBackground(viewModel = photosBackgroundViewModel)
        RenderClock(
            clockViewModel = clockViewModel,
            clockPersistence = clockPersistence,
            hudColor = hudColor
        )
        RenderWeather(weatherViewModel, weatherPersistence, generalDataPersistence, hudColor)
        CountdownDisplay(countdownViewModel, countdownPersistence, hudColor)

        // Fading Button
        AnimatedVisibility(
            visible = showButton,
            enter = fadeIn(animationSpec = tween(durationMillis = 300)),
            exit = fadeOut(animationSpec = tween(durationMillis = 300)),
        ) {
            Button(
                onClick = {
                    context.startActivity(Intent(context, PreferencesActivity::class.java))
                },
                modifier =
                Modifier
                    .align(Alignment.Center)
                    .padding(16.dp),
            ) {
                Text(stringResource(id = R.string.settings_btn))
            }
        }

        // Automatically hide the button after 5 seconds
        LaunchedEffect(showButton) {
            if (showButton) {
                coroutineScope.launch {
                    delay(5000)
                    showButton = false
                }
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun CrashCollectionNotice() {
    DigitalFrameTheme {
        ShowCrashCollectionNotice { }
    }
}