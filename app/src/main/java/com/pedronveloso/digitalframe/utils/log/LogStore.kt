package com.pedronveloso.digitalframe.utils.log

import java.time.LocalDateTime

enum class LogLevel {
    INFO,
    ERROR,
}

class LogEntry(
    val tag: String,
    val message: String,
    val timestamp: LocalDateTime,
    val level: LogLevel,
)

interface LogStore {
    fun log(message: String)

    fun error(
        message: String,
        throwable: Throwable? = null,
    )

    fun getLogs(): List<LogEntry>
}
