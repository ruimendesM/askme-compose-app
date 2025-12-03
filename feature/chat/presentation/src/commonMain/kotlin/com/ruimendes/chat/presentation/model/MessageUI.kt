package com.ruimendes.chat.presentation.model

import com.ruimendes.chat.domain.models.ChatMessageDeliveryStatus
import com.ruimendes.core.designsystem.components.avatar.ChatParticipantUI
import com.ruimendes.core.presentation.util.UiText

sealed class MessageUI(open val id: String) {

    data class LocalUserMessage(
        override val id: String,
        val content: String,
        val deliveryStatus: ChatMessageDeliveryStatus,
        val isMenuOpen: Boolean,
        val formattedSentTime: UiText
    ) : MessageUI(id)

    data class OtherUserMessage(
        override val id: String,
        val content: String,
        val formattedSentTime: UiText,
        val sender: ChatParticipantUI
    ) : MessageUI(id)

    data class DateSeparator(
        override val id: String,
        val date: UiText
    ): MessageUI(id)

}