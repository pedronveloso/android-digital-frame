package com.pedronveloso.digitalframe.network.openweather

import com.pedronveloso.digitalframe.data.openweather.OpenWeatherResponse
import com.pedronveloso.digitalframe.network.NetworkResult
import javax.inject.Inject

class OpenWeatherServiceImpl
    @Inject
    constructor(
        private val retrofit: OpenWeatherServiceApi,
    ) : OpenWeatherService {
        override suspend fun fetchCurrentWeatherConditions(
            lat: String,
            lon: String,
        ): NetworkResult<OpenWeatherResponse> =
            try {
                val weatherResponse = retrofit.fetchCurrentWeatherConditions(lat, lon)
                // TODO: Check if response code is actually 200.
                NetworkResult.success(weatherResponse)
            } catch (e: Exception) {
                NetworkResult.failure(e)
            }
    }
