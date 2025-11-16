package com.ruimendes.core.data.mappers

import com.ruimendes.core.data.dto.AuthInfoSerializable
import com.ruimendes.core.data.dto.UserSerializable
import com.ruimendes.core.domain.auth.AuthInfo
import com.ruimendes.core.domain.auth.User

fun AuthInfoSerializable.toDomain(): AuthInfo {
    return AuthInfo(
        accessToken = accessToken,
        refreshToken = refreshToken,
        user = user.toDomain()
    )
}

fun UserSerializable.toDomain(): User {
    return User(
        id = id,
        email = email,
        username = username,
        hasEmailVerified = hasEmailVerified,
        profilePictureUrl = profilePictureUrl
    )
}

fun AuthInfo.toSerializable(): AuthInfoSerializable {
    return AuthInfoSerializable(
        accessToken = accessToken,
        refreshToken = refreshToken,
        user = user.toSerializable()
    )
}

fun User.toSerializable(): UserSerializable {
    return UserSerializable(
        id = id,
        email = email,
        username = username,
        hasEmailVerified = hasEmailVerified,
        profilePictureUrl = profilePictureUrl
    )
}