package com.pedronveloso.digitalframe.elements.clock

import com.google.common.truth.Truth.assertThat
import com.pedronveloso.digitalframe.fakes.FakePreferencesPersistence
import org.junit.Before
import org.junit.Test

class ClockPersistenceTests {
    private lateinit var fakePersistence: FakePreferencesPersistence
    private lateinit var clockPersistence: ClockPersistence

    @Before
    fun setUp() {
        fakePersistence = FakePreferencesPersistence()
        clockPersistence = RealClockPersistence(fakePersistence)
    }

    @Test
    fun `use24HClock returns false as default value when not set`() {
        val result = clockPersistence.use24HClock()
        assertThat(result).isFalse()
    }

    @Test
    fun `use24HClock returns true after being set`() {
        clockPersistence.setUse24HClock(true)
        val result = clockPersistence.use24HClock()
        assertThat(result).isTrue()
    }
}
