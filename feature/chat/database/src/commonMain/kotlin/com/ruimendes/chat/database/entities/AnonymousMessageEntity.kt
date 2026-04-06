package com.ruimendes.chat.database.entities

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "anonymous_message",
    indices = [
        Index(value = ["createdAt"])
    ]
)
data class AnonymousMessageEntity(
    @PrimaryKey
    val id: String,
    val senderEmail: String,
    val content: String,
    val createdAt: Long
)
