package com.ruimendes.chat.data.network

import com.ruimendes.chat.domain.models.ConnectionState

expect class ConnectionErrorHandler {
    fun getConnectionStateForError(cause: Throwable): ConnectionState
    fun transformException(exception: Throwable): Throwable
    fun isRetryableError(cause: Throwable): Boolean
}