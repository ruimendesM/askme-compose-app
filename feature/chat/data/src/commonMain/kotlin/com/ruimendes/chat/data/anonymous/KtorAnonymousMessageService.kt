package com.ruimendes.chat.data.anonymous

import com.ruimendes.chat.domain.anonymous.AnonymousMessage
import com.ruimendes.core.data.networking.get
import com.ruimendes.core.domain.util.DataError
import com.ruimendes.core.domain.util.Result
import com.ruimendes.core.domain.util.map
import io.ktor.client.HttpClient
import kotlin.time.Instant

class KtorAnonymousMessageService(
    private val httpClient: HttpClient
) : AnonymousMessageService {

    companion object {
        const val PAGE_SIZE = 20
    }

    override suspend fun fetchMessages(
        before: Instant?,
        pageSize: Int
    ): Result<List<AnonymousMessage>, DataError.Remote> {
        return httpClient.get<List<AnonymousMessageDto>>(
            route = "api/anonymous-messages",
            queryParams = buildMap {
                this["pageSize"] = pageSize
                if (before != null) {
                    this["before"] = before.toString()
                }
            }
        ).map { dtos ->
            dtos.map { it.toDomain() }
        }
    }
}
