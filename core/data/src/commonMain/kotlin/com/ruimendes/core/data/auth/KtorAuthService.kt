package com.ruimendes.core.data.auth

import com.ruimendes.core.data.dto.AuthInfoSerializable
import com.ruimendes.core.data.dto.requests.EmailRequest
import com.ruimendes.core.data.dto.requests.LoginRequest
import com.ruimendes.core.data.dto.requests.RegisterRequest
import com.ruimendes.core.data.dto.requests.ResetPasswordRequest
import com.ruimendes.core.data.mappers.toDomain
import com.ruimendes.core.data.networking.get
import com.ruimendes.core.data.networking.post
import com.ruimendes.core.domain.auth.AuthInfo
import com.ruimendes.core.domain.auth.AuthService
import com.ruimendes.core.domain.util.DataError
import com.ruimendes.core.domain.util.EmptyResult
import com.ruimendes.core.domain.util.Result
import com.ruimendes.core.domain.util.map
import io.ktor.client.HttpClient

class KtorAuthService(
    private val httpClient: HttpClient
) : AuthService {

    override suspend fun login(
        email: String,
        password: String
    ): Result<AuthInfo, DataError.Remote> {
        return httpClient.post<LoginRequest, AuthInfoSerializable>(
            route = "/auth/login",
            body = LoginRequest(
                email = email,
                password = password
            )
        ).map { authInfoSerializable ->
            authInfoSerializable.toDomain()
        }
    }

    override suspend fun register(
        email: String,
        username: String,
        password: String
    ): EmptyResult<DataError.Remote> {
        return httpClient.post(
            route = "/auth/register",
            body = RegisterRequest(
                email = email,
                username = username,
                password = password
            )
        )
    }

    override suspend fun resendVerificationEmail(email: String): EmptyResult<DataError.Remote> {
        return httpClient.post(
            route = "/auth/resend-verification",
            body = EmailRequest(email)
        )
    }

    override suspend fun verifyEmail(token: String): EmptyResult<DataError.Remote> {
        return httpClient.get(
            route = "/auth/verify",
            queryParams = mapOf("token" to token)
        )
    }

    override suspend fun forgotPassword(email: String): EmptyResult<DataError.Remote> {
        return httpClient.post(
            route = "auth/forgot-password",
            body = EmailRequest(email)
        )
    }

    override suspend fun resetPassword(
        newPassword: String,
        token: String
    ): EmptyResult<DataError.Remote> {
        return httpClient.post(
            route = "auth/reset-password",
            body = ResetPasswordRequest(newPassword, token)
        )
    }
}