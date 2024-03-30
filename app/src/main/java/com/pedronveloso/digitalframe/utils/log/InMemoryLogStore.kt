package com.pedronveloso.digitalframe.utils.log

import android.util.Log
import java.time.LocalDateTime

class InMemoryLogStore : LogStore {

    private val logs = mutableListOf<LogEntry>()

    override fun log(message: String) {
        val callingClass = determineCallingClass()
        logs.add(LogEntry(callingClass, message, LocalDateTime.now(), LogLevel.INFO))
        Log.v(callingClass, message)
    }

    override fun logError(message: String, throwable: Throwable?) {
        val callingClass = determineCallingClass()
        logs.add(LogEntry(callingClass, "$message: ${throwable?.message}", LocalDateTime.now(), LogLevel.ERROR))
        Log.e(callingClass, message, throwable)
    }

    override fun getLogs(): List<LogEntry> {
        return logs.toList()
    }


    private fun determineCallingClass(): String {
        val stackTrace = Thread.currentThread().stackTrace
        val callingClass = stackTrace[3].className
        return callingClass
    }
}
