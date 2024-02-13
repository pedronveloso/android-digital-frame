package com.pedronveloso.digitalframe.fakes

import com.pedronveloso.digitalframe.persistence.PreferencesPersistence

class FakePreferencesPersistence : PreferencesPersistence {
    private val storage = mutableMapOf<String, Any>()

    private fun getKey(sectionId: String, propertyId: String): String = "$sectionId.$propertyId"

    override fun getPreferenceValue(sectionId: String, propertyId: String, defaultValue: Int): Int =
        storage[getKey(sectionId, propertyId)] as? Int ?: defaultValue

    override fun getPreferenceValue(
        sectionId: String,
        propertyId: String,
        defaultValue: Double
    ): Double =
        storage[getKey(sectionId, propertyId)] as? Double ?: defaultValue

    override fun getPreferenceValue(
        sectionId: String,
        propertyId: String,
        defaultValue: String
    ): String =
        storage[getKey(sectionId, propertyId)] as? String ?: defaultValue

    override fun getPreferenceValue(
        sectionId: String,
        propertyId: String,
        defaultValue: Boolean
    ): Boolean =
        storage[getKey(sectionId, propertyId)] as? Boolean ?: defaultValue

    override fun setPreferenceValue(sectionId: String, propertyId: String, value: Int) {
        storage[getKey(sectionId, propertyId)] = value
    }

    override fun setPreferenceValue(sectionId: String, propertyId: String, value: Double) {
        storage[getKey(sectionId, propertyId)] = value
    }

    override fun setPreferenceValue(sectionId: String, propertyId: String, value: String) {
        storage[getKey(sectionId, propertyId)] = value
    }

    override fun setPreferenceValue(sectionId: String, propertyId: String, value: Boolean) {
        storage[getKey(sectionId, propertyId)] = value
    }
}
