package com.ruimendes.chat.data.notification

import com.ruimendes.chat.data.dto.request.RegisterDeviceTokenRequest
import com.ruimendes.chat.domain.notifications.DeviceTokenService
import com.ruimendes.core.data.networking.delete
import com.ruimendes.core.data.networking.post
import com.ruimendes.core.domain.util.DataError
import com.ruimendes.core.domain.util.EmptyResult
import io.ktor.client.HttpClient

class KtorDeviceTokenService(
    private val httpClient: HttpClient
): DeviceTokenService {

    override suspend fun registerToken(
        token: String,
        platform: String
    ): EmptyResult<DataError.Remote> {
        return httpClient.post(
            route = "/notification/register",
            body = RegisterDeviceTokenRequest(
                token = token,
                platform = platform
            )
        )
    }

    override suspend fun unregisterToken(token: String): EmptyResult<DataError.Remote> {
        return httpClient.delete(
            route = "/notification/${token}}"
        )
    }
}