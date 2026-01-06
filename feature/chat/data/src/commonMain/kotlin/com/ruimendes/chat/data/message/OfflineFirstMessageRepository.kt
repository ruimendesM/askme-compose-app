package com.ruimendes.chat.data.message

import com.ruimendes.chat.data.database.safeDatabaseUpdate
import com.ruimendes.chat.database.AppChatDatabase
import com.ruimendes.chat.domain.message.MessageRepository
import com.ruimendes.chat.domain.models.ChatMessageDeliveryStatus
import com.ruimendes.core.domain.util.DataError
import com.ruimendes.core.domain.util.EmptyResult
import kotlin.time.Clock

class OfflineFirstMessageRepository(
    private val database: AppChatDatabase
) : MessageRepository {

    override suspend fun updateMessageDeliveryStatus(
        messageId: String,
        status: ChatMessageDeliveryStatus
    ): EmptyResult<DataError.Local> {
        return safeDatabaseUpdate {
            database.chatMessageDao.updateDeliveryStatus(
                messageId = messageId,
                status = status.name,
                timestamp = Clock.System.now().toEpochMilliseconds()
            )
        }
    }
}