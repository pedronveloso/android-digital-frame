package com.pedronveloso.digitalframe.preferences

data class PreferenceSection(
    val id: String,
    val title: String,
    val preferences: List<PreferenceItem>
)

sealed class PreferenceItem {
    data class InputFieldPreference(
        val id: String,
        val title: String,
        val hint: String? = null,
        val type: InputType,
        val onChangeCallback: ((String) -> Unit)? = null
    ) : PreferenceItem()

    data class Switch(
        val id: String,
        val title: String,
        val description: String? = null,
        val default: Boolean,
        val onChangeCallback: ((Boolean) -> Unit)? = null
    ) :
        PreferenceItem()

    data class Button(val label: String, val action: () -> Unit) : PreferenceItem()
}

enum class InputType {
    TEXT, INT, DOUBLE
}