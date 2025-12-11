package com.ruimendes.chat.presentation.mappers

import com.ruimendes.chat.domain.models.ChatParticipant
import com.ruimendes.core.designsystem.components.avatar.ChatParticipantUI
import com.ruimendes.core.domain.auth.User

fun ChatParticipant.toUi(): ChatParticipantUI {
    return ChatParticipantUI(
        id = userId,
        username = username,
        initials = initials,
        imageUrl = profilePictureUrl
    )
}

fun User.toUi(): ChatParticipantUI {
    return ChatParticipantUI(
        id = id,
        username = username,
        initials = username.take(2).uppercase(),
        imageUrl = profilePictureUrl
    )
}