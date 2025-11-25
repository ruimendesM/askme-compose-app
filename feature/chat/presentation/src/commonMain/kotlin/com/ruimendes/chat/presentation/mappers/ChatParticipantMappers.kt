package com.ruimendes.chat.presentation.mappers

import com.ruimendes.chat.domain.models.ChatParticipant
import com.ruimendes.core.designsystem.components.avatar.ChatParticipantUi

fun ChatParticipant.toUi(): ChatParticipantUi {
    return ChatParticipantUi(
        id = userId,
        username = username,
        initials = initials,
        imageUrl = profilePictureUrl
    )

}