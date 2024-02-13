package com.pedronveloso.digitalframe.persistence

import android.content.Context
import android.content.SharedPreferences

class SharedPreferencesPersistence(
    private val context: Context,
    prefsName: String = "settings_preferences"
) : PreferencesPersistence {

    private val sharedPreferences: SharedPreferences by lazy {
        context.getSharedPreferences(prefsName, Context.MODE_PRIVATE)
    }

    private fun buildKey(sectionId: String, propertyId: String): String {
        return "$sectionId:$propertyId"
    }

    override fun getPreferenceValue(sectionId: String, propertyId: String, defaultValue: Int): Int {
        val key = buildKey(sectionId, propertyId)
        return sharedPreferences.getInt(key, defaultValue)
    }

    override fun getPreferenceValue(
        sectionId: String,
        propertyId: String,
        defaultValue: Double
    ): Double {
        val key = buildKey(sectionId, propertyId)
        val value = sharedPreferences.getString(key, null)?.toDoubleOrNull() ?: return defaultValue
        return value
    }

    override fun getPreferenceValue(
        sectionId: String,
        propertyId: String,
        defaultValue: String
    ): String {
        val key = buildKey(sectionId, propertyId)
        return sharedPreferences.getString(key, defaultValue) ?: defaultValue
    }

    override fun getPreferenceValue(
        sectionId: String,
        propertyId: String,
        defaultValue: Boolean
    ): Boolean {
        val key = buildKey(sectionId, propertyId)
        return sharedPreferences.getBoolean(key, defaultValue)
    }

    override fun setPreferenceValue(sectionId: String, propertyId: String, value: Int) {
        val editor = sharedPreferences.edit()
        val key = buildKey(sectionId, propertyId)
        editor.putInt(key, value).apply()
    }

    override fun setPreferenceValue(sectionId: String, propertyId: String, value: Double) {
        val editor = sharedPreferences.edit()
        val key = buildKey(sectionId, propertyId)
        editor.putString(key, value.toString())?.apply()
    }

    override fun setPreferenceValue(sectionId: String, propertyId: String, value: String) {
        val editor = sharedPreferences.edit()
        val key = buildKey(sectionId, propertyId)
        editor.putString(key, value)?.apply()
    }

    override fun setPreferenceValue(sectionId: String, propertyId: String, value: Boolean) {
        val editor = sharedPreferences.edit()
        val key = buildKey(sectionId, propertyId)
        editor.putBoolean(key, value)?.apply()
    }
}