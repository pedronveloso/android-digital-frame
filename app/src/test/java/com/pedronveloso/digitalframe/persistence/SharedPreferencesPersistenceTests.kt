package com.pedronveloso.digitalframe.persistence

import android.content.Context
import android.content.SharedPreferences
import androidx.test.core.app.ApplicationProvider
import com.google.common.truth.Truth.assertThat
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.time.LocalDate

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [Config.OLDEST_SDK])
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

        val result = sharedPreferencesPersistence.getPreferenceValue(section_id, property_key, 0)
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

        val storedValue = sharedPreferencesPersistence.getPreferenceValue(section_id, key, 0.0)
        assertThat(storedValue).isEqualTo(value)
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

        val result = sharedPreferencesPersistence.getPreferenceValue(section_id, key, "")
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

        val result = sharedPreferencesPersistence.getPreferenceValue(section_id, key, false)
        assertThat(result).isEqualTo(value)
    }

    @Test
    fun getPreferenceValue_returnsDefaultValue_whenKeyNotFound_forDate() {
        val defaultValue = LocalDate.of(2024, 2, 12)
        val result = sharedPreferencesPersistence.getPreferenceValue(
            "section",
            "missing_date_key",
            defaultValue
        )
        assertThat(result).isEqualTo(defaultValue)
    }

    @Test
    fun setPreferenceValue_savesDateValue() {
        val sectionId = "section"
        val propertyKey = "date_key"
        val value = LocalDate.of(2024, 2, 12)
        sharedPreferencesPersistence.setPreferenceValue(sectionId, propertyKey, value)

        val result =
            sharedPreferencesPersistence.getPreferenceValue(sectionId, propertyKey, LocalDate.MIN)
        assertThat(result).isEqualTo(value)
    }
}