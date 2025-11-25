package com.ruimendes.chat.data.chat

import com.ruimendes.chat.data.dto.ChatParticipantDto
import com.ruimendes.chat.data.mappers.toDomain
import com.ruimendes.chat.domain.chat.ChatParticipantService
import com.ruimendes.chat.domain.models.ChatParticipant
import com.ruimendes.core.data.networking.get
import com.ruimendes.core.domain.util.DataError
import com.ruimendes.core.domain.util.Result
import com.ruimendes.core.domain.util.map
import io.ktor.client.HttpClient

class KtorChatParticipantService(
    private val httpClient: HttpClient
): ChatParticipantService {

    override suspend fun searchParticipant(query: String): Result<ChatParticipant, DataError.Remote> {
        return httpClient.get<ChatParticipantDto>(
            route = "/participants",
            queryParams = mapOf("query" to query)
        ).map { it.toDomain() }
    }
}