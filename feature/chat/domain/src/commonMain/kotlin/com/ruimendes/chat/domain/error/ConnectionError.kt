package com.ruimendes.chat.domain.error

import com.ruimendes.core.domain.util.Error

enum class ConnectionError: Error {
    NOT_CONNECTED,
    MESSAGE_SEND_FAILED
}