package com.pedronveloso.digitalframe.persistence

interface PreferencesPersistence {
    fun getPreferenceValue(sectionId: String, propertyId: String, defaultValue: Int): Int
    fun getPreferenceValue(sectionId: String, propertyId: String, defaultValue: Double): Double
    fun getPreferenceValue(sectionId: String, propertyId: String, defaultValue: String): String
    fun getPreferenceValue(sectionId: String, propertyId: String, defaultValue: Boolean): Boolean

    // Set preference values using section and property IDs
    fun setPreferenceValue(sectionId: String, propertyId: String, value: Int)
    fun setPreferenceValue(sectionId: String, propertyId: String, value: Double)
    fun setPreferenceValue(sectionId: String, propertyId: String, value: String)
    fun setPreferenceValue(sectionId: String, propertyId: String, value: Boolean)
}