package com.pedronveloso.digitalframe.persistence

interface PreferencesPersistence {
    suspend fun getPreferenceValue(key: String, defaultValue: Int): Int
    suspend fun getPreferenceValue(key: String, defaultValue: Double): Double
    suspend fun getPreferenceValue(key: String, defaultValue: String): String

    suspend fun setPreferenceValue(key: String, value: Int)
    suspend fun setPreferenceValue(key: String, value: Double)
    suspend fun setPreferenceValue(key: String, value: String)
}