package com.pedronveloso.digitalframe.data.vo

/**
 * Wrap all UI-related requests in one of 4 states: success, loading, failure or blank.
 *
 * Blank state, as the name suggests, typically represents that the initial state (pre-request) should be displayed.
 */
sealed class UiResult<in T> {
    class Blank<T> : UiResult<T>()

    class Loading<T> : UiResult<T>()

    class Failure<T>(val exception: Exception) : UiResult<T>()

    class Success<T>(val data: T) : UiResult<T>()

    companion object {
        fun <T> success(data: T): UiResult<T> = Success(data)

        fun <T> failure(exception: Exception): UiResult<T> = Failure(exception)
    }
}
