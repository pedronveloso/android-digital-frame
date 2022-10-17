package com.pedronveloso.digitalframe.network.openweather

data class Temperatures(
    val day: Double,
    val eve: Double,
    val max: Double,
    val min: Double,
    val morn: Double,
    val night: Double
)
