package com.pedronveloso.digitalframe.utils

import java.time.LocalTime

fun isDayTime(): Boolean {
    val currentTime = LocalTime.now()
    val startDay = LocalTime.of(6, 0) // 6 AM
    val endDay = LocalTime.of(23, 0) // 11 PM

    return currentTime.isAfter(startDay) && currentTime.isBefore(endDay)
}
