package com.ruimendes.chat.presentation.model

import com.ruimendes.chat.domain.models.ChatMessageDeliveryStatus
import com.ruimendes.core.designsystem.components.avatar.ChatParticipantUI
import com.ruimendes.core.presentation.util.UiText

sealed interface MessageUI {

    data class LocalUserMessage(
        val id: String,
        val content: String,
        val deliveryStatus: ChatMessageDeliveryStatus,
        val isMenuOpen: Boolean,
        val formattedSentTime: UiText
    ) : MessageUI

    data class OtherUserMessage(
        val id: String,
        val content: String,
        val formattedSentTime: UiText,
        val sender: ChatParticipantUI
    ) : MessageUI

    data class DateSeparator(
        val id: String,
        val date: UiText
    ): MessageUI

}