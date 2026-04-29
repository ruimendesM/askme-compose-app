package com.ruimendes.chat.presentation.chat_list

import com.ruimendes.chat.domain.chat.ChatRepository
import com.ruimendes.chat.domain.models.Chat
import com.ruimendes.chat.domain.models.ChatInfo
import com.ruimendes.chat.domain.models.ChatParticipant
import com.ruimendes.core.domain.util.DataError
import com.ruimendes.core.domain.util.EmptyResult
import com.ruimendes.core.domain.util.Result
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf

class FakeChatRepository : ChatRepository {

    private val chats = MutableStateFlow<List<Chat>>(emptyList())

    override fun getChats(): Flow<List<Chat>> = chats

    override fun getChatInfoById(chatId: String): Flow<ChatInfo> = flowOf()

    override fun getActiveParticipantsByChatId(chatId: String): Flow<List<ChatParticipant>> = flowOf()

    override suspend fun fetchChats(): Result<List<Chat>, DataError.Remote> =
        Result.Success(emptyList())

    override suspend fun fetchChatById(chatId: String): EmptyResult<DataError.Remote> =
        Result.Success(Unit)

    override suspend fun createChat(otherUserIds: List<String>): Result<Chat, DataError.Remote> =
        Result.Failure(DataError.Remote.UNKNOWN)

    override suspend fun leaveChat(chatId: String): EmptyResult<DataError.Remote> =
        Result.Success(Unit)

    override suspend fun addParticipantsToChat(
        chatId: String,
        userIds: List<String>
    ): Result<Chat, DataError.Remote> = Result.Failure(DataError.Remote.UNKNOWN)

    fun emit(newChats: List<Chat>) {
        chats.value = newChats
    }
}
