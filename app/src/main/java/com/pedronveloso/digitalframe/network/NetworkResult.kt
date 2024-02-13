package com.pedronveloso.digitalframe.network

/**
 * Wrap all I/O-related requests in one of 2 states: success or failure.
 */
sealed class NetworkResult<in T> {
    class Failure<T>(val exception: Exception, val data: T? = null) : NetworkResult<T>()

    class Success<T>(val data: T) : NetworkResult<T>()

    companion object {
        fun <T> success(data: T): NetworkResult<T> = Success(data)

        fun <T> failure(
            exception: Exception,
            data: T? = null,
        ): NetworkResult<T> = Failure(exception, data)
    }
}
