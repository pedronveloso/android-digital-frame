package com.pedronveloso.digitalframe.persistence

interface PluginDataPersistence {
    fun isEnabled(): Boolean

    fun setEnabled(value: Boolean)

    companion object {
        const val PROPERTY_ENABLED = "enabled"
    }
}
