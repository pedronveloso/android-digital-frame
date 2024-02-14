package com.pedronveloso.digitalframe.network.openweather

import com.pedronveloso.digitalframe.data.openweather.OpenWeatherResponse
import com.pedronveloso.digitalframe.network.NetworkResult

interface OpenWeatherService {
    suspend fun fetchCurrentWeatherConditions(
        lat: String,
        lon: String
    ): NetworkResult<OpenWeatherResponse>
}
