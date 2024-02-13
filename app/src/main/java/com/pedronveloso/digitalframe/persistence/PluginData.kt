package com.pedronveloso.digitalframe.persistence

interface PluginData {
    fun isEnabled(): Boolean

    fun setEnabled(value: Boolean)

    companion object {
        const val PROPERTY_ENABLED = "enabled"
    }
}
