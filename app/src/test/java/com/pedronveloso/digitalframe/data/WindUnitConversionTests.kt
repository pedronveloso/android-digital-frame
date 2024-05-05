package com.pedronveloso.digitalframe.data

import com.pedronveloso.digitalframe.data.openweather.Wind
import junit.framework.TestCase.assertEquals
import org.junit.Test

class WindUnitConversionTests {

    @Test
    fun `test conversion from meters per second to kilometers per hour`() {
        val expectedKmh = 36.0
        val windSpeed = Wind(10.0)
        val actualKmh = windSpeed.getWindSpeedInKmHour()
        assertEquals(expectedKmh, actualKmh, 0.1)
    }

    @Test
    fun `test conversion from meters per second to miles per hour`() {
        val expectedMph = 22.37
        val windSpeed = Wind(10.0)
        val actualMph = windSpeed.getWindSpeedInMilesHour()
        assertEquals(expectedMph, actualMph, 0.001)
    }

    @Test
    fun `test conversion from meters per second to knots`() {
        val expectedKnots = 19.4384449
        val windSpeed = Wind(10.0)
        val actualKnots = windSpeed.getWindSpeedInKnots()
        assertEquals(expectedKnots, actualKnots, 0.1)
    }
}