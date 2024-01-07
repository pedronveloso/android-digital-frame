package com.pedronveloso.digitalframe.elements

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.with
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pedronveloso.digitalframe.R
import com.pedronveloso.digitalframe.data.GlobalValues
import com.pedronveloso.digitalframe.data.exceptions.NetworkException
import com.pedronveloso.digitalframe.data.vo.UiResult
import com.pedronveloso.digitalframe.network.NetworkResult
import com.pedronveloso.digitalframe.network.openweather.OpenWeatherResponse
import com.pedronveloso.digitalframe.network.openweather.OpenWeatherService
import com.pedronveloso.digitalframe.ui.FadingComposable
import com.pedronveloso.digitalframe.ui.MyTypography
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject
import kotlin.math.roundToInt
import kotlin.time.Duration.Companion.hours

@HiltViewModel
class WeatherViewModel @Inject constructor(
    private val apiService: OpenWeatherService
) : ViewModel() {

    private var weatherState by mutableStateOf<UiResult<OpenWeatherResponse>>(UiResult.Blank())


    init {
        repeatedExecution()
    }

    private fun repeatedExecution() {
        viewModelScope.launch {
            fetchWeatherConditions()
            // How often to refresh the API. TODO: Make configurable.
            delay(3.hours)
            repeatedExecution()
        }
    }

    private fun fetchWeatherConditions() {
        weatherState = UiResult.Loading()

        viewModelScope.launch {
            weatherState = when (val result = apiService.fetchCurrentWeatherConditions()) {
                is NetworkResult.Failure -> {
                    UiResult.failure(NetworkException())
                }

                is NetworkResult.Success -> {
                    UiResult.success(result.data)
                }
            }
        }
    }

    @Composable
    fun RenderWeather(use24HClock: Boolean = false) {
        FadingComposable {
            Column(
                Modifier
                    .padding(32.dp)
                    .fillMaxWidth()
                    .fillMaxHeight(),
                verticalArrangement = Arrangement.Bottom,
                horizontalAlignment = Alignment.End
            ) {
                when (weatherState) {
                    is UiResult.Blank -> {
                        Text(
                            text = "No weather data",
                            style = MyTypography.titleMedium.copy(
                                color = Color.White,
                                shadow = Shadow(
                                    color = Color.Black,
                                    offset = Offset(0f, 2f),
                                    blurRadius = 1f
                                )
                            )
                        )
                    }
                    // TODO: If failed to get new weather data, use latest known data.
                    is UiResult.Failure -> {
                        Text(
                            text = "Failed to get weather data",
                            style = MyTypography.titleMedium.copy(
                                color = Color.White,
                                shadow = Shadow(
                                    color = Color.Black,
                                    offset = Offset(0f, 2f),
                                    blurRadius = 1f
                                )
                            )
                        )
                    }

                    is UiResult.Loading -> {
                        Text(
                            text = "loading...",
                            style = MyTypography.titleMedium.copy(
                                color = Color.White,
                                shadow = Shadow(
                                    color = Color.Black,
                                    offset = Offset(0f, 2f),
                                    blurRadius = 1f
                                )
                            )
                        )
                    }

                    is UiResult.Success -> {
                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            val weatherDay =
                                (weatherState as UiResult.Success<OpenWeatherResponse>).data.weatherDays.first()


                            // Noon.
                            Spacer(modifier = Modifier.size(16.dp))
                            DrawWeatherElementWithIcon(
                                use24HClock = use24HClock,
                                temperature = weatherDay.temperatures.day,
                                iconMain = weatherDay.weather.first().main
                            )
                        }
                    }
                }
            }
        }
    }

    @Composable
    fun DrawWeatherElementWithIcon(use24HClock: Boolean, temperature: Double, iconMain: String) {
        Column(
            verticalArrangement = Arrangement.SpaceEvenly,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Temperature.
            val dayTempValue = temperature.roundToInt()
            val dayTemp = "$dayTempValue °C"
            Text(
                text = dayTemp,
                style = MyTypography.displayMedium.copy(
                    color = Color.White,
                    shadow = Shadow(color = Color.Black, offset = Offset(0f, 2f), blurRadius = 1f)
                )
            )

            // Icon.
            val iconId = when (iconMain) {
                "Clouds" -> R.drawable.cloudy_day
                "Clear" -> R.drawable.sun
                "Rain" -> R.drawable.cloud_rain
                else -> R.drawable.ic_launcher_foreground
            }
            Image(painter = painterResource(id = iconId), contentDescription = null)
        }
    }

    @Composable
    fun DrawWeatherElementEdges(
        use24HClock: Boolean,
        temperature: Double,
        timeLabel: LocalDateTime
    ) {
        Column(
            verticalArrangement = Arrangement.SpaceEvenly,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Temperature.
            val dayTempValue = temperature.roundToInt()
            val dayTemp = "$dayTempValue °C"
            Text(
                text = dayTemp,
                style = MyTypography.displayMedium.copy(
                    color = Color.White,
                    shadow = Shadow(color = Color.Black, offset = Offset(0f, 2f), blurRadius = 1f)
                )
            )

            // Time.
            val formatter: DateTimeFormatter =
                if (use24HClock) {
                    DateTimeFormatter.ofPattern("HH:mm")
                } else {
                    DateTimeFormatter.ofPattern("hh:mm a")
                }
            Text(
                text = formatter.format(timeLabel),
                style = MyTypography.titleMedium.copy(
                    color = Color.White,
                    shadow = Shadow(color = Color.Black, offset = Offset(0f, 2f), blurRadius = 1f)
                )
            )
        }
    }
}
