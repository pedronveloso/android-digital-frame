package com.pedronveloso.digitalframe.persistence

import android.content.Context
import android.content.SharedPreferences
import androidx.test.core.app.ApplicationProvider
import com.google.common.truth.Truth.assertThat
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class SharedPreferencesPersistenceTest {

    private lateinit var sharedPreferencesPersistence: SharedPreferencesPersistence
    private lateinit var sharedPreferences: SharedPreferences
    private val context: Context = ApplicationProvider.getApplicationContext()

    @Before
    fun setUp() {
        val sharedPreferencesName = "test_settings_preferences"
        sharedPreferences =
            context.getSharedPreferences(sharedPreferencesName, Context.MODE_PRIVATE)
        sharedPreferences.edit().clear().commit() // Clear SharedPreferences before each test
        sharedPreferencesPersistence = SharedPreferencesPersistence(context)
    }

    @Test
    fun getPreferenceValue_returnsDefaultValue_whenKeyNotFound_forInt() {
        val defaultValue = 0
        val result = sharedPreferencesPersistence.getPreferenceValue(
            "section",
            "missing_int_key",
            defaultValue
        )
        assertThat(result).isEqualTo(defaultValue)
    }

    @Test
    fun setPreferenceValue_savesIntValue() {
        val section_id = "section"
        val property_key = "int_key"
        val value = 42
        sharedPreferencesPersistence.setPreferenceValue(section_id, property_key, value)

        val result = sharedPreferences.getInt("$section_id:$property_key", 0)
        assertThat(result).isEqualTo(value)
    }

    @Test
    fun getPreferenceValue_returnsDefaultValue_whenKeyNotFound_forDouble() {
        val defaultValue = 0.0
        val result = sharedPreferencesPersistence.getPreferenceValue(
            "section",
            "missing_double_key",
            defaultValue
        )
        assertThat(result).isEqualTo(defaultValue)
    }

    @Test
    fun setPreferenceValue_savesDoubleValue() {
        val section_id = "section"
        val key = "double_key"
        val value = 42.42
        sharedPreferencesPersistence.setPreferenceValue(section_id, key, value)

        val storedValue = sharedPreferences.getString("$section_id:$key", null)
        assertThat(storedValue?.toDouble()).isEqualTo(value)
    }

    @Test
    fun getPreferenceValue_returnsDefaultValue_whenKeyNotFound_forString() {
        val defaultValue = "default"
        val result = sharedPreferencesPersistence.getPreferenceValue(
            "section",
            "missing_string_key",
            defaultValue
        )
        assertThat(result).isEqualTo(defaultValue)
    }

    @Test
    fun setPreferenceValue_savesStringValue() {
        val section_id = "section"
        val key = "string_key"
        val value = "testValue"
        sharedPreferencesPersistence.setPreferenceValue(section_id, key, value)

        val result = sharedPreferences.getString("$section_id:$key", null)
        assertThat(result).isEqualTo(value)
    }

    @Test
    fun getPreferenceValue_returnsDefaultValue_whenKeyNotFound_forBoolean() {
        val defaultValue = false
        val result = sharedPreferencesPersistence.getPreferenceValue(
            "section",
            "missing_boolean_key",
            defaultValue
        )
        assertThat(result).isEqualTo(defaultValue)
    }

    @Test
    fun setPreferenceValue_savesBooleanValue() {
        val section_id = "section"
        val key = "boolean_key"
        val value = true
        sharedPreferencesPersistence.setPreferenceValue(section_id, key, value)

        val result = sharedPreferences.getBoolean("$section_id:$key", false)
        assertThat(result).isEqualTo(value)
    }
}