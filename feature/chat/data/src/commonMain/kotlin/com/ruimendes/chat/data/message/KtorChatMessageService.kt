package com.ruimendes.chat.data.message

import com.ruimendes.chat.data.dto.ChatMessageDto
import com.ruimendes.chat.data.mappers.toDomain
import com.ruimendes.chat.domain.message.ChatMessageService
import com.ruimendes.chat.domain.models.ChatMessage
import com.ruimendes.core.data.networking.get
import com.ruimendes.core.domain.util.DataError
import com.ruimendes.core.domain.util.Result
import com.ruimendes.core.domain.util.map
import io.ktor.client.HttpClient
import kotlin.text.set

class KtorChatMessageService(
    private val httpClient: HttpClient
) : ChatMessageService {

    override suspend fun fetchMessages(
        chatId: String,
        before: String?
    ): Result<List<ChatMessage>, DataError.Remote> {
        return httpClient.get<List<ChatMessageDto>>(
            route = "chat/$chatId/messages",
            queryParams = buildMap {
                this["pageSize"] = ChatMessageConstants.PAGE_SIZE
                if (before != null) {
                    this["before"] = before
                }
            }
        ).map {
            it.map { it.toDomain() }
        }
    }
}