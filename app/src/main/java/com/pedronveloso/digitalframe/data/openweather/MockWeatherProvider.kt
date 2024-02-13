package com.pedronveloso.digitalframe.data.openweather

import java.time.LocalDateTime

object MockWeatherProvider {
    val mockWeatherResponse =
        OpenWeatherResponse(
            city = City(name = "Sample City", country = "SC"),
            networkCode = "200",
            weatherDays =
                listOf(
                    WeatherDay(
                        clouds = 0,
                        deg = 100,
                        dateTime = LocalDateTime.now(),
                        feels_like = FeelsLike(day = 25.0, eve = 20.0, morn = 15.0, night = 18.0),
                        gust = 5.5,
                        humidity = 10,
                        pressure = 1013,
                        speed = 4.0,
                        sunrise = LocalDateTime.now(),
                        sunset = LocalDateTime.now(),
                        temperatures =
                            Temperatures(
                                day = 23.0,
                                eve = 21.0,
                                max = 24.0,
                                min = 18.0,
                                morn = 19.0,
                                night = 20.0,
                            ),
                        weather =
                            listOf(
                                Weather(description = "Clear sky", icon = "01d", id = 800, main = "Clear"),
                            ),
                    ),
                ),
        )
}
