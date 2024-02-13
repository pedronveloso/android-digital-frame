package com.pedronveloso.digitalframe.data.openweather

import com.google.gson.annotations.SerializedName

data class OpenWeatherResponse(
    val city: City,
    @SerializedName("cod")
    val networkCode: String,
    @SerializedName(value = "list")
    val weatherDays: List<WeatherDay>,
)
