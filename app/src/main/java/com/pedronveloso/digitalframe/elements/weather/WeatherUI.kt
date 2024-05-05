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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.pedronveloso.digitalframe.R
import com.pedronveloso.digitalframe.data.openweather.OpenWeatherResponse
import com.pedronveloso.digitalframe.data.openweather.WeatherType
import com.pedronveloso.digitalframe.data.openweather.Wind
import com.pedronveloso.digitalframe.data.openweather.WindSpeedUnit
import com.pedronveloso.digitalframe.data.vo.UiResult
import com.pedronveloso.digitalframe.elements.general.FakeGeneralDataPersistence
import com.pedronveloso.digitalframe.elements.general.GeneralDataPersistence
import com.pedronveloso.digitalframe.network.openweather.FakeWeatherService
import com.pedronveloso.digitalframe.ui.DigitalFrameTheme
import com.pedronveloso.digitalframe.ui.FadingComposable
import com.pedronveloso.digitalframe.ui.FontStyles
import com.pedronveloso.digitalframe.ui.deriveHUDColor
import kotlin.math.roundToInt

@Composable
fun RenderWeather(
    viewModel: WeatherViewModel,
    weatherPersistence: WeatherPersistence,
    generalDataPersistence: GeneralDataPersistence,
    hudColor: Color,
) {
    viewModel.startRepeatedExecution(weatherPersistence, generalDataPersistence)
    val weatherState by viewModel.weatherState.collectAsState()

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
                            windSpeed = weatherResponse.wind,
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
    windSpeed: Wind,
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

                // Apply the correct unit to the wind speed.
                val windUnit = weatherPersistence.windSpeedUnit()
                val windSpeedLabel = when (windUnit) {
                    WindSpeedUnit.MetersPerSecond -> {
                        stringResource(id = R.string.wind_meters_per_second, windSpeed.speed)
                    }

                    WindSpeedUnit.KilometersPerHour -> {
                        stringResource(
                            id = R.string.wind_kilometers_per_hour,
                            windSpeed.getWindSpeedInKmHour()
                        )
                    }

                    WindSpeedUnit.MilesPerHour -> {
                        stringResource(
                            id = R.string.wind_miles_per_hour,
                            windSpeed.getWindSpeedInMilesHour()
                        )
                    }

                    WindSpeedUnit.Knots -> {
                        stringResource(id = R.string.wind_knots, windSpeed.getWindSpeedInKnots())
                    }
                }

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

@Preview()
@Composable
fun PreviewRenderWeather() {
    val backgroundHsl = floatArrayOf(210f, 0.9f, 0.5f)
    val hudColor = deriveHUDColor(backgroundHsl)
    val weatherData = FakeWeatherPersistence()
    val generalData = FakeGeneralDataPersistence()

    DigitalFrameTheme {
        RenderWeather(
            WeatherViewModel(FakeWeatherService()),
            weatherData,
            generalData,
            hudColor
        )
    }
}