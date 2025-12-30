package com.ruimendes.chat.data.network

class IOSNetworkCancellationException(
    message: String,
    cause: Throwable? = null
): Exception(message, cause)