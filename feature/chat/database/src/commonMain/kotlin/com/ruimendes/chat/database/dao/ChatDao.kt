package com.ruimendes.chat.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import com.ruimendes.chat.database.entities.ChatEntity
import com.ruimendes.chat.database.entities.ChatInfoEntity
import com.ruimendes.chat.database.entities.ChatMessageEntity
import com.ruimendes.chat.database.entities.ChatParticipantCrossRef
import com.ruimendes.chat.database.entities.ChatParticipantEntity
import com.ruimendes.chat.database.entities.ChatWithParticipants
import kotlinx.coroutines.flow.Flow

@Dao
interface ChatDao {

    @Upsert
    suspend fun upsertChat(chat: ChatEntity)

    @Upsert
    suspend fun upsertChats(chats: List<ChatEntity>)

    @Query(
        """
        DELETE
        FROM chatentity 
        WHERE chatId = :chatId
    """
    )
    suspend fun deleteChatById(chatId: String)

    @Query(
        """
        SELECT *
        FROM chatentity
        ORDER BY lastActivityAt
        DESC
    """
    )
    @Transaction
    fun getChatWithParticipants(): Flow<List<ChatWithParticipants>>

    @Query(
        """
        SELECT DISTINCT c.*
        FROM chatentity c
        JOIN chatparticipantcrossref cpcr ON c.chatId = cpcr.chatId
        WHERE cpcr.isActive = 1
        ORDER BY lastActivityAt DESC
    """
    )
    @Transaction
    fun getChatWithActiveParticipants(): Flow<List<ChatWithParticipants>>

    @Query(
        """
        SELECT *
        FROM chatentity
        WHERE chatId = :chatId
    """
    )
    @Transaction
    suspend fun getChatById(chatId: String): ChatWithParticipants?

    @Query(
        """
        DELETE
        FROM chatentity
    """
    )
    suspend fun deleteAllChats()

    @Query(
        """
        SELECT chatId
        FROM chatentity
        """
    )
    suspend fun getAllChatIds(): List<String>

    @Transaction
    suspend fun deleteChatsByIds(chatIds: List<String>) {
        chatIds.forEach { chatId ->
            deleteChatById(chatId)
        }
    }

    @Query(
        """
        SELECT COUNT(*)
        FROM chatentity
    """
    )
    fun getChatCount(): Flow<Int>

    @Query(
        """
            SELECT p.*
            FROM chatparticipantentity p
            JOIN chatparticipantcrossref cpcr ON p.userId = cpcr.userId
            WHERE cpcr.chatId = :chatId AND cpcr.isActive = true
            ORDER BY p.username
        """
    )
    fun getActiveParticipantsByChatId(chatId: String): Flow<List<ChatParticipantEntity>>

    @Query(
        """
        SELECT *
        FROM chatentity
        WHERE chatId = :chatId
    """
    )
    @Transaction
    fun getChatInfoById(chatId: String): Flow<ChatInfoEntity?>

    @Transaction
    suspend fun upsertChatWithParticipantsAndCrossRefs(
        chat: ChatEntity,
        participants: List<ChatParticipantEntity>,
        participantDao: ChatParticipantDao,
        crossRefDao: ChatParticipantsCrossRefDao
    ) {
        upsertChat(chat)
        participantDao.upsertParticipants(participants)

        val crossRefs = participants.map { participant ->
            ChatParticipantCrossRef(
                chatId = chat.chatId,
                userId = participant.userId,
                isActive = true
            )
        }
        crossRefDao.upsertCrossRefs(crossRefs)
        crossRefDao.syncChatParticipants(chat.chatId, participants)
    }

    @Transaction
    suspend fun upsertChatsWithParticipantsAndCrossRefs(
        chats: List<ChatWithParticipants>,
        participantDao: ChatParticipantDao,
        crossRefDao: ChatParticipantsCrossRefDao,
        messageDao: ChatMessageDao
    ) {
        upsertChats(chats.map { it.chat })

        val localChatIds = getAllChatIds()
        val serverChatIds = chats.map { it.chat.chatId }.toSet()
        val staleChatIds = localChatIds - serverChatIds

        chats.forEach { chat ->
            chat.lastMessage?.run {
                messageDao.upsertMessage(
                    ChatMessageEntity(
                        messageId = messageId,
                        chatId = chatId,
                        content = content,
                        timestamp = timestamp,
                        senderId = senderId,
                        deliveryStatus = deliveryStatus,
                    )
                )
            }
        }

        val allParticipants = chats.flatMap { it.participants }
        participantDao.upsertParticipants(allParticipants)

        val allCrossRefs = chats.flatMap { chatWithParticipants ->
            chatWithParticipants.participants.map { participant ->
                ChatParticipantCrossRef(
                    chatId = chatWithParticipants.chat.chatId,
                    userId = participant.userId,
                    isActive = true
                )
            }
        }
        crossRefDao.upsertCrossRefs(allCrossRefs)

        chats.forEach { chat ->
            crossRefDao.syncChatParticipants(
                chatId = chat.chat.chatId,
                participants = chat.participants
            )
        }

        deleteChatsByIds(staleChatIds)
    }
}