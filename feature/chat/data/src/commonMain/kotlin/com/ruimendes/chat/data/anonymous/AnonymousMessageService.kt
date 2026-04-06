package com.ruimendes.chat.data.anonymous

import com.ruimendes.chat.domain.anonymous.AnonymousMessage
import com.ruimendes.core.domain.util.DataError
import com.ruimendes.core.domain.util.Result
import kotlin.time.Instant

interface AnonymousMessageService {
    suspend fun fetchMessages(
        before: Instant?,
        pageSize: Int
    ): Result<List<AnonymousMessage>, DataError.Remote>
}
