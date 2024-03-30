package com.pedronveloso.digitalframe.elements.general

import com.pedronveloso.digitalframe.persistence.PreferencesPersistence
import com.pedronveloso.digitalframe.preferences.location.LocationData

interface GeneralData {
    fun explicitlyDisabledCrashCollection(): Boolean

    fun setExplicitlyDisabledCrashCollection(value: Boolean)

    fun locationData(): LocationData

    fun setLocationData(value: LocationData)

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
        private const val LAT = "lat_d"
        private const val LON = "lon_d"
    }
}

class FakeGeneralData : GeneralData {
    override fun explicitlyDisabledCrashCollection(): Boolean {
        return false
    }

    override fun setExplicitlyDisabledCrashCollection(value: Boolean) {
        // Do nothing.
    }

    override fun locationData(): LocationData {
        return LocationData(37.808332, -122.415715)
    }

    override fun setLocationData(value: LocationData) {
        // Do nothing.
    }
}