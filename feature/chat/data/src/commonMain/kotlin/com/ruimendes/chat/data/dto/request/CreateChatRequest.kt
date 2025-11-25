package com.ruimendes.chat.data.dto.request

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CreateChatRequest(
    @SerialName("other_user_ids")
    val otherUserIds: List<String>
)
