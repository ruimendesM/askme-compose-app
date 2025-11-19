package com.ruimendes.core.data.dto.requests

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ResetPasswordRequest(
    @SerialName("new_password")
    val newPassword: String,
    val token: String
)
