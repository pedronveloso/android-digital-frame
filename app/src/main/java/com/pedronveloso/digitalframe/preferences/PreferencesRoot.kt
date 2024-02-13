package com.pedronveloso.digitalframe.preferences

data class PreferencesRoot(val sections: List<PreferencesSection>) {
    class Builder() {
        private val sections = mutableListOf<PreferencesSection>()
        private val sectionIds = mutableSetOf<String>()

        fun addSection(section: PreferencesSection) {
            checkSectionIdUniqueness(section.id)
            sections.add(section)
        }

        private fun checkSectionIdUniqueness(id: String) {
            if (!sectionIds.add(id)) {
                throw IllegalArgumentException("Section ID '$id' is already used within the preferences.")
            }
        }

        fun build(): PreferencesRoot = PreferencesRoot(sections)
    }
}