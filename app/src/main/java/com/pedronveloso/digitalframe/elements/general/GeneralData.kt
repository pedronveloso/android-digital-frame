package com.pedronveloso.digitalframe.elements.general

import com.pedronveloso.digitalframe.persistence.PreferencesPersistence

interface GeneralData {
    fun explicitlyDisabledCrashCollection(): Boolean

    fun setExplicitlyDisabledCrashCollection(value: Boolean)

    fun lat(): String

    fun lon(): String

    fun setLat(value: String)

    fun setLon(value: String)
}

class RealGeneralData(private val persistence: PreferencesPersistence) : GeneralData {
    override fun explicitlyDisabledCrashCollection(): Boolean {
        return persistence.getPreferenceValue(
            SECTION_ID,
            EXPLICITLY_DISABLED_CRASH_COLLECTION,
            false,
        )
    }

    override fun setExplicitlyDisabledCrashCollection(value: Boolean) {
        persistence.setPreferenceValue(SECTION_ID, EXPLICITLY_DISABLED_CRASH_COLLECTION, value)
    }

    override fun lat(): String {
        return persistence.getPreferenceValue(SECTION_ID, LAT, "37.808332")
    }

    override fun lon(): String {
        return persistence.getPreferenceValue(SECTION_ID, LON, "-122.415715")
    }

    override fun setLat(value: String) {
        persistence.setPreferenceValue(SECTION_ID, LAT, value)
    }

    override fun setLon(value: String) {
        persistence.setPreferenceValue(SECTION_ID, LON, value)
    }

    companion object {
        private const val SECTION_ID = "general"
        private const val EXPLICITLY_DISABLED_CRASH_COLLECTION =
            "explicitly-disabled-crash-collection"
        private const val LAT = "lat"
        private const val LON = "lon"
    }
}

class FakeGeneralData : GeneralData {
    override fun explicitlyDisabledCrashCollection(): Boolean {
        return false
    }

    override fun setExplicitlyDisabledCrashCollection(value: Boolean) {
        // Do nothing.
    }

    override fun lat(): String {
        return "37.808332"
    }

    override fun lon(): String {
        return "-122.415715"
    }

    override fun setLat(value: String) {
        // Do nothing.
    }

    override fun setLon(value: String) {
        // Do nothing.
    }
}