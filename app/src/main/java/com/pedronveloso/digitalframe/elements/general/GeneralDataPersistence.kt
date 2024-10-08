package com.pedronveloso.digitalframe.elements.general

import com.pedronveloso.digitalframe.persistence.PreferencesPersistence
import com.pedronveloso.digitalframe.preferences.location.LocationData

interface GeneralDataPersistence {
    fun explicitlyDisabledCrashCollection(): Boolean

    fun setExplicitlyDisabledCrashCollection(value: Boolean)

    fun userPromptedForCrashCollection(): Boolean

    fun setUserPromptedForCrashCollection(value: Boolean)

    fun locationData(): LocationData

    fun setLocationData(value: LocationData)
}

class RealGeneralDataPersistence(
    private val persistence: PreferencesPersistence,
) : GeneralDataPersistence {
    override fun explicitlyDisabledCrashCollection(): Boolean =
        persistence.getPreferenceValue(
            SECTION_ID,
            EXPLICITLY_DISABLED_CRASH_COLLECTION,
            false,
        )

    override fun setExplicitlyDisabledCrashCollection(value: Boolean) {
        persistence.setPreferenceValue(SECTION_ID, EXPLICITLY_DISABLED_CRASH_COLLECTION, value)
    }

    override fun userPromptedForCrashCollection(): Boolean =
        persistence.getPreferenceValue(
            SECTION_ID,
            USER_PROMPTED_FOR_CRASH_COLLECTION,
            false,
        )

    override fun setUserPromptedForCrashCollection(value: Boolean) {
        persistence.setPreferenceValue(SECTION_ID, USER_PROMPTED_FOR_CRASH_COLLECTION, value)
    }

    override fun locationData(): LocationData {
        val lat = persistence.getPreferenceValue(SECTION_ID, LAT, 37.808332)
        val lon = persistence.getPreferenceValue(SECTION_ID, LON, -122.415715)
        return LocationData(lat, lon)
    }

    override fun setLocationData(value: LocationData) {
        persistence.setPreferenceValue(SECTION_ID, LAT, value.latitude)
        persistence.setPreferenceValue(SECTION_ID, LON, value.longitude)
    }

    companion object {
        private const val SECTION_ID = "general"

        private const val EXPLICITLY_DISABLED_CRASH_COLLECTION =
            "explicitly-disabled-crash-collection"

        private const val USER_PROMPTED_FOR_CRASH_COLLECTION =
            "user-prompted-for-crash-collection"

        private const val LAT = "lat_d"
        private const val LON = "lon_d"
    }
}

class FakeGeneralDataPersistence : GeneralDataPersistence {
    override fun explicitlyDisabledCrashCollection(): Boolean = false

    override fun setExplicitlyDisabledCrashCollection(value: Boolean) {
        // Do nothing.
    }

    override fun userPromptedForCrashCollection(): Boolean = true

    override fun setUserPromptedForCrashCollection(value: Boolean) {
        // Do nothing.
    }

    override fun locationData(): LocationData = LocationData(37.808332, -122.415715)

    override fun setLocationData(value: LocationData) {
        // Do nothing.
    }
}
