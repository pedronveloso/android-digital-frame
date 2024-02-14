package com.pedronveloso.digitalframe.network.openweather

import com.pedronveloso.digitalframe.data.openweather.OpenWeatherResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface OpenWeatherServiceApi {
    @GET("weather")
    suspend fun fetchCurrentWeatherConditions(
        @Query(value = "lat") latitude: String = "37.808332",
        @Query(value = "lon") longitude: String = "-122.415715",
        @Query(value = "appid") apiKey: String = API_KEY,
        @Query(value = "units") units: String = "metric",
    ): OpenWeatherResponse

    companion object {
        private const val API_KEY = "f68b13ab43f8d29d4ba7979c2c09695d"
    }
}
