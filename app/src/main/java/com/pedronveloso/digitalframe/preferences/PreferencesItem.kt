package com.pedronveloso.digitalframe.preferences

data class PreferenceSection(
    val title: String,
    val preferences: List<PreferenceItem>
)

sealed class PreferenceItem {
    data class InputFieldPreference(
        val title: String,
        val hint: String? = null,
        val type: InputType
    ) : PreferenceItem()

    data class Switch(val title: String, val description: String? = null, val default: Boolean) :
        PreferenceItem()

    data class Button(val label: String, val action: () -> Unit) : PreferenceItem()
}

enum class InputType {
    TEXT, INT, DOUBLE
}