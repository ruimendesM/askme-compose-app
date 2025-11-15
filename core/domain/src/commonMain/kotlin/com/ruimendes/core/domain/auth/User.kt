package com.ruimendes.core.domain.auth

data class User(
    val id: String,
    val email: String,
    val username: String,
    val hasEmailVerified: Boolean,
    val profilePictureUrl: String? = null
)
