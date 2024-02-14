package com.pedronveloso.digitalframe.elements.weather

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pedronveloso.digitalframe.R
import com.pedronveloso.digitalframe.data.exceptions.NetworkException
import com.pedronveloso.digitalframe.data.openweather.OpenWeatherResponse
import com.pedronveloso.digitalframe.data.vo.UiResult
import com.pedronveloso.digitalframe.network.NetworkResult
import com.pedronveloso.digitalframe.network.openweather.FakeWeatherService
import com.pedronveloso.digitalframe.network.openweather.OpenWeatherService
import com.pedronveloso.digitalframe.ui.DigitalFrameTheme
import com.pedronveloso.digitalframe.ui.FadingComposable
import com.pedronveloso.digitalframe.ui.FontStyles
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.math.roundToInt
import kotlin.time.Duration.Companion.hours

@HiltViewModel
class WeatherViewModel
    @Inject
    constructor(
        private val apiService: OpenWeatherService,
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
                weatherState =
                    when (val result = apiService.fetchCurrentWeatherConditions()) {
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
        fun RenderWeather(
            weatherData: WeatherData,
            backgroundHsl: FloatArray,
        ) {
            FadingComposable {
                Column(
                    Modifier
                        .padding(32.dp)
                        .fillMaxWidth()
                        .fillMaxHeight(),
                    verticalArrangement = Arrangement.Bottom,
                    horizontalAlignment = Alignment.End,
                ) {
                    when (weatherState) {
                        is UiResult.Blank -> {
                            Text(
                                text = "No weather data",
                                style = FontStyles.textStyleTitleMedium(backgroundHsl),
                            )
                        }
                        // TODO: If failed to get new weather data, use latest known data.
                        is UiResult.Failure -> {
                            Text(
                                text = "Failed to get weather data",
                                style = FontStyles.textStyleTitleMedium(backgroundHsl),
                            )
                        }

                        is UiResult.Loading -> {
                            Text(
                                text = "loading...",
                                style = FontStyles.textStyleTitleMedium(backgroundHsl),
                            )
                        }

                        is UiResult.Success -> {
                            Row(
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                val weatherDay =
                                    (weatherState as UiResult.Success<OpenWeatherResponse>).data.weatherDays.first()

                                // Noon.
                                Spacer(modifier = Modifier.size(16.dp))
                                DrawWeatherElementWithIcon(
                                    weatherData,
                                    temperature = weatherDay.temperatures.day,
                                    windSpeed = weatherDay.speed,
                                    iconMain = weatherDay.weather.first().main,
                                    backgroundHsl,
                                )
                            }
                        }
                    }
                }
            }
        }

        @Composable
        fun DrawWeatherElementWithIcon(
            weatherData: WeatherData,
            temperature: Double,
            windSpeed: Double,
            iconMain: String,
            backgroundHsl: FloatArray,
        ) {
            Column(
                verticalArrangement = Arrangement.SpaceEvenly,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                // Temperature.
                val dayTemp: String
                if (weatherData.useCelsius()) {
                    val dayTempValue = temperature.roundToInt()
                    dayTemp = "$dayTempValue °C"
                } else {
                    val dayTempValue = (temperature * 9 / 5 + 32).roundToInt()
                    dayTemp = "$dayTempValue °F"
                }

                Text(
                    text = dayTemp,
                    style = FontStyles.textStyleDisplayMedium(backgroundHsl),
                )

                // Wind Speed.
                if (weatherData.showWind()) {
                    val windSpeedValue = windSpeed.roundToInt()
                    val windSpeedLabel = "💨 $windSpeedValue m/s"
                    Text(
                        text = windSpeedLabel,
                        style = FontStyles.textStyleBodyLarge(backgroundHsl),
                    )
                }

                // Icon.
                val iconId =
                    when (iconMain) {
                        "Clouds" -> R.drawable.cloudy_day
                        "Clear" -> R.drawable.sun
                        "Rain" -> R.drawable.cloud_rain
                        else -> R.drawable.ic_launcher_foreground
                    }
                Image(painter = painterResource(id = iconId), contentDescription = null)
            }
        }
    }

@Preview(showBackground = true)
@Composable
fun PreviewRenderWeather() {
    val backgroundHsl = floatArrayOf(210f, 0.9f, 0.5f)
    val weatherData = FakeWeatherData()

    DigitalFrameTheme {
        WeatherViewModel(FakeWeatherService()).RenderWeather(weatherData, backgroundHsl)
    }
}