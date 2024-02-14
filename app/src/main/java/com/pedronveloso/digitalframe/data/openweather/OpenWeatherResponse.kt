package com.pedronveloso.digitalframe.data.openweather


data class OpenWeatherResponse(
    val weather: List<Weather>,
    val main: TemperatureDetails,
    val wind: Wind
)

data class Weather(
    val id: Int,
    val main: String,
    val description: String,
    val icon: String
)

data class TemperatureDetails(
    val temp: Double,
    val temp_min: Double,
    val temp_max: Double
)

data class Wind(
    val speed: Double
)
