package com.ruimendes.core.data.dto

import com.ruimendes.core.domain.auth.User
import kotlinx.serialization.Serializable

@Serializable
data class AuthInfoSerializable(
    val accessToken: String,
    val refreshToken: String,
    val user: UserSerializable
)