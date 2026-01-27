package com.ruimendes.chat.domain.participant

import com.ruimendes.chat.domain.models.ChatParticipant
import com.ruimendes.core.domain.util.DataError
import com.ruimendes.core.domain.util.Result

interface ChatParticipantService {
    suspend fun searchParticipant(query: String): Result<ChatParticipant, DataError.Remote>

    suspend fun getLocalParticipant(): Result<ChatParticipant, DataError.Remote>
}