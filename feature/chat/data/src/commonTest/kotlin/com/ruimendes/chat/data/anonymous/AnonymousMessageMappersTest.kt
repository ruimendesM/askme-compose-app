package com.ruimendes.chat.data.anonymous

import com.ruimendes.chat.database.entities.AnonymousMessageEntity
import com.ruimendes.chat.domain.anonymous.AnonymousMessage
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.time.Instant

class AnonymousMessageMappersTest {

    @Test
    fun `dto toDomain parses ISO string to Instant and maps all fields`() {
        val dto = AnonymousMessageDto(
            id = "msg-1",
            senderEmail = "sender@example.com",
            content = "Hello there",
            createdAt = "2024-01-15T10:30:00Z"
        )

        val domain = dto.toDomain()

        assertEquals("msg-1", domain.id)
        assertEquals("sender@example.com", domain.senderEmail)
        assertEquals("Hello there", domain.content)
        assertEquals(Instant.parse("2024-01-15T10:30:00Z"), domain.createdAt)
    }

    @Test
    fun `entity toDomain converts epoch millis to Instant and maps all fields`() {
        val epochMillis = 1705314600000L
        val entity = AnonymousMessageEntity(
            id = "msg-2",
            senderEmail = "another@example.com",
            content = "Test message",
            createdAt = epochMillis
        )

        val domain = entity.toDomain()

        assertEquals("msg-2", domain.id)
        assertEquals("another@example.com", domain.senderEmail)
        assertEquals("Test message", domain.content)
        assertEquals(Instant.fromEpochMilliseconds(epochMillis), domain.createdAt)
    }

    @Test
    fun `domain toEntity converts Instant to epoch millis and maps all fields`() {
        val instant = Instant.parse("2024-01-15T10:30:00Z")
        val domain = AnonymousMessage(
            id = "msg-3",
            senderEmail = "user@example.com",
            content = "Domain message",
            createdAt = instant
        )

        val entity = domain.toEntity()

        assertEquals("msg-3", entity.id)
        assertEquals("user@example.com", entity.senderEmail)
        assertEquals("Domain message", entity.content)
        assertEquals(instant.toEpochMilliseconds(), entity.createdAt)
    }
}
