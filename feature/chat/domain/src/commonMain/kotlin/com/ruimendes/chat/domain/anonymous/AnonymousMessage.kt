package com.ruimendes.chat.domain.anonymous

import kotlin.time.Instant

data class AnonymousMessage(
    val id: String,
    val senderEmail: String,
    val content: String,
    val createdAt: Instant
)
