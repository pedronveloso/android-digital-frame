package com.pedronveloso.digitalframe.elements

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pedronveloso.digitalframe.data.exceptions.NetworkException
import com.pedronveloso.digitalframe.data.vo.UiResult
import com.pedronveloso.digitalframe.network.NetworkResult
import com.pedronveloso.digitalframe.network.openweather.OpenWeatherResponse
import com.pedronveloso.digitalframe.network.openweather.OpenWeatherService
import com.pedronveloso.digitalframe.ui.MyTypography
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.math.roundToInt
import kotlin.time.Duration.Companion.hours

@HiltViewModel
class WeatherViewModel @Inject constructor(
    private val apiService: OpenWeatherService
) : ViewModel() {

    var weatherState by mutableStateOf<UiResult<OpenWeatherResponse>>(UiResult.Blank())

    init {
        repeatedExecution()
    }

    private fun repeatedExecution() {
        viewModelScope.launch {
            fetchWeatherConditions()
            // How often to refresh the API. TODO: Make configurable.
            delay(6.hours)
            repeatedExecution()
        }
    }

    private fun fetchWeatherConditions() {
        weatherState = UiResult.Loading()

        viewModelScope.launch {
            when (val result = apiService.fetchCurrentWeatherConditions()) {
                is NetworkResult.Failure -> {
                    weatherState =
                        UiResult.failure(NetworkException())
                }
                is NetworkResult.Success -> {
                    weatherState = UiResult.success(result.data)
                }
            }
        }
    }

    @Composable
    fun RenderWeather() {
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
                    Text(text = "No weather data", style = MyTypography.displayLarge.copy(color = Color.White, shadow = Shadow(color = Color.Black, offset = Offset(0f, 2f), blurRadius = 1f)))
                }
                // TODO: If failed to get new weather data, use latest known data.
                is UiResult.Failure -> {
                    Text(text = "Failed to get weather data", style = MyTypography.displayLarge.copy(color = Color.White, shadow = Shadow(color = Color.Black, offset = Offset(0f, 2f), blurRadius = 1f)))
                }
                is UiResult.Loading -> {
                    Text(text = "loading...", style = MyTypography.displayLarge.copy(color = Color.White, shadow = Shadow(color = Color.Black, offset = Offset(0f, 2f), blurRadius = 1f)))
                }
                is UiResult.Success -> {
                    val dayTempValue = (weatherState as UiResult.Success<OpenWeatherResponse>).data.weatherDays.first().temperatures.day.roundToInt()
                    val dayTemp = "$dayTempValue °C"
                    Text(text = dayTemp, style = MyTypography.displayLarge.copy(color = Color.White, shadow = Shadow(color = Color.Black, offset = Offset(0f, 2f), blurRadius = 1f)))
                }
            }
        }
    }
}
