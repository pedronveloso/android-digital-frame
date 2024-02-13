package com.pedronveloso.digitalframe.elements.countdown

import com.pedronveloso.digitalframe.persistence.PluginData
import com.pedronveloso.digitalframe.persistence.PluginData.Companion.PROPERTY_ENABLED
import com.pedronveloso.digitalframe.persistence.PreferencesPersistence


interface CountdownData : PluginData {
    fun setDaysRemaining(value: Int)
    fun getDaysRemaining(): Int
    fun setMessage(value: String)
    fun getMessage(): String
}

class RealCountdownData(private val persistence: PreferencesPersistence) : CountdownData {
    override fun setDaysRemaining(value: Int) {
        persistence.setPreferenceValue(SECTION_ID, DAYS_REMAINING, value)
    }

    override fun getDaysRemaining(): Int {
        return persistence.getPreferenceValue(SECTION_ID, DAYS_REMAINING, 0)
    }

    override fun setMessage(value: String) {
        persistence.setPreferenceValue(SECTION_ID, MESSAGE, value)
    }

    override fun getMessage(): String {
        return persistence.getPreferenceValue(SECTION_ID, MESSAGE, "")
    }

    override fun isEnabled(): Boolean {
        return persistence.getPreferenceValue(SECTION_ID, PROPERTY_ENABLED, true)
    }

    override fun setEnabled(value: Boolean) {
        persistence.setPreferenceValue(SECTION_ID, PROPERTY_ENABLED, value)
    }

    companion object {
        private const val SECTION_ID = "countdown"
        private const val DAYS_REMAINING = "days-remaining"
        private const val MESSAGE = "message"
    }
}

