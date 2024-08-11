package com.pedronveloso.digitalframe.elements.weather

import com.pedronveloso.digitalframe.data.openweather.WindSpeedUnit
import com.pedronveloso.digitalframe.persistence.PluginDataPersistence
import com.pedronveloso.digitalframe.persistence.PluginDataPersistence.Companion.PROPERTY_ENABLED
import com.pedronveloso.digitalframe.persistence.PreferencesPersistence

interface WeatherPersistence : PluginDataPersistence {
    fun setUseCelsius(value: Boolean)

    fun useCelsius(): Boolean

    fun windSpeedUnit(): WindSpeedUnit

    fun setWindSpeedUnit(value: WindSpeedUnit)

    fun showWind(): Boolean

    fun setShowWind(value: Boolean)
}

class RealWeatherPersistence(
    private val persistence: PreferencesPersistence,
) : WeatherPersistence {
    override fun setUseCelsius(value: Boolean) {
        persistence.setPreferenceValue(SECTION_ID, USE_CELSIUS, value)
    }

    override fun useCelsius(): Boolean = persistence.getPreferenceValue(SECTION_ID, USE_CELSIUS, true)

    override fun windSpeedUnit(): WindSpeedUnit =
        WindSpeedUnit.valueOf(
            persistence.getPreferenceValue(
                SECTION_ID,
                WIND_SPEED_UNIT,
                WindSpeedUnit.MetersPerSecond.name,
            ),
        )

    override fun setWindSpeedUnit(value: WindSpeedUnit) {
        persistence.setPreferenceValue(SECTION_ID, WIND_SPEED_UNIT, value.name)
    }

    override fun showWind(): Boolean = persistence.getPreferenceValue(SECTION_ID, SHOW_WIND, true)

    override fun setShowWind(value: Boolean) {
        persistence.setPreferenceValue(SECTION_ID, SHOW_WIND, value)
    }

    override fun isEnabled(): Boolean = persistence.getPreferenceValue(SECTION_ID, PROPERTY_ENABLED, true)

    override fun setEnabled(value: Boolean) {
        persistence.setPreferenceValue(SECTION_ID, PROPERTY_ENABLED, value)
    }

    companion object {
        private const val SECTION_ID = "weather"
        private const val USE_CELSIUS = "use-celsius"
        private const val SHOW_WIND = "show-wind"
        private const val WIND_SPEED_UNIT = "wind-speed-unit"
    }
}

class FakeWeatherPersistence : WeatherPersistence {
    private var enabled = true
    private var useCelsius = true
    private var showWind = true
    private var windSpeedUnit = WindSpeedUnit.MetersPerSecond

    override fun setUseCelsius(value: Boolean) {
        useCelsius = value
    }

    override fun useCelsius(): Boolean = useCelsius

    override fun windSpeedUnit(): WindSpeedUnit = windSpeedUnit

    override fun setWindSpeedUnit(value: WindSpeedUnit) {
        windSpeedUnit = value
    }

    override fun showWind(): Boolean = showWind

    override fun setShowWind(value: Boolean) {
        showWind = value
    }

    override fun isEnabled(): Boolean = enabled

    override fun setEnabled(value: Boolean) {
        enabled = value
    }
}
