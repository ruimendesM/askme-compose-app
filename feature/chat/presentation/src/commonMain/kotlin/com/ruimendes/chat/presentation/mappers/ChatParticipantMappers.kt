package com.ruimendes.chat.presentation.mappers

import com.ruimendes.chat.domain.models.ChatParticipant
import com.ruimendes.core.designsystem.components.avatar.ChatParticipantUI

fun ChatParticipant.toUi(): ChatParticipantUI {
    return ChatParticipantUI(
        id = userId,
        username = username,
        initials = initials,
        imageUrl = profilePictureUrl
    )

}