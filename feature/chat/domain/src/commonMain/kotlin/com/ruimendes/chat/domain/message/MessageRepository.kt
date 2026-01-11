package com.ruimendes.chat.domain.message

import com.ruimendes.chat.domain.models.ChatMessage
import com.ruimendes.chat.domain.models.ChatMessageDeliveryStatus
import com.ruimendes.chat.domain.models.MessageWithSender
import com.ruimendes.chat.domain.models.OutgoingNewMessage
import com.ruimendes.core.domain.util.DataError
import com.ruimendes.core.domain.util.EmptyResult
import com.ruimendes.core.domain.util.Result
import kotlinx.coroutines.flow.Flow

interface MessageRepository {
    suspend fun updateMessageDeliveryStatus(
        messageId: String,
        status: ChatMessageDeliveryStatus
    ): EmptyResult<DataError.Local>

    suspend fun fetchMessages(
        chatId: String,
        before: String? = null
    ): Result<List<ChatMessage>, DataError>

    suspend fun sendMessage(
         message: OutgoingNewMessage
    ): EmptyResult<DataError>

    fun getMessagesForChat(chatId: String): Flow<List<MessageWithSender>>
}