package com.ruimendes.core.domain.auth

import com.ruimendes.core.domain.util.DataError
import com.ruimendes.core.domain.util.EmptyResult

interface AuthService {
    suspend fun register(
        email: String,
        username: String,
        password: String,
    ): EmptyResult<DataError.Remote>
}