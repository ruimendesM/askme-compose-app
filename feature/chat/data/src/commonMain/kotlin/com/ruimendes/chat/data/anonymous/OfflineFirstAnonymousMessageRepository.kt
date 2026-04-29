package com.ruimendes.chat.data.anonymous

import com.ruimendes.chat.data.database.safeDatabaseUpdate
import com.ruimendes.chat.database.AppChatDatabase
import com.ruimendes.chat.domain.anonymous.AnonymousMessage
import com.ruimendes.chat.domain.anonymous.AnonymousMessageRepository
import com.ruimendes.core.domain.util.DataError
import com.ruimendes.core.domain.util.EmptyResult
import com.ruimendes.core.domain.util.Result
import com.ruimendes.core.domain.util.onSuccess
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlin.time.Instant

class OfflineFirstAnonymousMessageRepository(
    private val database: AppChatDatabase,
    private val service: AnonymousMessageService
) : AnonymousMessageRepository {

    private val dao get() = database.anonymousMessageDao

    override fun getMessages(): Flow<List<AnonymousMessage>> {
        return dao.getMessages().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun getLatestMessage(): Flow<AnonymousMessage?> {
        return dao.getLatestMessage().map { it?.toDomain() }
    }

    override suspend fun fetchMessages(
        before: Instant?,
        pageSize: Int
    ): Result<List<AnonymousMessage>, DataError> {
        return service.fetchMessages(before, pageSize)
            .onSuccess { messages ->
                safeDatabaseUpdate {
                    dao.upsertMessages(messages.map { it.toEntity() })
                }
            }
    }

    override suspend fun saveMessage(message: AnonymousMessage): EmptyResult<DataError.Local> {
        return safeDatabaseUpdate {
            dao.upsertMessage(message.toEntity())
        }
    }
}
