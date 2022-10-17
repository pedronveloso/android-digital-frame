package com.pedronveloso.digitalframe

import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.pedronveloso.digitalframe.elements.ClockViewModel
import com.pedronveloso.digitalframe.elements.PhotosBackgroundViewModel
import com.pedronveloso.digitalframe.elements.WeatherViewModel
import com.pedronveloso.digitalframe.ui.DigitalFrameTheme
import dagger.hilt.android.AndroidEntryPoint

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
                        weatherViewModel = viewModel()
                    )
                }
            }
        }
    }

    private fun hideSystemUI(view: View) {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        WindowInsetsControllerCompat(window, view).let { controller ->
            controller.hide(WindowInsetsCompat.Type.systemBars())
            controller.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }
    }
}

@Composable
fun MainScreen(
    photosBackgroundViewModel: PhotosBackgroundViewModel,
    clockViewModel: ClockViewModel,
    weatherViewModel: WeatherViewModel
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
    ) {
        photosBackgroundViewModel.RenderBackground()
        clockViewModel.RenderClock()
        weatherViewModel.RenderWeather()
    }
}
