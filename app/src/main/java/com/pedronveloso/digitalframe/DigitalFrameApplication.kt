package com.pedronveloso.digitalframe

import android.app.Application
import com.pedronveloso.digitalframe.utils.log.LogStoreProvider
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class DigitalFrameApplication : Application(){

    private val logger = LogStoreProvider.getLogStore()

    override fun onCreate() {
        super.onCreate()
        logger.log("App Created")
    }
}
