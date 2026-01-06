package com.ruimendes.chat.domain.message

import com.ruimendes.chat.domain.models.ChatMessageDeliveryStatus
import com.ruimendes.core.domain.util.DataError
import com.ruimendes.core.domain.util.EmptyResult

interface MessageRepository {
    suspend fun updateMessageDeliveryStatus(
        messageId: String,
        status: ChatMessageDeliveryStatus
    ): EmptyResult<DataError.Local>
}