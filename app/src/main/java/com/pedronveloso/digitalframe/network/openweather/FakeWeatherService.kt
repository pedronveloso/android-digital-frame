package com.pedronveloso.digitalframe.network.openweather

import com.pedronveloso.digitalframe.data.openweather.MockWeatherProvider
import com.pedronveloso.digitalframe.data.openweather.OpenWeatherResponse
import com.pedronveloso.digitalframe.network.NetworkResult

class FakeWeatherService : OpenWeatherService {
    override suspend fun fetchCurrentWeatherConditions(): NetworkResult<OpenWeatherResponse> {
        return NetworkResult.success(MockWeatherProvider.mockWeatherResponse)
    }
}