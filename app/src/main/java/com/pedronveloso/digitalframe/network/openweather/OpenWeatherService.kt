package com.pedronveloso.digitalframe.network.openweather

import com.pedronveloso.digitalframe.network.NetworkResult

interface OpenWeatherService {

    suspend fun fetchCurrentWeatherConditions(): NetworkResult<OpenWeatherResponse>
}
