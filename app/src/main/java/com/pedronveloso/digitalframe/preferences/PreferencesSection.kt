package com.pedronveloso.digitalframe.preferences

data class PreferencesSection(
    val id: String,
    val title: String,
    val preferenceItems: List<PreferenceItem>,
) {
    class Builder(
        private val sectionId: String,
        private val title: String,
    ) {
        private val preferences = mutableListOf<PreferenceItem>()
        private val preferenceIds = mutableSetOf<String>()

        private fun checkIdUniqueness(id: String) {
            if (!preferenceIds.add(id)) {
                throw IllegalArgumentException("Preference ID '$id' is already used within the section.")
            }
        }

        fun addPreference(preference: PreferenceItem) {
            checkIdUniqueness(preference.id)
            preferences.add(preference)
        }

        fun build(): PreferencesSection = PreferencesSection(sectionId, title, preferences)
    }
}
