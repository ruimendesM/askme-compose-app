package com.ruimendes.chat.presentation.mappers

import com.ruimendes.chat.domain.anonymous.AnonymousMessage
import com.ruimendes.chat.presentation.model.MessageUI
import com.ruimendes.chat.presentation.util.DateUtils
import com.ruimendes.core.designsystem.components.avatar.ChatParticipantUI
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

fun List<AnonymousMessage>.toAnonymousUIList(): List<MessageUI> {
    return this
        .sortedByDescending { it.createdAt }
        .groupBy {
            it.createdAt.toLocalDateTime(TimeZone.currentSystemDefault()).date
        }
        .flatMap { (date, messages) ->
            messages.map { it.toAnonymousMessageUI() } + MessageUI.DateSeparator(
                id = date.toString(),
                date = DateUtils.formatDateSeparator(date)
            )
        }
}

fun AnonymousMessage.toAnonymousMessageUI(): MessageUI.OtherUserMessage {
    val initials = senderEmail.take(2).uppercase()
    return MessageUI.OtherUserMessage(
        id = id,
        content = content,
        formattedSentTime = DateUtils.formatMessageTime(createdAt),
        sender = ChatParticipantUI(
            id = senderEmail,
            username = senderEmail,
            initials = initials,
            imageUrl = null
        )
    )
}
