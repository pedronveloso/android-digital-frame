package com.pedronveloso.digitalframe.elements.clock

import com.google.common.truth.Truth.assertThat
import com.pedronveloso.digitalframe.fakes.FakePreferencesPersistence
import org.junit.Before
import org.junit.Test


class ClockDataTest {

    private lateinit var fakePersistence: FakePreferencesPersistence
    private lateinit var clockData: ClockData

    @Before
    fun setUp() {
        fakePersistence = FakePreferencesPersistence()
        clockData = RealClockData(fakePersistence)

    }

    @Test
    fun `use24HClock returns false as default value when not set`() {
        val result = clockData.use24HClock()
        assertThat(result).isFalse()
    }

    @Test
    fun `use24HClock returns true after being set`() {
        clockData.setUse24HClock(true)
        val result = clockData.use24HClock()
        assertThat(result).isTrue()
    }
}