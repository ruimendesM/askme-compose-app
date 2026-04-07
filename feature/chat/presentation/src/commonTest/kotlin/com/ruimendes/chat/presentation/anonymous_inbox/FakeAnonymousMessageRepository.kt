package com.ruimendes.chat.presentation.anonymous_inbox

import com.ruimendes.chat.domain.anonymous.AnonymousMessage
import com.ruimendes.chat.domain.anonymous.AnonymousMessageRepository
import com.ruimendes.core.domain.util.DataError
import com.ruimendes.core.domain.util.EmptyResult
import com.ruimendes.core.domain.util.Result
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlin.time.Instant

class FakeAnonymousMessageRepository : AnonymousMessageRepository {

    private val messages = MutableStateFlow<List<AnonymousMessage>>(emptyList())
    var fetchResult: Result<List<AnonymousMessage>, DataError> = Result.Success(emptyList())

    override fun getMessages(): Flow<List<AnonymousMessage>> = messages

    override fun getLatestMessage(): Flow<AnonymousMessage?> {
        return messages.map { it.maxByOrNull { m -> m.createdAt } }
    }

    override suspend fun fetchMessages(
        before: Instant?,
        pageSize: Int
    ): Result<List<AnonymousMessage>, DataError> = fetchResult

    override suspend fun saveMessage(message: AnonymousMessage): EmptyResult<DataError.Local> {
        messages.value = messages.value + message
        return Result.Success(Unit)
    }

    fun emit(newMessages: List<AnonymousMessage>) {
        messages.value = newMessages
    }
}
