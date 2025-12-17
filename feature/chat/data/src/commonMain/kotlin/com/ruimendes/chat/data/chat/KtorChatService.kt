package com.ruimendes.chat.data.chat

import com.ruimendes.chat.data.dto.ChatDto
import com.ruimendes.chat.data.dto.request.CreateChatRequest
import com.ruimendes.chat.data.dto.request.ParticipantsRequest
import com.ruimendes.chat.data.mappers.toDomain
import com.ruimendes.chat.domain.chat.ChatService
import com.ruimendes.chat.domain.models.Chat
import com.ruimendes.core.data.networking.delete
import com.ruimendes.core.data.networking.get
import com.ruimendes.core.data.networking.post
import com.ruimendes.core.domain.util.DataError
import com.ruimendes.core.domain.util.EmptyResult
import com.ruimendes.core.domain.util.Result
import com.ruimendes.core.domain.util.asEmptyResult
import com.ruimendes.core.domain.util.map
import io.ktor.client.HttpClient
import io.ktor.client.request.delete

class KtorChatService(
    private val httpClient: HttpClient
): ChatService {

    override suspend fun createChat(otherUserIds: List<String>): Result<Chat, DataError.Remote> {
        return httpClient.post<CreateChatRequest, ChatDto>(
            route = "/chat",
            body = CreateChatRequest(otherUserIds)
        ).map { it.toDomain() }
    }

    override suspend fun getChats(): Result<List<Chat>, DataError.Remote> {
        return httpClient.get<List<ChatDto>>(
            route = "/chat"
        ).map { chatDtos ->
            chatDtos.map { it.toDomain() }
        }
    }

    override suspend fun getChatById(chatId: String): Result<Chat, DataError.Remote> {
       return httpClient.get<ChatDto>(
           route = "/chat/$chatId"
       ).map { it.toDomain() }
    }

    override suspend fun leaveChat(chatId: String): EmptyResult<DataError.Remote> {
        return httpClient.delete<Unit>(
            route = "/chat/$chatId/leave"
        ).asEmptyResult()
    }

    override suspend fun addParticipantsToChat(
        chatId: String,
        userIds: List<String>
    ): Result<Chat, DataError.Remote> {
        return httpClient.post<ParticipantsRequest, ChatDto>(
            route = "/chat/$chatId/add",
            body = ParticipantsRequest(userIds)
        ).map { it.toDomain() }
    }

}