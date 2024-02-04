package com.pedronveloso.digitalframe.preferences

class PreferenceSectionBuilder {
    private val preferences = mutableListOf<PreferenceItem>()
    private val preferenceIds = mutableSetOf<String>()

    private fun checkIdUniqueness(id: String) {
        if (!preferenceIds.add(id)) {
            throw IllegalArgumentException("Preference ID '$id' is already used within the section.")
        }
    }

    fun inputField(
        id: String,
        title: String,
        hint: String? = null,
        type: InputType,
        onChangeCallback: ((String) -> Unit)? = null
    ) {
        checkIdUniqueness(id)
        preferences.add(
            PreferenceItem.InputFieldPreference(
                id,
                title,
                hint,
                type,
                onChangeCallback
            )
        )
    }

    fun switch(
        id: String,
        title: String,
        description: String? = null,
        default: Boolean,
        onChangeCallback: ((Boolean) -> Unit)? = null
    ) {
        checkIdUniqueness(id)
        preferences.add(PreferenceItem.Switch(id, title, description, default, onChangeCallback))
    }

    fun button(label: String, action: () -> Unit) {
        preferences.add(PreferenceItem.Button(label, action))
    }

    fun build(): List<PreferenceItem> = preferences
}

class PreferencesBuilder {
    private val sections = mutableListOf<PreferenceSection>()
    private val sectionIds = mutableSetOf<String>()

    private fun checkSectionIdUniqueness(id: String) {
        if (!sectionIds.add(id)) {
            throw IllegalArgumentException("Section ID '$id' is already used within the preferences.")
        }
    }

    fun section(id: String, title: String, init: PreferenceSectionBuilder.() -> Unit) {
        checkSectionIdUniqueness(id)
        val builder = PreferenceSectionBuilder()
        builder.init()
        sections.add(PreferenceSection(id, title, builder.build()))
    }

    fun build(): List<PreferenceSection> = sections
}

fun Preferences(init: PreferencesBuilder.() -> Unit): List<PreferenceSection> {
    val builder = PreferencesBuilder()
    builder.init()
    return builder.build()
}