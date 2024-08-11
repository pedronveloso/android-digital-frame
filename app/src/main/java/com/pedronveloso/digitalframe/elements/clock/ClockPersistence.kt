package com.pedronveloso.digitalframe.elements.clock

import com.pedronveloso.digitalframe.persistence.PluginDataPersistence
import com.pedronveloso.digitalframe.persistence.PluginDataPersistence.Companion.PROPERTY_ENABLED
import com.pedronveloso.digitalframe.persistence.PreferencesPersistence

interface ClockPersistence : PluginDataPersistence {
    fun use24HClock(): Boolean

    fun setUse24HClock(value: Boolean)

    fun showYear(): Boolean

    fun setShowYear(value: Boolean)

    fun showSeconds(): Boolean

    fun setShowSeconds(value: Boolean)
}

class RealClockPersistence(
    private val persistence: PreferencesPersistence,
) : ClockPersistence {
    override fun use24HClock(): Boolean =
        persistence.getPreferenceValue(
            SECTION_ID,
            PROPERTY_24H_CLOCK,
            false,
        )

    override fun setUse24HClock(value: Boolean) {
        persistence.setPreferenceValue(SECTION_ID, PROPERTY_24H_CLOCK, value)
    }

    override fun showYear(): Boolean =
        persistence.getPreferenceValue(
            SECTION_ID,
            PROPERTY_SHOW_YEAR,
            true,
        )

    override fun setShowYear(value: Boolean) {
        persistence.setPreferenceValue(SECTION_ID, PROPERTY_SHOW_YEAR, value)
    }

    override fun showSeconds(): Boolean =
        persistence.getPreferenceValue(
            SECTION_ID,
            PROPERTY_SHOW_SECONDS,
            false,
        )

    override fun setShowSeconds(value: Boolean) {
        persistence.setPreferenceValue(SECTION_ID, PROPERTY_SHOW_SECONDS, value)
    }

    override fun isEnabled(): Boolean = persistence.getPreferenceValue(SECTION_ID, PROPERTY_ENABLED, true)

    override fun setEnabled(value: Boolean) {
        persistence.setPreferenceValue(SECTION_ID, PROPERTY_ENABLED, value)
    }

    companion object {
        private const val SECTION_ID = "clock"
        private const val PROPERTY_24H_CLOCK = "24h-clock"
        private const val PROPERTY_SHOW_YEAR = "show-year"
        private const val PROPERTY_SHOW_SECONDS = "show-seconds"
    }
}
