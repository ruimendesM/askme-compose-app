package com.ruimendes.core.domain.auth

data class AuthInfo(
    val accessToken: String,
    val refreshToken: String,
    val user: User,
) {
    val isAdmin: Boolean get() = user.role == "ADMIN"
}
