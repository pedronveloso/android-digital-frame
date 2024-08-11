package com.pedronveloso.digitalframe.elements.countdown

import com.pedronveloso.digitalframe.persistence.PluginDataPersistence
import com.pedronveloso.digitalframe.persistence.PluginDataPersistence.Companion.PROPERTY_ENABLED
import com.pedronveloso.digitalframe.persistence.PreferencesPersistence
import java.time.LocalDate

interface CountdownPersistence : PluginDataPersistence {
    fun setTargetDate(value: LocalDate)

    fun getTargetDate(): LocalDate

    fun setMessage(value: String)

    fun getMessage(): String
}

class RealCountdownPersistence(
    private val persistence: PreferencesPersistence,
) : CountdownPersistence {
    override fun setTargetDate(value: LocalDate) {
        persistence.setPreferenceValue(SECTION_ID, TARGET_DATE, value)
    }

    override fun getTargetDate(): LocalDate = persistence.getPreferenceValue(SECTION_ID, TARGET_DATE, LocalDate.now().minusDays(1))

    override fun setMessage(value: String) {
        persistence.setPreferenceValue(SECTION_ID, MESSAGE, value)
    }

    override fun getMessage(): String = persistence.getPreferenceValue(SECTION_ID, MESSAGE, "")

    override fun isEnabled(): Boolean = persistence.getPreferenceValue(SECTION_ID, PROPERTY_ENABLED, true)

    override fun setEnabled(value: Boolean) {
        persistence.setPreferenceValue(SECTION_ID, PROPERTY_ENABLED, value)
    }

    companion object {
        private const val SECTION_ID = "countdown"
        private const val TARGET_DATE = "target-date"
        private const val MESSAGE = "message"
    }
}
