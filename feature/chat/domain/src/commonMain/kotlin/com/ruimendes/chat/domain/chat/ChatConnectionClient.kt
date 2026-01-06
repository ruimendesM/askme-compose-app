package com.ruimendes.chat.domain.chat

import com.ruimendes.chat.domain.error.ConnectionError
import com.ruimendes.chat.domain.models.ChatMessage
import com.ruimendes.chat.domain.models.ConnectionState
import com.ruimendes.core.domain.util.EmptyResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

interface ChatConnectionClient {
    val chatMessages: Flow<ChatMessage>
    val connectionState: StateFlow<ConnectionState>
    suspend fun sendChatMessage(message: ChatMessage): EmptyResult<ConnectionError>
}