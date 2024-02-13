package com.pedronveloso.digitalframe.elements.clock

import com.google.common.truth.Truth.assertThat
import com.pedronveloso.digitalframe.fakes.FakePreferencesPersistence
import org.junit.Before
import org.junit.Test


class ClockDataTest {

    private lateinit var fakePersistence: FakePreferencesPersistence

    @Before
    fun setUp() {
        fakePersistence = FakePreferencesPersistence()
    }

    @Test
    fun `use24HClock returns false as default value when not set`() {
        val result = ClockData.use24HClock(fakePersistence)
        assertThat(result).isFalse()
    }

    @Test
    fun `use24HClock returns true after being set`() {
        ClockData.setUse24HClock(fakePersistence, true)
        val result = ClockData.use24HClock(fakePersistence)
        assertThat(result).isTrue()
    }
}