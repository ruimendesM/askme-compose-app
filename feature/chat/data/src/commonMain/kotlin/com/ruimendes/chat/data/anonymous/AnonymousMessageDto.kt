package com.ruimendes.chat.data.anonymous

import kotlinx.serialization.Serializable

@Serializable
data class AnonymousMessageDto(
    val id: String,
    val senderEmail: String,
    val content: String,
    val createdAt: String
)
