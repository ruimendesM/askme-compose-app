package com.ruimendes.chat.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.ruimendes.chat.database.dao.ChatDao
import com.ruimendes.chat.database.dao.ChatMessageDao
import com.ruimendes.chat.database.dao.ChatParticipantDao
import com.ruimendes.chat.database.dao.ChatParticipantsCrossRefDao
import com.ruimendes.chat.database.entities.ChatEntity
import com.ruimendes.chat.database.entities.ChatMessageEntity
import com.ruimendes.chat.database.entities.ChatParticipantCrossRef
import com.ruimendes.chat.database.entities.ChatParticipantEntity
import com.ruimendes.chat.database.view.LastMessageView

@Database(
    entities = [
        ChatEntity::class,
        ChatParticipantEntity::class,
        ChatMessageEntity::class,
        ChatParticipantCrossRef::class
    ],
    views = [
        LastMessageView::class
    ],
    version = 1
)
abstract class AppChatDatabase: RoomDatabase() {
    abstract val chatDao: ChatDao
    abstract val chatParticipantDao: ChatParticipantDao
    abstract val chatMessageDao: ChatMessageDao
    abstract val chatParticipantsCrossRefDao: ChatParticipantsCrossRefDao

    companion object {
        const val DATABASE_NAME = "askme_chat.db"
    }
}