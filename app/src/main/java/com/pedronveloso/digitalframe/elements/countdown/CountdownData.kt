package com.pedronveloso.digitalframe.elements.countdown

import com.pedronveloso.digitalframe.persistence.PreferencesPersistence


object CountdownData {

    private const val SECTION_ID = "countdown"
    private const val DAYS_REMAINING = "days-remaining"
    private const val MESSAGE = "message"

    fun setDaysRemaining(persistence: PreferencesPersistence, value: Int) {
        persistence.setPreferenceValue(SECTION_ID, DAYS_REMAINING, value)
    }

    fun getDaysRemaining(persistence: PreferencesPersistence): Int {
        return persistence.getPreferenceValue(SECTION_ID, DAYS_REMAINING, 0)
    }

    fun setMessage(persistence: PreferencesPersistence, value: String) {
        persistence.setPreferenceValue(SECTION_ID, MESSAGE, value)
    }

    fun getMessage(persistence: PreferencesPersistence): String {
        return persistence.getPreferenceValue(SECTION_ID, MESSAGE, "")
    }
}

