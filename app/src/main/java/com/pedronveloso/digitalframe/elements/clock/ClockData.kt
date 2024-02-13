package com.pedronveloso.digitalframe.elements.clock

import com.pedronveloso.digitalframe.persistence.PluginData
import com.pedronveloso.digitalframe.persistence.PluginData.Companion.PROPERTY_ENABLED
import com.pedronveloso.digitalframe.persistence.PreferencesPersistence


interface ClockData : PluginData {

    fun use24HClock(): Boolean
    fun setUse24HClock(value: Boolean)

}


class RealClockData(private val persistence: PreferencesPersistence) : ClockData {

    override fun use24HClock(): Boolean {
        return persistence.getPreferenceValue(
            SECTION_ID,
            PROPERTY_24H_CLOCK, false
        )
    }

    override fun setUse24HClock(value: Boolean) {
        persistence.setPreferenceValue(SECTION_ID, PROPERTY_24H_CLOCK, value)
    }

    override fun isEnabled(): Boolean {
        return persistence.getPreferenceValue(SECTION_ID, PROPERTY_ENABLED, true)
    }

    override fun setEnabled(value: Boolean) {
        persistence.setPreferenceValue(SECTION_ID, PROPERTY_ENABLED, value)
    }

    companion object {
        private const val SECTION_ID = "clock"
        private const val PROPERTY_24H_CLOCK = "24h-clock"
    }

}
