package com.pedronveloso.digitalframe.network.openweather

import com.google.gson.annotations.SerializedName
import java.time.LocalDateTime

data class WeatherDay(
    val clouds: Int,
    val deg: Int,

    @SerializedName("dt")
    val dateTime: LocalDateTime,
    val feels_like: FeelsLike,
    val gust: Double,
    val humidity: Int,
    val pressure: Int,
    val speed: Double,
    val sunrise: LocalDateTime,
    val sunset: LocalDateTime,

    @SerializedName("temp")
    val temperatures: Temperatures,

    val weather: List<Weather>
)
