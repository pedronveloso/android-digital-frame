package com.pedronveloso.digitalframe.preferences

class PreferenceSectionBuilder {
    private val preferences = mutableListOf<PreferenceItem>()

    fun inputField(title: String, hint: String? = null, type: InputType) {
        preferences.add(PreferenceItem.InputFieldPreference(title, hint, type))
    }

    fun switch(title: String, description: String? = null, default: Boolean) {
        preferences.add(PreferenceItem.Switch(title, description, default))
    }

    fun button(label: String, action: () -> Unit) {
        preferences.add(PreferenceItem.Button(label, action))
    }

    fun build(): List<PreferenceItem> = preferences
}

class PreferencesBuilder {
    private val sections = mutableListOf<PreferenceSection>()

    fun section(title: String, init: PreferenceSectionBuilder.() -> Unit) {
        val builder = PreferenceSectionBuilder()
        builder.init()
        sections.add(PreferenceSection(title, builder.build()))
    }

    fun build(): List<PreferenceSection> = sections
}

fun Preferences(init: PreferencesBuilder.() -> Unit): List<PreferenceSection> {
    val builder = PreferencesBuilder()
    builder.init()
    return builder.build()
}