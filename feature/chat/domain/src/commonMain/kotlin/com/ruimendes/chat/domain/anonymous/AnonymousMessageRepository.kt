package com.ruimendes.chat.domain.anonymous

import com.ruimendes.core.domain.util.DataError
import com.ruimendes.core.domain.util.EmptyResult
import com.ruimendes.core.domain.util.Result
import kotlinx.coroutines.flow.Flow
import kotlin.time.Instant

interface AnonymousMessageRepository {
    fun getMessages(): Flow<List<AnonymousMessage>>
    fun getLatestMessage(): Flow<AnonymousMessage?>
    suspend fun fetchMessages(before: Instant?, pageSize: Int): Result<List<AnonymousMessage>, DataError>
    suspend fun saveMessage(message: AnonymousMessage): EmptyResult<DataError.Local>
}
