package com.ruimendes.chat.data.mappers

import com.ruimendes.chat.data.dto.ChatDto
import com.ruimendes.chat.database.entities.ChatEntity
import com.ruimendes.chat.database.entities.ChatInfoEntity
import com.ruimendes.chat.database.entities.ChatWithParticipants
import com.ruimendes.chat.database.entities.MessageWithSender
import com.ruimendes.chat.domain.models.Chat
import com.ruimendes.chat.domain.models.ChatInfo
import com.ruimendes.chat.domain.models.ChatMessage
import com.ruimendes.chat.domain.models.ChatMessageDeliveryStatus
import com.ruimendes.chat.domain.models.ChatParticipant
import kotlin.time.Instant


typealias DataMessageWithSender = MessageWithSender
typealias DomainMessageWithSender = com.ruimendes.chat.domain.models.MessageWithSender
fun ChatDto.toDomain(): Chat {
    return Chat(
        id = id,
        participants = participants.map { it.toDomain() },
        lastActivityAt = Instant.parse(lastActivityAt),
        lastMessage = lastMessage?.toDomain()
    )
}

fun ChatEntity.toDomain(
    participants: List<ChatParticipant>,
    lastMessage: ChatMessage? = null
): Chat {
    return Chat(
        id = chatId,
        participants = participants,
        lastActivityAt = Instant.fromEpochMilliseconds(lastActivityAt),
        lastMessage = lastMessage
    )
}

fun ChatWithParticipants.toDomain(): Chat {
    return Chat(
        id = chat.chatId,
        participants = participants.map { it.toDomain() },
        lastActivityAt = Instant.fromEpochMilliseconds(chat.lastActivityAt),
        lastMessage = lastMessage?.toDomain()
    )
}

fun Chat.toEntity(): ChatEntity {
    return ChatEntity(
        chatId = id,
        lastActivityAt = lastActivityAt.toEpochMilliseconds()
    )
}

fun DataMessageWithSender.toDomain(): DomainMessageWithSender {
    return DomainMessageWithSender(
        message = message.toDomain(),
        sender = sender.toDomain(),
        deliveryStatus = ChatMessageDeliveryStatus.valueOf(this.message.deliveryStatus)
    )
}

fun ChatInfoEntity.toDomain(): ChatInfo {
    return ChatInfo(
        chat = chat.toDomain(
            participants = this.participants.map { it.toDomain() },
        ),
        messages = messagesWithSenders.map { it.toDomain() }
    )
}