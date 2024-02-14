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
) {
    val weatherType: WeatherType
        get() {
            // Source: https://openweathermap.org/weather-conditions
            return when (id) {
                in 200..299 -> WeatherType.Thunderstorm
                in 300..399 -> WeatherType.Drizzle
                in 500..599 -> WeatherType.Rain
                in 600..699 -> WeatherType.Snow
                in 700..799 -> WeatherType.Atmosphere
                800 -> WeatherType.Clear
                801 -> WeatherType.LightClouds
                in 802..804 -> WeatherType.HeavyClouds
                else -> WeatherType.Clear
            }
        }
}

enum class WeatherType {
    Clear,
    LightClouds,
    HeavyClouds,
    Rain,
    Snow,
    Thunderstorm,
    Drizzle,
    Atmosphere,
    Mist,
    Smoke,
    Haze,
    Dust,
    Fog,
    Sand,
    Ash,
    Squall,
    Tornado
}

data class TemperatureDetails(
    val temp: Double,
    val temp_min: Double,
    val temp_max: Double
)

data class Wind(
    val speed: Double
)
