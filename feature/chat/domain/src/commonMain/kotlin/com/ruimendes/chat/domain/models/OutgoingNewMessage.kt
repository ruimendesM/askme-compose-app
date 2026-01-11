package com.ruimendes.chat.domain.models

data class OutgoingNewMessage(
    val chatId: String,
    val messageId: String,
    val content: String
)
