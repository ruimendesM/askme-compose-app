package com.ruimendes.chat.domain.chat

import com.ruimendes.chat.domain.models.Chat
import com.ruimendes.core.domain.util.DataError
import com.ruimendes.core.domain.util.Result

interface ChatService {
    suspend fun createChat(
        otherUserIds: List<String>
    ): Result<Chat, DataError.Remote>
}