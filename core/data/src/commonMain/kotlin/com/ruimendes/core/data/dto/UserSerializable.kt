package com.ruimendes.core.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class UserSerializable(
    val id: String,
    val email: String,
    val username: String,
    val hasEmailVerified: Boolean,
    val profilePictureUrl: String? = null
)