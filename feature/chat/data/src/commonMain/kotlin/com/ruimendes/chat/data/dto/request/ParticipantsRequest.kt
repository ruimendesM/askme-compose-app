package com.ruimendes.chat.data.dto.request

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ParticipantsRequest(
    @SerialName("user_ids")
    val userIds: List<String>
)
