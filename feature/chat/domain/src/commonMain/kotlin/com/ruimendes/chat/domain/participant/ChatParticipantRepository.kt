package com.ruimendes.chat.domain.participant

import com.ruimendes.chat.domain.models.ChatParticipant
import com.ruimendes.core.domain.util.DataError
import com.ruimendes.core.domain.util.Result

interface ChatParticipantRepository {
    suspend fun fetchLocalParticipant(): Result<ChatParticipant, DataError>
}