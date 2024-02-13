package com.pedronveloso.digitalframe.persistence

import java.time.LocalDate

interface PreferencesPersistence {
    fun getPreferenceValue(
        sectionId: String,
        propertyId: String,
        defaultValue: Int,
    ): Int

    fun getPreferenceValue(
        sectionId: String,
        propertyId: String,
        defaultValue: Double,
    ): Double

    fun getPreferenceValue(
        sectionId: String,
        propertyId: String,
        defaultValue: String,
    ): String

    fun getPreferenceValue(
        sectionId: String,
        propertyId: String,
        defaultValue: Boolean,
    ): Boolean

    fun setPreferenceValue(
        sectionId: String,
        propertyId: String,
        value: Int,
    )

    fun setPreferenceValue(
        sectionId: String,
        propertyId: String,
        value: Double,
    )

    fun setPreferenceValue(
        sectionId: String,
        propertyId: String,
        value: String,
    )

    fun setPreferenceValue(
        sectionId: String,
        propertyId: String,
        value: Boolean,
    )

    fun getPreferenceValue(
        sectionId: String,
        propertyId: String,
        defaultValue: LocalDate,
    ): LocalDate

    fun setPreferenceValue(
        sectionId: String,
        propertyId: String,
        value: LocalDate,
    )
}
