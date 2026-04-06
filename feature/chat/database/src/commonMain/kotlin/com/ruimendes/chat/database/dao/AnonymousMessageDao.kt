package com.ruimendes.chat.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.ruimendes.chat.database.entities.AnonymousMessageEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AnonymousMessageDao {

    @Upsert
    suspend fun upsertMessage(entity: AnonymousMessageEntity)

    @Upsert
    suspend fun upsertMessages(entities: List<AnonymousMessageEntity>)

    @Query("SELECT * FROM anonymous_message ORDER BY createdAt DESC")
    fun getMessages(): Flow<List<AnonymousMessageEntity>>

    @Query("SELECT * FROM anonymous_message ORDER BY createdAt DESC LIMIT 1")
    fun getLatestMessage(): Flow<AnonymousMessageEntity?>

    @Query("SELECT * FROM anonymous_message WHERE createdAt < :before ORDER BY createdAt DESC LIMIT :pageSize")
    suspend fun getMessagesBefore(before: Long, pageSize: Int): List<AnonymousMessageEntity>
}
