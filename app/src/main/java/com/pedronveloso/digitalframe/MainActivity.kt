package com.pedronveloso.digitalframe

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
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
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.pedronveloso.digitalframe.activities.PreferencesActivity
import com.pedronveloso.digitalframe.elements.ClockViewModel
import com.pedronveloso.digitalframe.elements.CountdownViewModel
import com.pedronveloso.digitalframe.elements.PhotosBackgroundViewModel
import com.pedronveloso.digitalframe.elements.RenderBackground
import com.pedronveloso.digitalframe.elements.WeatherViewModel
import com.pedronveloso.digitalframe.ui.DigitalFrameTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Keep Screen On.
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        setContent {
            DigitalFrameTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    hideSystemUI(LocalView.current)
                    MainScreen(
                        photosBackgroundViewModel = viewModel(),
                        clockViewModel = viewModel(),
                        weatherViewModel = viewModel(),
                        countdownViewModel = viewModel(),
                    )
                }
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
fun MainScreen(
    photosBackgroundViewModel: PhotosBackgroundViewModel,
    clockViewModel: ClockViewModel,
    weatherViewModel: WeatherViewModel,
    countdownViewModel: CountdownViewModel
) {
    var showButton by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current

    val backgroundHsl by photosBackgroundViewModel.hsl.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .pointerInput(Unit) {
                detectTapGestures(
                    onPress = {
                        showButton = true
                    }
                )
            }
    ) {
        RenderBackground(viewModel = photosBackgroundViewModel)
        clockViewModel.RenderClock(backgroundHsl = backgroundHsl)
        weatherViewModel.RenderWeather(backgroundHsl = backgroundHsl)
        countdownViewModel.CountdownDisplay(backgroundHsl)

        // Fading Button
        AnimatedVisibility(
            visible = showButton,
            enter = fadeIn(animationSpec = tween(durationMillis = 300)),
            exit = fadeOut(animationSpec = tween(durationMillis = 300))
        ) {
            Button(
                onClick = {
                    throw RuntimeException("Test Crash") // Force a crash
                    context.startActivity(Intent(context, PreferencesActivity::class.java))
                },
                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(16.dp)
            ) {
                Text("Settings")
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
