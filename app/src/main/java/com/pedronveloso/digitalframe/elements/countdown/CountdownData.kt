package com.pedronveloso.digitalframe.elements.countdown

import com.pedronveloso.digitalframe.persistence.PluginData
import com.pedronveloso.digitalframe.persistence.PluginData.Companion.PROPERTY_ENABLED
import com.pedronveloso.digitalframe.persistence.PreferencesPersistence
import java.time.LocalDate


interface CountdownData : PluginData {
    fun setTargetDate(value: LocalDate)
    fun getTargetDate(): LocalDate
    fun setMessage(value: String)
    fun getMessage(): String
}

class RealCountdownData(private val persistence: PreferencesPersistence) : CountdownData {
    override fun setTargetDate(value: LocalDate) {
        persistence.setPreferenceValue(SECTION_ID, TARGET_DATE, value)
    }

    override fun getTargetDate(): LocalDate {
        return persistence.getPreferenceValue(SECTION_ID, TARGET_DATE, LocalDate.now())
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
        private const val TARGET_DATE = "target-date"
        private const val MESSAGE = "message"
    }
}

