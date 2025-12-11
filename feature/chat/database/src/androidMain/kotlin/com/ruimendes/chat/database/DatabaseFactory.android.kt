package com.ruimendes.chat.database

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase

actual class DatabaseFactory(
    private val context: Context
) {
    actual fun create(): RoomDatabase.Builder<AppChatDatabase> {
        val dbFile = context.applicationContext.getDatabasePath(AppChatDatabase.DATABASE_NAME)

        return Room.databaseBuilder(
            context.applicationContext,
            dbFile.absolutePath
        )
    }
}