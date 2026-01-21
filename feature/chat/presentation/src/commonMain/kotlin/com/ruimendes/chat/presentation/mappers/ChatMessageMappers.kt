package com.ruimendes.chat.presentation.mappers

import com.ruimendes.chat.domain.models.MessageWithSender
import com.ruimendes.chat.presentation.model.MessageUI
import com.ruimendes.chat.presentation.util.DateUtils
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

fun List<MessageWithSender>.toUIList(localUserId: String): List<MessageUI> {
    return this
        .sortedByDescending { it.message.createdAt }
        .groupBy {
            it.message.createdAt.toLocalDateTime(TimeZone.currentSystemDefault()).date
        }
        .flatMap { (date, messages) ->
            messages.map { it.toUI(localUserId) } + MessageUI.DateSeparator(
                id = date.toString(),
                date = DateUtils.formatDateSeparator(date)
            )
        }
}

fun MessageWithSender.toUI(localUserId: String): MessageUI {
    val isFromLocalUser = this.sender.userId == localUserId
    return if (isFromLocalUser) {
        MessageUI.LocalUserMessage(
            id = message.id,
            content = message.content,
            deliveryStatus = message.deliveryStatus,
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