package com.pedronveloso.digitalframe.persistence

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class DataStorePreferencesPersistenceTest {

    private lateinit var context: Context
    private lateinit var persistence: DataStorePreferencesPersistence

    @Before
    fun setUp() {
        // Using Robolectric to get the context
        context = ApplicationProvider.getApplicationContext()
        persistence = DataStorePreferencesPersistence(context)
    }

    @Test
    fun testSetAndGetPreferenceValue_Int() = runBlocking {
        val key = "testIntKey"
        val expectedValue = 42

        persistence.setPreferenceValue(key, expectedValue)
        val actualValue = persistence.getPreferenceValue(key, 0)

        assertThat(actualValue).isEqualTo(expectedValue)
    }

    @Test
    fun testSetAndGetPreferenceValue_Double() = runBlocking {
        val key = "testDoubleKey"
        val expectedValue = 3.14

        persistence.setPreferenceValue(key, expectedValue)
        val actualValue = persistence.getPreferenceValue(key, 0.0)

        assertThat(actualValue).isEqualTo(expectedValue)
    }

    @Test
    fun testSetAndGetPreferenceValue_String() = runBlocking {
        val key = "testStringKey"
        val expectedValue = "testValue"

        persistence.setPreferenceValue(key, expectedValue)
        val actualValue = persistence.getPreferenceValue(key, "")

        assertThat(actualValue).isEqualTo(expectedValue)
    }
}