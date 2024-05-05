package com.pedronveloso.digitalframe.data.openweather


data class OpenWeatherResponse(
    val weather: List<Weather>,
    val main: TemperatureDetails,
    val wind: Wind
){
    fun printForLogs() : String {
        return "Weather: ${weather[0].main}, id: ${weather[0].id},  Temp: ${main.temp}, Wind: ${wind.speed}"
    }
}

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
                600 -> WeatherType.LightSnow
                in 615..620 -> WeatherType.LightSnow
                in 601..614 -> WeatherType.Snow
                in 621..622 -> WeatherType.Snow
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
    LightSnow,
    Snow,
    HeavySnow,
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

/**
 * By default, wind speed from OpenWeather is in meters per second.
 */
data class Wind(
    val speed: Double
) {
    fun getWindSpeedInKmHour(): Double {
        return speed / 1000f
    }

    fun getWindSpeedInMilesHour(): Double {
        return speed / 1609.344
    }

    fun getWindSpeedInKnots(): Double {
        return speed / 0.514444
    }
}
