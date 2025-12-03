package com.ruimendes.chat.domain.models

enum class ConnectionState {
    DISCONNECTED,
    CONNECTED,
    CONNECTING,
    ERROR_NETWORK,
    ERROR_UNKNOWN
}