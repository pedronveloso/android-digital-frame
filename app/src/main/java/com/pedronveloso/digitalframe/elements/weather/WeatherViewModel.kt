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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
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
import com.pedronveloso.digitalframe.elements.general.FakeGeneralDataPersistence
import com.pedronveloso.digitalframe.elements.general.GeneralDataPersistence
import com.pedronveloso.digitalframe.network.NetworkResult
import com.pedronveloso.digitalframe.network.openweather.FakeWeatherService
import com.pedronveloso.digitalframe.network.openweather.OpenWeatherService
import com.pedronveloso.digitalframe.ui.DigitalFrameTheme
import com.pedronveloso.digitalframe.ui.FadingComposable
import com.pedronveloso.digitalframe.ui.FontStyles
import com.pedronveloso.digitalframe.ui.deriveHUDColor
import com.pedronveloso.digitalframe.utils.log.LogStoreProvider
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
    private val logger = LogStoreProvider.getLogStore()

    private fun repeatedExecution(
        weatherPersistence: WeatherPersistence,
        generalDataPersistence: GeneralDataPersistence
    ) {
        executionJob?.cancel()
        executionJob = viewModelScope.launch {
            fetchWeatherConditions(
                generalDataPersistence.locationData().latitude.toString(),
                generalDataPersistence.locationData().longitude.toString()
            )
                // How often to refresh the API. TODO: Make configurable.
            delay(1.hours)
            repeatedExecution(weatherPersistence, generalDataPersistence)
            }
        }

    private fun fetchWeatherConditions(latitude: String, longitude: String) {
            weatherState = UiResult.Loading()

        logger.log("Fetching weather for: $latitude, $longitude")

            viewModelScope.launch {
                weatherState =
                    when (val result =
                        apiService.fetchCurrentWeatherConditions(latitude, longitude)) {
                        is NetworkResult.Failure -> {
                            logger.logError("Failed to fetch weather data", result.exception)
                            UiResult.failure(NetworkException())
                        }

                        is NetworkResult.Success -> {
                            logger.log("Fetched weather data: ${result.data.printForLogs()}")
                            UiResult.success(result.data)
                        }
                    }
            }
        }

        @Composable
        fun RenderWeather(
            weatherPersistence: WeatherPersistence,
            generalDataPersistence: GeneralDataPersistence,
            hudColor: Color,
        ) {
            if (!startedRepeatedExecution) {
                startedRepeatedExecution = true
                repeatedExecution(weatherPersistence, generalDataPersistence)
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
                                style = FontStyles.textStyleTitleMedium(hudColor),
                            )
                        }
                        // TODO: If failed to get new weather data, use latest known data.
                        is UiResult.Failure -> {
                            Text(
                                text = "Failed to get weather data",
                                style = FontStyles.textStyleTitleMedium(hudColor),
                            )
                        }

                        is UiResult.Loading -> {
                            Text(
                                text = "loading...",
                                style = FontStyles.textStyleTitleMedium(hudColor),
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
                                    weatherPersistence,
                                    temperature = weatherResponse.main.temp,
                                    windSpeed = weatherResponse.wind.speed,
                                    weatherType = weatherResponse.weather.first().weatherType,
                                    hudColor = hudColor,
                                )
                            }
                        }
                    }
                }
            }
        }

        @Composable
        fun DrawWeatherElementWithIcon(
            weatherPersistence: WeatherPersistence,
            temperature: Double,
            windSpeed: Double,
            weatherType: WeatherType,
            hudColor: Color,
        ) {
            Column(
                verticalArrangement = Arrangement.SpaceEvenly,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                // Temperature.
                val dayTemp: String
                if (weatherPersistence.useCelsius()) {
                    val dayTempValue = temperature.roundToInt()
                    dayTemp = "$dayTempValue °C"
                } else {
                    val dayTempValue = (temperature * 9 / 5 + 32).roundToInt()
                    dayTemp = "$dayTempValue °F"
                }

                Text(
                    text = dayTemp,
                    style = FontStyles.textStyleDisplayMedium(hudColor),
                )

                // Wind Speed.
                if (weatherPersistence.showWind()) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Image(
                            painter = painterResource(id = R.drawable.ic_wind),
                            contentDescription = null,
                            modifier = Modifier.size(24.dp),
                            colorFilter = ColorFilter.tint(hudColor),
                        )
                        Spacer(modifier = Modifier.size(4.dp))
                        val windSpeedValue = windSpeed.roundToInt()
                        val windSpeedLabel = "$windSpeedValue m/s"
                        Text(
                            text = windSpeedLabel,
                            style = FontStyles.textStyleBodyLarge(hudColor),
                        )
                    }
                }

                // TODO: Consider day and night.
                // TODO: Different icon for light snow.
                val iconId =
                    when (weatherType) {
                        WeatherType.Clear -> R.drawable.day_clear
                        WeatherType.LightClouds -> R.drawable.day_partial_cloud
                        WeatherType.HeavyClouds -> R.drawable.cloudy
                        WeatherType.Rain -> R.drawable.rain
                        WeatherType.LightSnow -> R.drawable.snow
                        WeatherType.Snow -> R.drawable.snow
                        WeatherType.Thunderstorm -> R.drawable.thunder
                        WeatherType.Drizzle -> R.drawable.day_rain
                        WeatherType.Atmosphere -> R.drawable.mist
                        WeatherType.Fog -> R.drawable.fog
                        WeatherType.Tornado -> R.drawable.tornado
                        else -> R.drawable.day_clear
                    }

                Image(
                    painter = painterResource(id = iconId),
                    contentDescription = null,
                    colorFilter = ColorFilter.tint(hudColor)
                )
            }
        }
    }

@Preview(showBackground = true)
@Composable
fun PreviewRenderWeather() {
    val backgroundHsl = floatArrayOf(210f, 0.9f, 0.5f)
    val hudColor = deriveHUDColor(backgroundHsl)
    val weatherData = FakeWeatherPersistence()
    val generalData = FakeGeneralDataPersistence()

    DigitalFrameTheme {
        WeatherViewModel(FakeWeatherService()).RenderWeather(
            weatherData,
            generalData,
            hudColor
        )
    }
}
