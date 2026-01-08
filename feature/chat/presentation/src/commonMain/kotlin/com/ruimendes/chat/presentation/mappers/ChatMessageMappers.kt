package com.ruimendes.chat.presentation.mappers

import com.ruimendes.chat.domain.models.MessageWithSender
import com.ruimendes.chat.presentation.model.MessageUI
import com.ruimendes.chat.presentation.util.DateUtils

fun MessageWithSender.toUI(localUserId: String): MessageUI {
    val isFromLocalUser = this.sender.userId == localUserId
    return if (isFromLocalUser) {
        MessageUI.LocalUserMessage(
            id = message.id,
            content = message.content,
            deliveryStatus = message.deliveryStatus,
            isMenuOpen = false,
            formattedSentTime = DateUtils.formatMessageTime(instant = message.createdAt)
        )
    } else {
        MessageUI.OtherUserMessage(
            id = message.id,
            content = message.content,
            formattedSentTime = DateUtils.formatMessageTime(instant = message.createdAt),
            sender = sender.toUi()
        )
    }
}