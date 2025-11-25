package com.ruimendes.chat.domain.chat

import com.ruimendes.chat.domain.models.ChatParticipant
import com.ruimendes.core.domain.util.DataError
import com.ruimendes.core.domain.util.Result

interface ChatParticipantService {
    suspend fun searchParticipant(query: String): Result<ChatParticipant, DataError.Remote>
}