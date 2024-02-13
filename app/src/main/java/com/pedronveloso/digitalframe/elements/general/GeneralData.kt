package com.pedronveloso.digitalframe.elements.general

import com.pedronveloso.digitalframe.persistence.PreferencesPersistence

interface GeneralData {
    fun explicitlyDisabledCrashCollection(): Boolean
    fun setExplicitlyDisabledCrashCollection(value: Boolean)
}

class RealGeneralData(private val persistence: PreferencesPersistence) : GeneralData {
    override fun explicitlyDisabledCrashCollection(): Boolean {
        return persistence.getPreferenceValue(
            SECTION_ID,
            EXPLICITLY_DISABLED_CRASH_COLLECTION,
            false
        )
    }

    override fun setExplicitlyDisabledCrashCollection(value: Boolean) {
        persistence.setPreferenceValue(SECTION_ID, EXPLICITLY_DISABLED_CRASH_COLLECTION, value)
    }

    companion object {
        private const val SECTION_ID = "general"
        private const val EXPLICITLY_DISABLED_CRASH_COLLECTION =
            "explicitly-disabled-crash-collection"
    }
}