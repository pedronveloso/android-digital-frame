package com.pedronveloso.digitalframe.data.openweather


object MockWeatherProvider {
    val mockWeatherResponse =
        OpenWeatherResponse(
            listOf(
                Weather(
                    800,
                    "Clear",
                    "clear sky",
                    "01d"
                )
            ),
            TemperatureDetails(
                20.0,
                19.0,
                21.0
            ),
            Wind(
                2.0
            )
        )
}
