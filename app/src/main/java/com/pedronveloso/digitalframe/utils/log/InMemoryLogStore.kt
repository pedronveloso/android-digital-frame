package com.pedronveloso.digitalframe.utils.log

import android.util.Log
import java.time.LocalDateTime
import java.util.LinkedList

class InMemoryLogStore : LogStore {

    private val logs = LinkedList<LogEntry>()

    override fun log(message: String) {
        val callingClass = determineCallingClass()
        addLogEntry(LogEntry(callingClass, message, LocalDateTime.now(), LogLevel.INFO))
        Log.v(callingClass, message)
    }

    override fun logError(message: String, throwable: Throwable?) {
        val callingClass = determineCallingClass()
        addLogEntry(
            LogEntry(
                callingClass,
                "$message: ${throwable?.message}",
                LocalDateTime.now(),
                LogLevel.ERROR
            )
        )
        Log.e(callingClass, message, throwable)
    }

    override fun getLogs(): List<LogEntry> {
        return logs.toList()
    }

    private fun addLogEntry(logEntry: LogEntry) {
        if (logs.size >= MAX_LOG_ENTRIES) {
            logs.removeFirst()
        }
        logs.add(logEntry)
    }

    private fun determineCallingClass(): String {
        val stackTrace = Thread.currentThread().stackTrace
        val callingClass = stackTrace[3].className
        return callingClass
    }

    companion object {
        private const val MAX_LOG_ENTRIES = 400
    }
}
