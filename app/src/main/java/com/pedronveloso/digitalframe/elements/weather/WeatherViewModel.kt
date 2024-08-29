package com.pedronveloso.digitalframe.elements.weather

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pedronveloso.digitalframe.data.openweather.OpenWeatherResponse
import com.pedronveloso.digitalframe.data.vo.UiResult
import com.pedronveloso.digitalframe.elements.general.GeneralDataPersistence
import com.pedronveloso.digitalframe.network.NetworkResult
import com.pedronveloso.digitalframe.network.openweather.OpenWeatherService
import com.pedronveloso.digitalframe.utils.log.LogStoreProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.time.Duration.Companion.hours

@HiltViewModel
class WeatherViewModel
    @Inject
    constructor(
        private val apiService: OpenWeatherService,
    ) : ViewModel() {
        private var previousWeatherState: UiResult<OpenWeatherResponse> = UiResult.Blank()
        private val _weatherState = MutableStateFlow<UiResult<OpenWeatherResponse>>(UiResult.Blank())
        val weatherState: StateFlow<UiResult<OpenWeatherResponse>> = _weatherState.asStateFlow()

        private var executionJob: Job? = null
        private var startedRepeatedExecution = false
        private val logger = LogStoreProvider.getLogStore()

        fun startRepeatedExecution(
            weatherPersistence: WeatherPersistence,
            generalDataPersistence: GeneralDataPersistence,
        ) {
            if (!startedRepeatedExecution) {
                startedRepeatedExecution = true
                repeatedExecution(weatherPersistence, generalDataPersistence)
            }
        }

        private fun repeatedExecution(
            weatherPersistence: WeatherPersistence,
            generalDataPersistence: GeneralDataPersistence,
        ) {
            executionJob?.cancel()
            executionJob =
                viewModelScope.launch {
                    fetchWeatherConditions(
                        generalDataPersistence.locationData().latitude.toString(),
                        generalDataPersistence.locationData().longitude.toString(),
                    )
                    // How often to refresh the API. TODO: Make configurable.
                    delay(1.hours)
                    repeatedExecution(weatherPersistence, generalDataPersistence)
                }
        }

        private fun fetchWeatherConditions(
            latitude: String,
            longitude: String,
        ) {
            previousWeatherState = _weatherState.value
            _weatherState.value = UiResult.Loading()

            logger.log("Fetching weather for: $latitude, $longitude")

            viewModelScope.launch {
                _weatherState.value =
                    when (
                        val result =
                            apiService.fetchCurrentWeatherConditions(latitude, longitude)
                    ) {
                        is NetworkResult.Failure -> {
                            logger.error("Failed to fetch weather data", result.exception)

                            // If failed to get new weather data, use latest known data.
                            previousWeatherState
                        }

                        is NetworkResult.Success -> {
                            logger.log("Fetched weather data: ${result.data.printForLogs()}")
                            UiResult.success(result.data)
                        }
                    }
            }
        }
    }
