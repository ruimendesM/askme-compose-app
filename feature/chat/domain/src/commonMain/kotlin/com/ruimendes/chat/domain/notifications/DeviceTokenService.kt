package com.ruimendes.chat.domain.notifications

import com.ruimendes.core.domain.util.DataError
import com.ruimendes.core.domain.util.EmptyResult

interface DeviceTokenService {

    suspend fun registerToken(
        token: String,
        platform: String
    ): EmptyResult<DataError.Remote>

    suspend fun unregisterToken(
        token: String
    ): EmptyResult<DataError.Remote>
}