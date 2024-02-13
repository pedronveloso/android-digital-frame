package com.pedronveloso.digitalframe.elements.weather

import com.pedronveloso.digitalframe.persistence.PluginData
import com.pedronveloso.digitalframe.persistence.PluginData.Companion.PROPERTY_ENABLED
import com.pedronveloso.digitalframe.persistence.PreferencesPersistence

interface WeatherData : PluginData {
    fun setUseCelsius(value: Boolean)
    fun useCelsius(): Boolean

    fun showWind(): Boolean
    fun setShowWind(value: Boolean)
}

class RealWeatherData(private val persistence: PreferencesPersistence) : WeatherData {
    override fun setUseCelsius(value: Boolean) {
        persistence.setPreferenceValue(SECTION_ID, USE_CELSIUS, value)
    }

    override fun useCelsius(): Boolean {
        return persistence.getPreferenceValue(SECTION_ID, USE_CELSIUS, true)
    }

    override fun showWind(): Boolean {
        return persistence.getPreferenceValue(SECTION_ID, SHOW_WIND, true)
    }

    override fun setShowWind(value: Boolean) {
        persistence.setPreferenceValue(SECTION_ID, SHOW_WIND, value)
    }

    override fun isEnabled(): Boolean {
        return persistence.getPreferenceValue(SECTION_ID, PROPERTY_ENABLED, true)
    }

    override fun setEnabled(value: Boolean) {
        persistence.setPreferenceValue(SECTION_ID, PROPERTY_ENABLED, value)

    }

    companion object {
        private const val SECTION_ID = "weather"
        private const val USE_CELSIUS = "use-celsius"
        private const val SHOW_WIND = "show-wind"
    }
}

class FakeWeatherData : WeatherData {
    private var enabled = true
    private var useCelsius = true
    private var showWind = true

    override fun setUseCelsius(value: Boolean) {
        useCelsius = value
    }

    override fun useCelsius(): Boolean {
        return useCelsius
    }

    override fun showWind(): Boolean {
        return showWind
    }

    override fun setShowWind(value: Boolean) {
        showWind = value
    }

    override fun isEnabled(): Boolean {
        return enabled
    }

    override fun setEnabled(value: Boolean) {
        enabled = value
    }
}