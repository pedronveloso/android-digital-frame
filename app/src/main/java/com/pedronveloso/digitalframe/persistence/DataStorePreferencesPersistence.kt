package com.pedronveloso.digitalframe.persistence

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first

class DataStorePreferencesPersistence(private val context: Context) : PreferencesPersistence {
    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

    private fun preferencesKey(key: String) = stringPreferencesKey(key)

    override suspend fun getPreferenceValue(key: String, defaultValue: Int): Int {
        val dataStoreKey = preferencesKey(key)
        val preferences = context.dataStore.data.first()
        return preferences[dataStoreKey]?.toInt() ?: defaultValue
    }

    override suspend fun getPreferenceValue(key: String, defaultValue: Double): Double {
        val dataStoreKey = preferencesKey(key)
        val preferences = context.dataStore.data.first()
        return (preferences[dataStoreKey] ?: defaultValue.toString()).toDouble()
    }

    override suspend fun getPreferenceValue(key: String, defaultValue: String): String {
        val dataStoreKey = preferencesKey(key)
        val preferences = context.dataStore.data.first()
        return preferences[dataStoreKey] ?: defaultValue
    }

    override suspend fun setPreferenceValue(key: String, value: Int) {
        val dataStoreKey = preferencesKey(key)
        context.dataStore.edit { settings ->
            settings[dataStoreKey] = value.toString()
        }
    }

    override suspend fun setPreferenceValue(key: String, value: Double) {
        val dataStoreKey = preferencesKey(key)
        context.dataStore.edit { settings ->
            settings[dataStoreKey] = value.toString()
        }
    }

    override suspend fun setPreferenceValue(key: String, value: String) {
        val dataStoreKey = preferencesKey(key)
        context.dataStore.edit { settings ->
            settings[dataStoreKey] = value
        }
    }
}
