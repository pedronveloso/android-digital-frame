package com.pedronveloso.digitalframe.network.openweather

import retrofit2.http.GET
import retrofit2.http.Query

interface OpenWeatherServiceApi {

    @GET("forecast/daily")
    suspend fun fetchCurrentWeatherConditions(
        @Query(value = "zip") zip: String = "94102,US",
        @Query(value = "appid") apiKey: String = API_KEY,
        @Query(value = "units") units: String = "metric"
    ): OpenWeatherResponse

    companion object {
        private const val API_KEY = "f68b13ab43f8d29d4ba7979c2c09695d"
    }
}
