package com.ruimendes.chat.presentation.chat_list

import com.ruimendes.core.domain.auth.AuthInfo
import com.ruimendes.core.domain.auth.SessionStorage
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

class FakeSessionStorage : SessionStorage {

    private val authInfo = MutableStateFlow<AuthInfo?>(null)

    override fun observeAuthInfo(): Flow<AuthInfo?> = authInfo

    override suspend fun set(info: AuthInfo?) {
        authInfo.value = info
    }

    fun emit(info: AuthInfo?) {
        authInfo.value = info
    }
}
