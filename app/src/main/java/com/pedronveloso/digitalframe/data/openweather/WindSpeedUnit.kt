package com.pedronveloso.digitalframe.data.openweather

import android.content.Context
import com.pedronveloso.digitalframe.R

enum class WindSpeedUnit {
    MetersPerSecond,
    MilesPerHour,
    KilometersPerHour,
    Knots;

    fun toDisplayString(context: Context): String {
        return when (this) {
            MetersPerSecond -> context.getString(R.string.pref_weather_wind_unit_meters_second)
            MilesPerHour -> context.getString(R.string.pref_weather_wind_unit_miles_hour)
            KilometersPerHour -> context.getString(R.string.pref_weather_wind_unit_kilometers_hour)
            Knots -> context.getString(R.string.pref_weather_wind_unit_knots)
        }
    }
}