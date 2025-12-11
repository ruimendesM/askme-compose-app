package com.ruimendes.chat.domain.chat

import com.ruimendes.chat.domain.models.Chat
import com.ruimendes.core.domain.util.DataError
import com.ruimendes.core.domain.util.Result
import kotlinx.coroutines.flow.Flow

interface ChatRepository {
    fun getChats(): Flow<List<Chat>>
    suspend fun fetchChats(): Result<List<Chat>, DataError.Remote>
}