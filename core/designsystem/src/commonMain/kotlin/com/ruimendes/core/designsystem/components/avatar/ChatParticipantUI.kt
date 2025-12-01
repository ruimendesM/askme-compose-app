package com.ruimendes.core.designsystem.components.avatar

data class ChatParticipantUI(
    val id: String,
    val username: String,
    val initials: String,
    val imageUrl: String? = null
)
