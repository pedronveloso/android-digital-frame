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
import com.pedronveloso.digitalframe.data.openweather.WeatherType
import com.pedronveloso.digitalframe.data.vo.UiResult
import com.pedronveloso.digitalframe.elements.general.FakeGeneralData
import com.pedronveloso.digitalframe.elements.general.GeneralData
import com.pedronveloso.digitalframe.network.NetworkResult
import com.pedronveloso.digitalframe.network.openweather.FakeWeatherService
import com.pedronveloso.digitalframe.network.openweather.OpenWeatherService
import com.pedronveloso.digitalframe.ui.DigitalFrameTheme
import com.pedronveloso.digitalframe.ui.FadingComposable
import com.pedronveloso.digitalframe.ui.FontStyles
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
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

    private var executionJob: Job? = null
    private var startedRepeatedExecution = false

    private fun repeatedExecution(weatherData: WeatherData, generalData: GeneralData) {
        executionJob?.cancel()
        executionJob = viewModelScope.launch {
            fetchWeatherConditions(generalData.lat(), generalData.lon())
                // How often to refresh the API. TODO: Make configurable.
            delay(1.hours)
            repeatedExecution(weatherData, generalData)
            }
        }

    private fun fetchWeatherConditions(latitude: String, longitude: String) {
            weatherState = UiResult.Loading()

            viewModelScope.launch {
                weatherState =
                    when (val result =
                        apiService.fetchCurrentWeatherConditions(latitude, longitude)) {
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
            generalData: GeneralData,
            backgroundHsl: FloatArray,
        ) {
            if (!startedRepeatedExecution) {
                startedRepeatedExecution = true
                repeatedExecution(weatherData, generalData)
            }

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
                                val weatherResponse =
                                    (weatherState as UiResult.Success<OpenWeatherResponse>).data

                                // Noon.
                                Spacer(modifier = Modifier.size(16.dp))
                                DrawWeatherElementWithIcon(
                                    weatherData,
                                    temperature = weatherResponse.main.temp,
                                    windSpeed = weatherResponse.wind.speed,
                                    weatherType = weatherResponse.weather.first().weatherType,
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
            weatherType: WeatherType,
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

                // TODO: Consider day and night.
                val iconId =
                    when (weatherType) {
                        WeatherType.Clear -> R.drawable.day_clear
                        WeatherType.LightClouds -> R.drawable.day_partial_cloud
                        WeatherType.HeavyClouds -> R.drawable.cloudy
                        WeatherType.Rain -> R.drawable.rain
                        WeatherType.Snow -> R.drawable.snow
                        WeatherType.Thunderstorm -> R.drawable.thunder
                        WeatherType.Drizzle -> R.drawable.day_rain
                        WeatherType.Atmosphere -> R.drawable.mist
                        WeatherType.Fog -> R.drawable.fog
                        WeatherType.Tornado -> R.drawable.tornado
                        else -> R.drawable.day_clear
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
    val generalData = FakeGeneralData()

    DigitalFrameTheme {
        WeatherViewModel(FakeWeatherService()).RenderWeather(
            weatherData,
            generalData,
            backgroundHsl
        )
    }
}
