package com.pedronveloso.digitalframe.preferences

sealed class PreferenceItem(open val id: String) {
    data class InputFieldPref(
        override val id: String,
        val sectionId: String,
        val title: String,
        val hint: String? = null,
        val type: InputType,
        val onChangeCallback: ((String) -> Unit)? = null
    ) : PreferenceItem(id)


    class SwitchPref(
        override val id: String,
        val title: String,
        val description: String? = null,
        val defaultValueProvider: () -> Boolean
    ) : PreferenceItem(id) {

        var defaultValue: Boolean = defaultValueProvider()
        var onChangeCallback: ((Boolean) -> Unit)? = null
            set(value) {
                field = { booleanValue ->
                    value?.invoke(booleanValue)
                }
            }
    }

    data class Button(override val id: String, val label: String, val action: () -> Unit) :
        PreferenceItem(id)
}

enum class InputType {
    TEXT, INT, DOUBLE
}