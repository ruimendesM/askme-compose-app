package com.ruimendes.chat.presentation.util

import askme.feature.chat.presentation.generated.resources.Res
import askme.feature.chat.presentation.generated.resources.network_error
import askme.feature.chat.presentation.generated.resources.offline
import askme.feature.chat.presentation.generated.resources.online
import askme.feature.chat.presentation.generated.resources.reconnecting
import askme.feature.chat.presentation.generated.resources.unknown_error
import com.ruimendes.chat.domain.models.ConnectionState
import com.ruimendes.core.presentation.util.UiText

fun ConnectionState.toUiText(): UiText {
    val resource=  when(this) {
        ConnectionState.DISCONNECTED -> Res.string.offline
        ConnectionState.CONNECTED -> Res.string.reconnecting
        ConnectionState.CONNECTING -> Res.string.online
        ConnectionState.ERROR_NETWORK -> Res.string.network_error
        ConnectionState.ERROR_UNKNOWN -> Res.string.unknown_error
    }

    return UiText.Resource(resource)
}