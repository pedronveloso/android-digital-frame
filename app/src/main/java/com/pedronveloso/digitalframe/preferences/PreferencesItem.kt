package com.pedronveloso.digitalframe.preferences

import com.pedronveloso.digitalframe.preferences.location.LocationData
import com.pedronveloso.digitalframe.utils.log.LogStoreProvider

sealed class PreferenceItem(open val id: String) {
    data class InputFieldPref(
        override val id: String,
        val title: String,
        val hint: String? = null,
        val type: InputType,
        val initialValueProvider: () -> String,
        val onChangeCallback: ((String) -> Unit)? = null,
    ) : PreferenceItem(id)

    class SwitchPref(
        override val id: String,
        val title: String,
        val description: String? = null,
        val initialValueProvider: () -> Boolean,
    ) : PreferenceItem(id) {
        var defaultValue: Boolean = initialValueProvider()
        var onChangeCallback: ((Boolean) -> Unit)? = null
            set(value) {
                field = { booleanValue ->
                    val logger = LogStoreProvider.getLogStore()
                    logger.log("SwitchPref $title changed to $booleanValue")
                    value?.invoke(booleanValue)
                }
            }
    }

    data class Button(override val id: String, val label: String, val action: () -> Unit) :
        PreferenceItem(id)

    data class Label(override val id: String, val text: String) : PreferenceItem(id)

    class LocationPref(
        override val id: String,
        val title: String,
        val description: String? = null,
        val initialValueProvider: () -> LocationData,
        val onChangeCallback: ((LocationData) -> Unit)? = null
    ) : PreferenceItem(id)

}

enum class InputType {
    TEXT,
    INT,
    DOUBLE,
    DATE,
}
