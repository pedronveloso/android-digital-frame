package com.pedronveloso.digitalframe.elements.clock

import com.pedronveloso.digitalframe.persistence.PreferencesPersistence


object ClockData {

    private const val SECTION_ID = "clock"
    private const val PROPERTY_24H_CLOCK = "24h-clock"

    fun use24HClock(persistence: PreferencesPersistence): Boolean {
        return persistence.getPreferenceValue(SECTION_ID, PROPERTY_24H_CLOCK, false)
    }

    fun setUse24HClock(persistence: PreferencesPersistence, value: Boolean) {
        persistence.setPreferenceValue(SECTION_ID, PROPERTY_24H_CLOCK, value)
    }

}

