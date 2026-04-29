# Anonymous Messages Inbox Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Add a read-only "ADMIN" inbox conversation showing anonymous messages, visible only to admin users.

**Architecture:** Separate domain (dedicated models, repository, Room table) with shared UI components (message bubbles, date separators). JWT role decoded client-side for admin detection. Offline-first with WebSocket real-time updates.

**Tech Stack:** Kotlin Multiplatform, Compose Multiplatform, Room, Ktor, Koin, Kotlinx Serialization, JUnit5, Turbine, AssertK

**Spec:** `docs/superpowers/specs/2026-04-01-anonymous-inbox-design.md`

**Skills:** @android-data-layer, @android-testing, @android-di-koin, @android-presentation-mvi, @android-compose-ui

---

## File Map

### New files

| File | Purpose |
|------|---------|
| `core/data/src/commonMain/.../auth/JwtDecoder.kt` | Decode JWT payload and extract role claim |
| `core/data/src/commonTest/.../auth/JwtDecoderTest.kt` | Tests for JWT decoding |
| `feature/chat/database/src/commonMain/.../entities/AnonymousMessageEntity.kt` | Room entity |
| `feature/chat/database/src/commonMain/.../dao/AnonymousMessageDao.kt` | Room DAO |
| `feature/chat/domain/src/commonMain/.../anonymous/AnonymousMessage.kt` | Domain model |
| `feature/chat/domain/src/commonMain/.../anonymous/AnonymousMessageRepository.kt` | Repository interface |
| `feature/chat/data/src/commonMain/.../anonymous/AnonymousMessageDto.kt` | Network DTO |
| `feature/chat/data/src/commonMain/.../anonymous/AnonymousMessageService.kt` | Service interface |
| `feature/chat/data/src/commonMain/.../anonymous/KtorAnonymousMessageService.kt` | Ktor implementation |
| `feature/chat/data/src/commonMain/.../anonymous/AnonymousMessageMappers.kt` | DTO/Entity/Domain mappers |
| `feature/chat/data/src/commonMain/.../anonymous/OfflineFirstAnonymousMessageRepository.kt` | Repository impl |
| `feature/chat/data/src/commonTest/.../anonymous/AnonymousMessageMappersTest.kt` | Mapper tests |
| `feature/chat/presentation/src/commonMain/.../anonymous_inbox/AnonymousInboxState.kt` | MVI State |
| `feature/chat/presentation/src/commonMain/.../anonymous_inbox/AnonymousInboxAction.kt` | MVI Action |
| `feature/chat/presentation/src/commonMain/.../anonymous_inbox/AnonymousInboxEvent.kt` | MVI Event |
| `feature/chat/presentation/src/commonMain/.../anonymous_inbox/AnonymousInboxViewModel.kt` | ViewModel |
| `feature/chat/presentation/src/commonMain/.../anonymous_inbox/AnonymousInboxScreen.kt` | Screen composable |
| `feature/chat/presentation/src/commonMain/.../mappers/AnonymousMessageUiMappers.kt` | Domain→MessageUI mappers |
| `feature/chat/presentation/src/commonTest/.../anonymous_inbox/FakeAnonymousMessageRepository.kt` | Test fake |
| `feature/chat/presentation/src/commonTest/.../anonymous_inbox/AnonymousInboxViewModelTest.kt` | ViewModel tests |
| `feature/chat/presentation/src/commonTest/.../chat_list/ChatListViewModelTest.kt` | Chat list tests |

### Modified files

| File | Change |
|------|--------|
| `core/domain/.../auth/User.kt` | Add `role: String?` field |
| `core/domain/.../auth/AuthInfo.kt` | Add `isAdmin` computed property |
| `core/data/.../dto/UserSerializable.kt` | Add `role: String?` field |
| `core/data/.../mappers/AuthInfoMapper.kt` | Map role via JWT decoder |
| `feature/chat/database/.../AppChatDatabase.kt` | Register new entity, bump version to 2 |
| `feature/chat/data/.../dto/websocket/IncomingWebSocketDto.kt` | Add `NewAnonymousMessageDto` + enum value |
| `feature/chat/data/.../chat/WebSocketChatConnectionClient.kt` | Handle `NEW_ANONYMOUS_MESSAGE` |
| `feature/chat/data/.../di/ChatDataModule.kt` | Register anonymous message DI bindings |
| `feature/chat/presentation/.../model/ChatUI.kt` | Add `isAdminInbox: Boolean` |
| `feature/chat/presentation/.../chat_list/ChatListViewModel.kt` | Inject repo, prepend ADMIN item |
| `feature/chat/presentation/.../chat_list_detail/ChatListDetailAdaptiveLayout.kt` | Route to inbox detail |
| `feature/chat/presentation/.../chat_list_detail/ChatListDetailState.kt` | Add `isAdminInbox` to state |
| `feature/chat/presentation/.../chat_list_detail/ChatListDetailViewModel.kt` | Set `isAdminInbox` on chat selection |
| `feature/chat/presentation/.../di/ChatPresentationModule.kt` | Register AnonymousInboxViewModel |

---

## Task 1: JWT Role Decoding

**Files:**
- Create: `core/data/src/commonMain/kotlin/com/ruimendes/core/data/auth/JwtDecoder.kt`
- Create: `core/data/src/commonTest/kotlin/com/ruimendes/core/data/auth/JwtDecoderTest.kt`
- Modify: `core/domain/src/commonMain/kotlin/com/ruimendes/core/domain/auth/User.kt`
- Modify: `core/domain/src/commonMain/kotlin/com/ruimendes/core/domain/auth/AuthInfo.kt`
- Modify: `core/data/src/commonMain/kotlin/com/ruimendes/core/data/dto/UserSerializable.kt`
- Modify: `core/data/src/commonMain/kotlin/com/ruimendes/core/data/mappers/AuthInfoMapper.kt`

- [ ] **Step 1: Write failing tests for JWT decoder**

Create `core/data/src/commonTest/kotlin/com/ruimendes/core/data/auth/JwtDecoderTest.kt`:

```kotlin
package com.ruimendes.core.data.auth

import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isNull
import kotlin.test.Test

class JwtDecoderTest {

    @Test
    fun `decodeJwtRole returns role from valid token`() {
        // JWT with payload: {"sub":"user1","role":"ADMIN"}
        // Header: {"alg":"HS256","typ":"JWT"}
        val token = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9." +
            "eyJzdWIiOiJ1c2VyMSIsInJvbGUiOiJBRE1JTiJ9." +
            "signature"
        assertThat(decodeJwtRole(token)).isEqualTo("ADMIN")
    }

    @Test
    fun `decodeJwtRole returns USER role`() {
        // JWT with payload: {"sub":"user2","role":"USER"}
        val token = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9." +
            "eyJzdWIiOiJ1c2VyMiIsInJvbGUiOiJVU0VSIn0." +
            "signature"
        assertThat(decodeJwtRole(token)).isEqualTo("USER")
    }

    @Test
    fun `decodeJwtRole returns null when no role claim`() {
        // JWT with payload: {"sub":"user3"}
        val token = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9." +
            "eyJzdWIiOiJ1c2VyMyJ9." +
            "signature"
        assertThat(decodeJwtRole(token)).isNull()
    }

    @Test
    fun `decodeJwtRole returns null for malformed token`() {
        assertThat(decodeJwtRole("not-a-jwt")).isNull()
    }

    @Test
    fun `decodeJwtRole returns null for empty string`() {
        assertThat(decodeJwtRole("")).isNull()
    }
}
```

- [ ] **Step 2: Run tests to verify they fail**

Run: `./gradlew :core:data:testDebugUnitTest --tests "com.ruimendes.core.data.auth.JwtDecoderTest" --info`
Expected: FAIL — `decodeJwtRole` does not exist yet.

- [ ] **Step 3: Implement JWT decoder**

Create `core/data/src/commonMain/kotlin/com/ruimendes/core/data/auth/JwtDecoder.kt`:

```kotlin
package com.ruimendes.core.data.auth

import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

@OptIn(ExperimentalEncodingApi::class)
fun decodeJwtRole(token: String): String? {
    return try {
        val parts = token.split(".")
        if (parts.size != 3) return null

        val payload = parts[1]
            .replace('-', '+')
            .replace('_', '/')
            .let { padded ->
                val mod = padded.length % 4
                if (mod > 0) padded + "=".repeat(4 - mod) else padded
            }

        val decoded = Base64.decode(payload).decodeToString()
        val json = Json.parseToJsonElement(decoded).jsonObject
        json["role"]?.jsonPrimitive?.content
    } catch (_: Exception) {
        null
    }
}
```

- [ ] **Step 4: Run tests to verify they pass**

Run: `./gradlew :core:data:testDebugUnitTest --tests "com.ruimendes.core.data.auth.JwtDecoderTest" --info`
Expected: ALL PASS

- [ ] **Step 5: Add role to domain models**

Modify `core/domain/src/commonMain/kotlin/com/ruimendes/core/domain/auth/User.kt`:

```kotlin
data class User(
    val id: String,
    val email: String,
    val username: String,
    val hasEmailVerified: Boolean,
    val profilePictureUrl: String? = null,
    val role: String? = null
)
```

Modify `core/domain/src/commonMain/kotlin/com/ruimendes/core/domain/auth/AuthInfo.kt`:

```kotlin
data class AuthInfo(
    val accessToken: String,
    val refreshToken: String,
    val user: User,
) {
    val isAdmin: Boolean get() = user.role == "ADMIN"
}
```

Modify `core/data/src/commonMain/kotlin/com/ruimendes/core/data/dto/UserSerializable.kt`:

```kotlin
@Serializable
data class UserSerializable(
    val id: String,
    val email: String,
    val username: String,
    val hasEmailVerified: Boolean,
    val profilePictureUrl: String? = null,
    val role: String? = null
)
```

- [ ] **Step 6: Update mappers to use JWT decoder**

Modify `core/data/src/commonMain/kotlin/com/ruimendes/core/data/mappers/AuthInfoMapper.kt`:

```kotlin
package com.ruimendes.core.data.mappers

import com.ruimendes.core.data.auth.decodeJwtRole
import com.ruimendes.core.data.dto.AuthInfoSerializable
import com.ruimendes.core.data.dto.UserSerializable
import com.ruimendes.core.domain.auth.AuthInfo
import com.ruimendes.core.domain.auth.User

fun AuthInfoSerializable.toDomain(): AuthInfo {
    val role = decodeJwtRole(accessToken)
    return AuthInfo(
        accessToken = accessToken,
        refreshToken = refreshToken,
        user = user.toDomain(role = role)
    )
}

fun UserSerializable.toDomain(role: String? = null): User {
    return User(
        id = id,
        email = email,
        username = username,
        hasEmailVerified = hasEmailVerified,
        profilePictureUrl = profilePictureUrl,
        role = role ?: this.role
    )
}

fun AuthInfo.toSerializable(): AuthInfoSerializable {
    return AuthInfoSerializable(
        accessToken = accessToken,
        refreshToken = refreshToken,
        user = user.toSerializable()
    )
}

fun User.toSerializable(): UserSerializable {
    return UserSerializable(
        id = id,
        email = email,
        username = username,
        hasEmailVerified = hasEmailVerified,
        profilePictureUrl = profilePictureUrl,
        role = role
    )
}
```

- [ ] **Step 7: Verify project compiles**

Run: `./gradlew :core:data:compileKotlinAndroid :core:domain:compileKotlinAndroid`
Expected: BUILD SUCCESSFUL

- [ ] **Step 8: Commit**

```bash
git add core/domain/src/commonMain/kotlin/com/ruimendes/core/domain/auth/User.kt \
       core/domain/src/commonMain/kotlin/com/ruimendes/core/domain/auth/AuthInfo.kt \
       core/data/src/commonMain/kotlin/com/ruimendes/core/data/auth/JwtDecoder.kt \
       core/data/src/commonMain/kotlin/com/ruimendes/core/data/dto/UserSerializable.kt \
       core/data/src/commonMain/kotlin/com/ruimendes/core/data/mappers/AuthInfoMapper.kt \
       core/data/src/commonTest/kotlin/com/ruimendes/core/data/auth/JwtDecoderTest.kt
git commit -m "feat: decode JWT role claim for admin detection"
```

---

## Task 2: Anonymous Message Domain Model & Repository Interface

**Files:**
- Create: `feature/chat/domain/src/commonMain/kotlin/com/ruimendes/chat/domain/anonymous/AnonymousMessage.kt`
- Create: `feature/chat/domain/src/commonMain/kotlin/com/ruimendes/chat/domain/anonymous/AnonymousMessageRepository.kt`

- [ ] **Step 1: Create domain model**

Create `feature/chat/domain/src/commonMain/kotlin/com/ruimendes/chat/domain/anonymous/AnonymousMessage.kt`:

```kotlin
package com.ruimendes.chat.domain.anonymous

import kotlinx.datetime.Instant

data class AnonymousMessage(
    val id: String,
    val senderEmail: String,
    val content: String,
    val createdAt: Instant
)
```

- [ ] **Step 2: Create repository interface**

Create `feature/chat/domain/src/commonMain/kotlin/com/ruimendes/chat/domain/anonymous/AnonymousMessageRepository.kt`:

```kotlin
package com.ruimendes.chat.domain.anonymous

import com.ruimendes.core.domain.util.DataError
import com.ruimendes.core.domain.util.EmptyResult
import com.ruimendes.core.domain.util.Result
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.Instant

interface AnonymousMessageRepository {
    fun getMessages(): Flow<List<AnonymousMessage>>
    fun getLatestMessage(): Flow<AnonymousMessage?>
    suspend fun fetchMessages(before: Instant?, pageSize: Int): Result<List<AnonymousMessage>, DataError>
    suspend fun saveMessage(message: AnonymousMessage): EmptyResult<DataError.Local>
}
```

- [ ] **Step 3: Verify domain compiles**

Run: `./gradlew :feature:chat:domain:compileKotlinAndroid`
Expected: BUILD SUCCESSFUL

- [ ] **Step 4: Commit**

```bash
git add feature/chat/domain/src/commonMain/kotlin/com/ruimendes/chat/domain/anonymous/
git commit -m "feat: add anonymous message domain model and repository interface"
```

---

## Task 3: Room Entity & DAO

**Files:**
- Create: `feature/chat/database/src/commonMain/kotlin/com/ruimendes/chat/database/entities/AnonymousMessageEntity.kt`
- Create: `feature/chat/database/src/commonMain/kotlin/com/ruimendes/chat/database/dao/AnonymousMessageDao.kt`
- Modify: `feature/chat/database/src/commonMain/kotlin/com/ruimendes/chat/database/AppChatDatabase.kt`

- [ ] **Step 1: Create Room entity**

Create `feature/chat/database/src/commonMain/kotlin/com/ruimendes/chat/database/entities/AnonymousMessageEntity.kt`:

```kotlin
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
```

- [ ] **Step 2: Create DAO**

Create `feature/chat/database/src/commonMain/kotlin/com/ruimendes/chat/database/dao/AnonymousMessageDao.kt`:

```kotlin
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
```

- [ ] **Step 3: Register entity in database and bump version**

Modify `feature/chat/database/src/commonMain/kotlin/com/ruimendes/chat/database/AppChatDatabase.kt`. Add `AnonymousMessageEntity::class` to the entities array, bump version to 2, add the DAO accessor, and add a destructive migration fallback (since we don't have production users yet):

```kotlin
package com.ruimendes.chat.database

import androidx.room.ConstructedBy
import androidx.room.Database
import androidx.room.RoomDatabase
import com.ruimendes.chat.database.dao.AnonymousMessageDao
import com.ruimendes.chat.database.dao.ChatDao
import com.ruimendes.chat.database.dao.ChatMessageDao
import com.ruimendes.chat.database.dao.ChatParticipantDao
import com.ruimendes.chat.database.dao.ChatParticipantsCrossRefDao
import com.ruimendes.chat.database.entities.AnonymousMessageEntity
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
        ChatParticipantCrossRef::class,
        AnonymousMessageEntity::class
    ],
    views = [
        LastMessageView::class
    ],
    version = 2
)
@ConstructedBy(AppChatDatabaseConstructor::class)
abstract class AppChatDatabase: RoomDatabase() {
    abstract val chatDao: ChatDao
    abstract val chatParticipantDao: ChatParticipantDao
    abstract val chatMessageDao: ChatMessageDao
    abstract val chatParticipantsCrossRefDao: ChatParticipantsCrossRefDao
    abstract val anonymousMessageDao: AnonymousMessageDao

    companion object {
        const val DATABASE_NAME = "askme_chat.db"
    }
}
```

Note: Add `.fallbackToDestructiveMigration(true)` to the database builder in `ChatDataModule.kt` (will be done in Task 7 when updating DI).

- [ ] **Step 4: Verify database module compiles**

Run: `./gradlew :feature:chat:database:compileKotlinAndroid`
Expected: BUILD SUCCESSFUL

- [ ] **Step 5: Commit**

```bash
git add feature/chat/database/src/commonMain/kotlin/com/ruimendes/chat/database/entities/AnonymousMessageEntity.kt \
       feature/chat/database/src/commonMain/kotlin/com/ruimendes/chat/database/dao/AnonymousMessageDao.kt \
       feature/chat/database/src/commonMain/kotlin/com/ruimendes/chat/database/AppChatDatabase.kt
git commit -m "feat: add anonymous message Room entity and DAO"
```

---

## Task 4: Network DTO, Service & Data Mappers

**Files:**
- Create: `feature/chat/data/src/commonMain/kotlin/com/ruimendes/chat/data/anonymous/AnonymousMessageDto.kt`
- Create: `feature/chat/data/src/commonMain/kotlin/com/ruimendes/chat/data/anonymous/AnonymousMessageService.kt`
- Create: `feature/chat/data/src/commonMain/kotlin/com/ruimendes/chat/data/anonymous/KtorAnonymousMessageService.kt`
- Create: `feature/chat/data/src/commonMain/kotlin/com/ruimendes/chat/data/anonymous/AnonymousMessageMappers.kt`
- Create: `feature/chat/data/src/commonTest/kotlin/com/ruimendes/chat/data/anonymous/AnonymousMessageMappersTest.kt`

- [ ] **Step 1: Write failing mapper tests**

Create `feature/chat/data/src/commonTest/kotlin/com/ruimendes/chat/data/anonymous/AnonymousMessageMappersTest.kt`:

```kotlin
package com.ruimendes.chat.data.anonymous

import assertk.assertThat
import assertk.assertions.isEqualTo
import com.ruimendes.chat.database.entities.AnonymousMessageEntity
import com.ruimendes.chat.domain.anonymous.AnonymousMessage
import kotlinx.datetime.Instant
import kotlin.test.Test

class AnonymousMessageMappersTest {

    @Test
    fun `dto maps to domain correctly`() {
        val dto = AnonymousMessageDto(
            id = "msg-1",
            senderEmail = "john@example.com",
            content = "Hello",
            createdAt = "2026-03-25T10:30:00Z"
        )

        val result = dto.toDomain()

        assertThat(result.id).isEqualTo("msg-1")
        assertThat(result.senderEmail).isEqualTo("john@example.com")
        assertThat(result.content).isEqualTo("Hello")
        assertThat(result.createdAt).isEqualTo(Instant.parse("2026-03-25T10:30:00Z"))
    }

    @Test
    fun `entity maps to domain correctly`() {
        val entity = AnonymousMessageEntity(
            id = "msg-1",
            senderEmail = "john@example.com",
            content = "Hello",
            createdAt = 1711358400000L
        )

        val result = entity.toDomain()

        assertThat(result.id).isEqualTo("msg-1")
        assertThat(result.senderEmail).isEqualTo("john@example.com")
        assertThat(result.content).isEqualTo("Hello")
        assertThat(result.createdAt).isEqualTo(Instant.fromEpochMilliseconds(1711358400000L))
    }

    @Test
    fun `domain maps to entity correctly`() {
        val domain = AnonymousMessage(
            id = "msg-1",
            senderEmail = "john@example.com",
            content = "Hello",
            createdAt = Instant.fromEpochMilliseconds(1711358400000L)
        )

        val result = domain.toEntity()

        assertThat(result.id).isEqualTo("msg-1")
        assertThat(result.senderEmail).isEqualTo("john@example.com")
        assertThat(result.content).isEqualTo("Hello")
        assertThat(result.createdAt).isEqualTo(1711358400000L)
    }
}
```

- [ ] **Step 2: Run tests to verify they fail**

Run: `./gradlew :feature:chat:data:testDebugUnitTest --tests "com.ruimendes.chat.data.anonymous.AnonymousMessageMappersTest" --info`
Expected: FAIL — classes don't exist yet.

- [ ] **Step 3: Create DTO**

Create `feature/chat/data/src/commonMain/kotlin/com/ruimendes/chat/data/anonymous/AnonymousMessageDto.kt`:

```kotlin
package com.ruimendes.chat.data.anonymous

import kotlinx.serialization.Serializable

@Serializable
data class AnonymousMessageDto(
    val id: String,
    val senderEmail: String,
    val content: String,
    val createdAt: String
)
```

- [ ] **Step 4: Create mappers**

Create `feature/chat/data/src/commonMain/kotlin/com/ruimendes/chat/data/anonymous/AnonymousMessageMappers.kt`:

```kotlin
package com.ruimendes.chat.data.anonymous

import com.ruimendes.chat.database.entities.AnonymousMessageEntity
import com.ruimendes.chat.domain.anonymous.AnonymousMessage
import kotlinx.datetime.Instant

fun AnonymousMessageDto.toDomain(): AnonymousMessage {
    return AnonymousMessage(
        id = id,
        senderEmail = senderEmail,
        content = content,
        createdAt = Instant.parse(createdAt)
    )
}

fun AnonymousMessageEntity.toDomain(): AnonymousMessage {
    return AnonymousMessage(
        id = id,
        senderEmail = senderEmail,
        content = content,
        createdAt = Instant.fromEpochMilliseconds(createdAt)
    )
}

fun AnonymousMessage.toEntity(): AnonymousMessageEntity {
    return AnonymousMessageEntity(
        id = id,
        senderEmail = senderEmail,
        content = content,
        createdAt = createdAt.toEpochMilliseconds()
    )
}
```

- [ ] **Step 5: Run mapper tests to verify they pass**

Run: `./gradlew :feature:chat:data:testDebugUnitTest --tests "com.ruimendes.chat.data.anonymous.AnonymousMessageMappersTest" --info`
Expected: ALL PASS

- [ ] **Step 6: Create service interface and Ktor implementation**

Create `feature/chat/data/src/commonMain/kotlin/com/ruimendes/chat/data/anonymous/AnonymousMessageService.kt`:

```kotlin
package com.ruimendes.chat.data.anonymous

import com.ruimendes.chat.domain.anonymous.AnonymousMessage
import com.ruimendes.core.domain.util.DataError
import com.ruimendes.core.domain.util.Result
import kotlinx.datetime.Instant

interface AnonymousMessageService {
    suspend fun fetchMessages(
        before: Instant?,
        pageSize: Int
    ): Result<List<AnonymousMessage>, DataError.Remote>
}
```

Create `feature/chat/data/src/commonMain/kotlin/com/ruimendes/chat/data/anonymous/KtorAnonymousMessageService.kt`:

```kotlin
package com.ruimendes.chat.data.anonymous

import com.ruimendes.chat.domain.anonymous.AnonymousMessage
import com.ruimendes.core.data.networking.get
import com.ruimendes.core.domain.util.DataError
import com.ruimendes.core.domain.util.Result
import com.ruimendes.core.domain.util.map
import io.ktor.client.HttpClient
import kotlinx.datetime.Instant

class KtorAnonymousMessageService(
    private val httpClient: HttpClient
) : AnonymousMessageService {

    override suspend fun fetchMessages(
        before: Instant?,
        pageSize: Int
    ): Result<List<AnonymousMessage>, DataError.Remote> {
        return httpClient.get<List<AnonymousMessageDto>>(
            route = "/api/anonymous-messages",
            queryParams = buildMap {
                this["pageSize"] = pageSize
                if (before != null) {
                    this["before"] = before.toString()
                }
            }
        ).map { dtos ->
            dtos.map { it.toDomain() }
        }
    }

    companion object {
        const val PAGE_SIZE = 20
    }
}
```

- [ ] **Step 7: Verify data module compiles**

Run: `./gradlew :feature:chat:data:compileKotlinAndroid`
Expected: BUILD SUCCESSFUL

- [ ] **Step 8: Commit**

```bash
git add feature/chat/data/src/commonMain/kotlin/com/ruimendes/chat/data/anonymous/ \
       feature/chat/data/src/commonTest/kotlin/com/ruimendes/chat/data/anonymous/
git commit -m "feat: add anonymous message DTO, service, and mappers with tests"
```

---

## Task 5: Offline-First Repository

**Files:**
- Create: `feature/chat/data/src/commonMain/kotlin/com/ruimendes/chat/data/anonymous/OfflineFirstAnonymousMessageRepository.kt`

- [ ] **Step 1: Implement repository**

Create `feature/chat/data/src/commonMain/kotlin/com/ruimendes/chat/data/anonymous/OfflineFirstAnonymousMessageRepository.kt`:

```kotlin
package com.ruimendes.chat.data.anonymous

import com.ruimendes.chat.database.AppChatDatabase
import com.ruimendes.chat.domain.anonymous.AnonymousMessage
import com.ruimendes.chat.domain.anonymous.AnonymousMessageRepository
import com.ruimendes.core.domain.util.DataError
import com.ruimendes.core.domain.util.EmptyResult
import com.ruimendes.core.domain.util.Result
import com.ruimendes.core.domain.util.map
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.datetime.Instant

class OfflineFirstAnonymousMessageRepository(
    private val database: AppChatDatabase,
    private val service: AnonymousMessageService
) : AnonymousMessageRepository {

    private val dao get() = database.anonymousMessageDao

    override fun getMessages(): Flow<List<AnonymousMessage>> {
        return dao.getMessages().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun getLatestMessage(): Flow<AnonymousMessage?> {
        return dao.getLatestMessage().map { it?.toDomain() }
    }

    override suspend fun fetchMessages(
        before: Instant?,
        pageSize: Int
    ): Result<List<AnonymousMessage>, DataError> {
        return service.fetchMessages(before, pageSize)
            .map { messages ->
                dao.upsertMessages(messages.map { it.toEntity() })
                messages
            }
    }

    override suspend fun saveMessage(message: AnonymousMessage): EmptyResult<DataError.Local> {
        return try {
            dao.upsertMessage(message.toEntity())
            Result.Success(Unit)
        } catch (_: Exception) {
            Result.Failure(DataError.Local.UNKNOWN)
        }
    }
}
```

- [ ] **Step 2: Verify data module compiles**

Run: `./gradlew :feature:chat:data:compileKotlinAndroid`
Expected: BUILD SUCCESSFUL

- [ ] **Step 3: Commit**

```bash
git add feature/chat/data/src/commonMain/kotlin/com/ruimendes/chat/data/anonymous/OfflineFirstAnonymousMessageRepository.kt
git commit -m "feat: add offline-first anonymous message repository"
```

---

## Task 6: WebSocket Integration

**Files:**
- Modify: `feature/chat/data/src/commonMain/kotlin/com/ruimendes/chat/data/dto/websocket/IncomingWebSocketDto.kt`
- Modify: `feature/chat/data/src/commonMain/kotlin/com/ruimendes/chat/data/chat/WebSocketChatConnectionClient.kt`

- [ ] **Step 1: Add new WebSocket message type**

Modify `feature/chat/data/src/commonMain/kotlin/com/ruimendes/chat/data/dto/websocket/IncomingWebSocketDto.kt` — add `NEW_ANONYMOUS_MESSAGE` to the enum and a new DTO class:

Add to the `IncomingWebSocketType` enum:

```kotlin
enum class IncomingWebSocketType {
    NEW_MESSAGE,
    MESSAGE_DELETED,
    PROFILE_PICTURE_UPDATED,
    CHAT_PARTICIPANTS_CHANGED,
    NEW_ANONYMOUS_MESSAGE
}
```

Add inside the `IncomingWebSocketDto` sealed interface:

```kotlin
@Serializable
data class NewAnonymousMessageDto(
    val id: String,
    val senderEmail: String,
    val content: String,
    val createdAt: String,
    private val type: IncomingWebSocketType = IncomingWebSocketType.NEW_ANONYMOUS_MESSAGE
) : IncomingWebSocketDto
```

- [ ] **Step 2: Handle event in WebSocketChatConnectionClient**

Modify `feature/chat/data/src/commonMain/kotlin/com/ruimendes/chat/data/chat/WebSocketChatConnectionClient.kt`.

Add `AnonymousMessageRepository` to the constructor:

```kotlin
import com.ruimendes.chat.data.anonymous.toDomain
import com.ruimendes.chat.domain.anonymous.AnonymousMessageRepository

class WebSocketChatConnectionClient(
    private val webSocketConnector: KtorWebSocketConnector,
    private val chatRepository: ChatRepository,
    private val database: AppChatDatabase,
    private val sessionStorage: SessionStorage,
    private val anonymousMessageRepository: AnonymousMessageRepository,
    private val json: Json,
    private val applicationScope: CoroutineScope
) : ChatConnectionClient {
```

Add the new case to `parseIncomingMessage`:

```kotlin
IncomingWebSocketType.NEW_ANONYMOUS_MESSAGE.name -> {
    json.decodeFromString<IncomingWebSocketDto.NewAnonymousMessageDto>(message.payload)
}
```

Add the new case to `handleIncomingMessage`:

```kotlin
is IncomingWebSocketDto.NewAnonymousMessageDto -> handleNewAnonymousMessage(message)
```

Add the handler method:

```kotlin
private suspend fun handleNewAnonymousMessage(message: IncomingWebSocketDto.NewAnonymousMessageDto) {
    val authInfo = sessionStorage.observeAuthInfo().firstOrNull()
    if (authInfo?.isAdmin != true) return

    val domainMessage = com.ruimendes.chat.data.anonymous.AnonymousMessageDto(
        id = message.id,
        senderEmail = message.senderEmail,
        content = message.content,
        createdAt = message.createdAt
    ).toDomain()

    anonymousMessageRepository.saveMessage(domainMessage)
}
```

- [ ] **Step 3: Verify data module compiles**

Run: `./gradlew :feature:chat:data:compileKotlinAndroid`
Expected: BUILD SUCCESSFUL

- [ ] **Step 4: Commit**

```bash
git add feature/chat/data/src/commonMain/kotlin/com/ruimendes/chat/data/dto/websocket/IncomingWebSocketDto.kt \
       feature/chat/data/src/commonMain/kotlin/com/ruimendes/chat/data/chat/WebSocketChatConnectionClient.kt
git commit -m "feat: handle NEW_ANONYMOUS_MESSAGE WebSocket event"
```

---

## Task 7: Koin DI Wiring

**Files:**
- Modify: `feature/chat/data/src/commonMain/kotlin/com/ruimendes/chat/data/di/ChatDataModule.kt`
- Modify: `feature/chat/presentation/src/commonMain/kotlin/com/ruimendes/chat/presentation/di/ChatPresentationModule.kt`

- [ ] **Step 1: Register data layer DI bindings**

Modify `feature/chat/data/src/commonMain/kotlin/com/ruimendes/chat/data/di/ChatDataModule.kt`. Add imports and registrations:

```kotlin
import com.ruimendes.chat.data.anonymous.AnonymousMessageService
import com.ruimendes.chat.data.anonymous.KtorAnonymousMessageService
import com.ruimendes.chat.data.anonymous.OfflineFirstAnonymousMessageRepository
import com.ruimendes.chat.domain.anonymous.AnonymousMessageRepository
```

Add these lines inside the module block:

```kotlin
singleOf(::KtorAnonymousMessageService) bind AnonymousMessageService::class
singleOf(::OfflineFirstAnonymousMessageRepository) bind AnonymousMessageRepository::class
```

Also update the database builder to add destructive migration fallback:

```kotlin
single {
    get<DatabaseFactory>()
        .create()
        .fallbackToDestructiveMigration(true)
        .setDriver(BundledSQLiteDriver())
        .build()
}
```

- [ ] **Step 2: Verify data module compiles**

Run: `./gradlew :feature:chat:data:compileKotlinAndroid`
Expected: BUILD SUCCESSFUL

- [ ] **Step 3: Commit**

```bash
git add feature/chat/data/src/commonMain/kotlin/com/ruimendes/chat/data/di/ChatDataModule.kt
git commit -m "feat: register anonymous message DI bindings"
```

---

## Task 8: ChatUI Model & Chat List ViewModel Changes

**Files:**
- Modify: `feature/chat/presentation/src/commonMain/kotlin/com/ruimendes/chat/presentation/model/ChatUI.kt`
- Modify: `feature/chat/presentation/src/commonMain/kotlin/com/ruimendes/chat/presentation/chat_list/ChatListViewModel.kt`
- Create: `feature/chat/presentation/src/commonTest/kotlin/com/ruimendes/chat/presentation/chat_list/ChatListViewModelTest.kt`
- Create: `feature/chat/presentation/src/commonTest/kotlin/com/ruimendes/chat/presentation/anonymous_inbox/FakeAnonymousMessageRepository.kt`

- [ ] **Step 1: Add isAdminInbox to ChatUI**

Modify `feature/chat/presentation/src/commonMain/kotlin/com/ruimendes/chat/presentation/model/ChatUI.kt`:

```kotlin
data class ChatUI(
    val id: String,
    val localParticipant: ChatParticipantUI,
    val otherParticipants: List<ChatParticipantUI>,
    val lastMessage: ChatMessage?,
    val lastMessageSenderUsername: String?,
    val isAdminInbox: Boolean = false
)
```

- [ ] **Step 2: Create FakeAnonymousMessageRepository**

Create `feature/chat/presentation/src/commonTest/kotlin/com/ruimendes/chat/presentation/anonymous_inbox/FakeAnonymousMessageRepository.kt`:

```kotlin
package com.ruimendes.chat.presentation.anonymous_inbox

import com.ruimendes.chat.domain.anonymous.AnonymousMessage
import com.ruimendes.chat.domain.anonymous.AnonymousMessageRepository
import com.ruimendes.core.domain.util.DataError
import com.ruimendes.core.domain.util.EmptyResult
import com.ruimendes.core.domain.util.Result
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.datetime.Instant

class FakeAnonymousMessageRepository : AnonymousMessageRepository {

    private val messages = MutableStateFlow<List<AnonymousMessage>>(emptyList())
    var fetchResult: Result<List<AnonymousMessage>, DataError> = Result.Success(emptyList())

    override fun getMessages(): Flow<List<AnonymousMessage>> = messages

    override fun getLatestMessage(): Flow<AnonymousMessage?> {
        return messages.map { it.maxByOrNull { m -> m.createdAt } }
    }

    override suspend fun fetchMessages(
        before: Instant?,
        pageSize: Int
    ): Result<List<AnonymousMessage>, DataError> = fetchResult

    override suspend fun saveMessage(message: AnonymousMessage): EmptyResult<DataError.Local> {
        messages.value = messages.value + message
        return Result.Success(Unit)
    }

    fun emit(newMessages: List<AnonymousMessage>) {
        messages.value = newMessages
    }
}
```

- [ ] **Step 3: Write ChatListViewModel tests**

Create `feature/chat/presentation/src/commonTest/kotlin/com/ruimendes/chat/presentation/chat_list/FakeChatRepository.kt`:

```kotlin
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
    var fetchResult: Result<List<Chat>, DataError.Remote> = Result.Success(emptyList())

    override fun getChats(): Flow<List<Chat>> = chats
    override fun getChatInfoById(chatId: String): Flow<ChatInfo> = flowOf()
    override fun getActiveParticipantsByChatId(chatId: String): Flow<List<ChatParticipant>> = flowOf()
    override suspend fun fetchChats(): Result<List<Chat>, DataError.Remote> = fetchResult
    override suspend fun fetchChatById(chatId: String): EmptyResult<DataError.Remote> = Result.Success(Unit)
    override suspend fun createChat(otherUserIds: List<String>): Result<Chat, DataError.Remote> = Result.Failure(DataError.Remote.UNKNOWN)
    override suspend fun leaveChat(chatId: String): EmptyResult<DataError.Remote> = Result.Success(Unit)
    override suspend fun addParticipantsToChat(chatId: String, userIds: List<String>): Result<Chat, DataError.Remote> = Result.Failure(DataError.Remote.UNKNOWN)

    fun emit(newChats: List<Chat>) { chats.value = newChats }
}
```

Create `feature/chat/presentation/src/commonTest/kotlin/com/ruimendes/chat/presentation/chat_list/FakeSessionStorage.kt`:

```kotlin
package com.ruimendes.chat.presentation.chat_list

import com.ruimendes.core.domain.auth.AuthInfo
import com.ruimendes.core.domain.auth.SessionStorage
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

class FakeSessionStorage : SessionStorage {

    private val authInfo = MutableStateFlow<AuthInfo?>(null)

    override fun observeAuthInfo(): Flow<AuthInfo?> = authInfo

    override suspend fun set(info: AuthInfo?) {
        authInfo.value = info
    }
}
```

Create `feature/chat/presentation/src/commonTest/kotlin/com/ruimendes/chat/presentation/chat_list/ChatListViewModelTest.kt`:

```kotlin
package com.ruimendes.chat.presentation.chat_list

import app.cash.turbine.test
import assertk.assertThat
import assertk.assertions.hasSize
import assertk.assertions.isEmpty
import assertk.assertions.isEqualTo
import assertk.assertions.isTrue
import assertk.assertions.isFalse
import com.ruimendes.chat.domain.anonymous.AnonymousMessage
import com.ruimendes.chat.presentation.anonymous_inbox.FakeAnonymousMessageRepository
import com.ruimendes.core.domain.auth.AuthInfo
import com.ruimendes.core.domain.auth.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlinx.datetime.Instant
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ChatListViewModelTest {

    private val testDispatcher = UnconfinedTestDispatcher()
    private lateinit var fakeChatRepository: FakeChatRepository
    private lateinit var fakeSessionStorage: FakeSessionStorage
    private lateinit var fakeAnonymousRepository: FakeAnonymousMessageRepository
    private lateinit var viewModel: ChatListViewModel

    private val adminUser = User(
        id = "admin-1",
        email = "admin@test.com",
        username = "admin",
        hasEmailVerified = true,
        role = "ADMIN"
    )

    private val regularUser = User(
        id = "user-1",
        email = "user@test.com",
        username = "testuser",
        hasEmailVerified = true,
        role = "USER"
    )

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        fakeChatRepository = FakeChatRepository()
        fakeSessionStorage = FakeSessionStorage()
        fakeAnonymousRepository = FakeAnonymousMessageRepository()
        viewModel = ChatListViewModel(
            repository = fakeChatRepository,
            sessionStorage = fakeSessionStorage,
            anonymousMessageRepository = fakeAnonymousRepository
        )
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `admin user sees ADMIN inbox pinned first`() = runTest {
        fakeSessionStorage.set(AuthInfo(
            accessToken = "token",
            refreshToken = "refresh",
            user = adminUser
        ))

        viewModel.state.test {
            val state = awaitItem()
            assertThat(state.chats).hasSize(1)
            assertThat(state.chats.first().isAdminInbox).isTrue()
            assertThat(state.chats.first().id).isEqualTo(ChatListViewModel.ADMIN_INBOX_ID)
        }
    }

    @Test
    fun `non-admin user does not see ADMIN inbox`() = runTest {
        fakeSessionStorage.set(AuthInfo(
            accessToken = "token",
            refreshToken = "refresh",
            user = regularUser
        ))

        viewModel.state.test {
            val state = awaitItem()
            assertThat(state.chats).isEmpty()
        }
    }

    @Test
    fun `ADMIN item shows latest anonymous message preview`() = runTest {
        fakeSessionStorage.set(AuthInfo(
            accessToken = "token",
            refreshToken = "refresh",
            user = adminUser
        ))

        val message = AnonymousMessage(
            id = "msg-1",
            senderEmail = "john@example.com",
            content = "Hello admin",
            createdAt = Instant.parse("2026-03-25T10:30:00Z")
        )
        fakeAnonymousRepository.emit(listOf(message))

        viewModel.state.test {
            val state = awaitItem()
            assertThat(state.chats.first().lastMessage?.content).isEqualTo("Hello admin")
            assertThat(state.chats.first().lastMessageSenderUsername).isEqualTo("john@example.com")
        }
    }
}
```

- [ ] **Step 4: Update ChatListViewModel to show ADMIN inbox**

Modify `feature/chat/presentation/src/commonMain/kotlin/com/ruimendes/chat/presentation/chat_list/ChatListViewModel.kt`:

Add `AnonymousMessageRepository` to constructor and update the state flow:

```kotlin
package com.ruimendes.chat.presentation.chat_list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ruimendes.chat.domain.anonymous.AnonymousMessageRepository
import com.ruimendes.chat.domain.chat.ChatRepository
import com.ruimendes.chat.domain.models.ChatMessage
import com.ruimendes.chat.domain.models.ChatMessageDeliveryStatus
import com.ruimendes.chat.presentation.mappers.toUi
import com.ruimendes.chat.presentation.model.ChatUI
import com.ruimendes.core.designsystem.components.avatar.ChatParticipantUI
import com.ruimendes.core.domain.auth.SessionStorage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ChatListViewModel(
    private val repository: ChatRepository,
    private val sessionStorage: SessionStorage,
    private val anonymousMessageRepository: AnonymousMessageRepository
) : ViewModel() {

    private var hasLoadedInitialData = false

    private val _state = MutableStateFlow(ChatListState())
    val state = combine(
        _state,
        repository.getChats(),
        sessionStorage.observeAuthInfo(),
        anonymousMessageRepository.getLatestMessage()
    ) { currentState, chats, authInfo, latestAnonymousMessage ->
        if (authInfo == null) {
            return@combine ChatListState()
        }

        val regularChats = chats
            .map { it.toUi(authInfo.user.id) }
            .filter { it.otherParticipants.isNotEmpty() }

        val allChats = if (authInfo.isAdmin) {
            listOf(buildAdminInboxItem(authInfo.user.toUi(), latestAnonymousMessage)) + regularChats
        } else {
            regularChats
        }

        currentState.copy(
            chats = allChats,
            localParticipant = authInfo.user.toUi()
        )
    }
        .onStart {
            if (!hasLoadedInitialData) {
                loadChats()
                hasLoadedInitialData = true
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000L),
            initialValue = ChatListState()
        )

    fun onAction(action: ChatListAction) {
        when (action) {
            is ChatListAction.OnSelectChat -> {
                _state.update {
                    it.copy(
                        selectedChatId = action.chatId
                    )
                }
            }

            ChatListAction.OnConfirmLogout -> {}
            ChatListAction.OnCreateChatClick -> {}
            ChatListAction.OnDismissLogoutDialog -> {}

            ChatListAction.OnLogoutClick,
            ChatListAction.OnProfileSettingsClick,
            ChatListAction.OnDismissUserMenu -> {
                _state.update {
                    it.copy(isUserMenuOpen = false)
                }
            }

            ChatListAction.OnUserAvatarClick -> {
                _state.update {
                    it.copy(isUserMenuOpen = true)
                }
            }
        }
    }

    private fun loadChats() {
        viewModelScope.launch {
            repository.fetchChats()
        }
    }

    companion object {
        const val ADMIN_INBOX_ID = "ADMIN_INBOX"

        private fun buildAdminInboxItem(
            localParticipant: ChatParticipantUI,
            latestMessage: com.ruimendes.chat.domain.anonymous.AnonymousMessage?
        ): ChatUI {
            return ChatUI(
                id = ADMIN_INBOX_ID,
                localParticipant = localParticipant,
                otherParticipants = listOf(
                    ChatParticipantUI(
                        id = ADMIN_INBOX_ID,
                        username = "ADMIN",
                        initials = "AD"
                    )
                ),
                lastMessage = latestMessage?.let {
                    ChatMessage(
                        id = it.id,
                        chatId = ADMIN_INBOX_ID,
                        content = it.content,
                        createdAt = it.createdAt,
                        senderId = it.senderEmail,
                        deliveryStatus = ChatMessageDeliveryStatus.SENT
                    )
                },
                lastMessageSenderUsername = latestMessage?.senderEmail,
                isAdminInbox = true
            )
        }
    }
}
```

- [ ] **Step 5: Verify presentation module compiles**

Run: `./gradlew :feature:chat:presentation:compileKotlinAndroid`
Expected: BUILD SUCCESSFUL

- [ ] **Step 6: Commit**

```bash
git add feature/chat/presentation/src/commonMain/kotlin/com/ruimendes/chat/presentation/model/ChatUI.kt \
       feature/chat/presentation/src/commonMain/kotlin/com/ruimendes/chat/presentation/chat_list/ChatListViewModel.kt \
       feature/chat/presentation/src/commonTest/kotlin/com/ruimendes/chat/presentation/anonymous_inbox/FakeAnonymousMessageRepository.kt \
       feature/chat/presentation/src/commonTest/kotlin/com/ruimendes/chat/presentation/chat_list/FakeChatRepository.kt \
       feature/chat/presentation/src/commonTest/kotlin/com/ruimendes/chat/presentation/chat_list/FakeSessionStorage.kt \
       feature/chat/presentation/src/commonTest/kotlin/com/ruimendes/chat/presentation/chat_list/ChatListViewModelTest.kt
git commit -m "feat: show ADMIN inbox in chat list for admin users"
```

---

## Task 9: Anonymous Inbox ViewModel (MVI)

**Files:**
- Create: `feature/chat/presentation/src/commonMain/kotlin/com/ruimendes/chat/presentation/anonymous_inbox/AnonymousInboxState.kt`
- Create: `feature/chat/presentation/src/commonMain/kotlin/com/ruimendes/chat/presentation/anonymous_inbox/AnonymousInboxAction.kt`
- Create: `feature/chat/presentation/src/commonMain/kotlin/com/ruimendes/chat/presentation/anonymous_inbox/AnonymousInboxEvent.kt`
- Create: `feature/chat/presentation/src/commonMain/kotlin/com/ruimendes/chat/presentation/mappers/AnonymousMessageUiMappers.kt`
- Create: `feature/chat/presentation/src/commonMain/kotlin/com/ruimendes/chat/presentation/anonymous_inbox/AnonymousInboxViewModel.kt`

- [ ] **Step 1: Create MVI State, Action, Event**

Create `feature/chat/presentation/src/commonMain/kotlin/com/ruimendes/chat/presentation/anonymous_inbox/AnonymousInboxState.kt`:

```kotlin
package com.ruimendes.chat.presentation.anonymous_inbox

import com.ruimendes.chat.domain.models.ConnectionState
import com.ruimendes.chat.presentation.chat_detail.BannerState
import com.ruimendes.chat.presentation.model.MessageUI
import com.ruimendes.core.presentation.util.UiText

data class AnonymousInboxState(
    val messages: List<MessageUI> = emptyList(),
    val connectionState: ConnectionState = ConnectionState.DISCONNECTED,
    val isPaginationLoading: Boolean = false,
    val paginationError: UiText? = null,
    val endReached: Boolean = false,
    val isNearBottom: Boolean = true,
    val bannerState: BannerState = BannerState()
)
```

Create `feature/chat/presentation/src/commonMain/kotlin/com/ruimendes/chat/presentation/anonymous_inbox/AnonymousInboxAction.kt`:

```kotlin
package com.ruimendes.chat.presentation.anonymous_inbox

sealed interface AnonymousInboxAction {
    data object OnScrollToTop : AnonymousInboxAction
    data object OnRetryPaginationClick : AnonymousInboxAction
    data class OnFirstVisibleIndexChanged(val index: Int) : AnonymousInboxAction
    data class OnTopVisibleIndexChanged(val topVisibleIndex: Int) : AnonymousInboxAction
    data object OnHideBanner : AnonymousInboxAction
}
```

Create `feature/chat/presentation/src/commonMain/kotlin/com/ruimendes/chat/presentation/anonymous_inbox/AnonymousInboxEvent.kt`:

```kotlin
package com.ruimendes.chat.presentation.anonymous_inbox

import com.ruimendes.core.presentation.util.UiText

sealed interface AnonymousInboxEvent {
    data object OnNewMessage : AnonymousInboxEvent
    data class OnError(val error: UiText) : AnonymousInboxEvent
}
```

- [ ] **Step 2: Create AnonymousMessage → MessageUI mapper**

Create `feature/chat/presentation/src/commonMain/kotlin/com/ruimendes/chat/presentation/mappers/AnonymousMessageUiMappers.kt`:

```kotlin
package com.ruimendes.chat.presentation.mappers

import com.ruimendes.chat.domain.anonymous.AnonymousMessage
import com.ruimendes.chat.presentation.model.MessageUI
import com.ruimendes.chat.presentation.util.DateUtils
import com.ruimendes.core.designsystem.components.avatar.ChatParticipantUI
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

fun List<AnonymousMessage>.toAnonymousUIList(): List<MessageUI> {
    return this
        .sortedByDescending { it.createdAt }
        .groupBy {
            it.createdAt.toLocalDateTime(TimeZone.currentSystemDefault()).date
        }
        .flatMap { (date, messages) ->
            messages.map { it.toUI() } + MessageUI.DateSeparator(
                id = date.toString(),
                date = DateUtils.formatDateSeparator(date)
            )
        }
}

fun AnonymousMessage.toUI(): MessageUI.OtherUserMessage {
    val emailPrefix = senderEmail.substringBefore("@")
    val initials = emailPrefix
        .take(2)
        .uppercase()
        .ifEmpty { "??" }

    return MessageUI.OtherUserMessage(
        id = id,
        content = content,
        formattedSentTime = DateUtils.formatMessageTime(instant = createdAt),
        sender = ChatParticipantUI(
            id = senderEmail,
            username = senderEmail,
            initials = initials,
            imageUrl = null
        )
    )
}
```

- [ ] **Step 3: Create AnonymousInboxViewModel**

Create `feature/chat/presentation/src/commonMain/kotlin/com/ruimendes/chat/presentation/anonymous_inbox/AnonymousInboxViewModel.kt`:

```kotlin
package com.ruimendes.chat.presentation.anonymous_inbox

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import askme.feature.chat.presentation.generated.resources.Res
import askme.feature.chat.presentation.generated.resources.today
import com.ruimendes.chat.domain.anonymous.AnonymousMessage
import com.ruimendes.chat.domain.anonymous.AnonymousMessageRepository
import com.ruimendes.chat.domain.chat.ChatConnectionClient
import com.ruimendes.chat.domain.models.ConnectionState
import com.ruimendes.chat.presentation.chat_detail.BannerState
import com.ruimendes.chat.presentation.mappers.toAnonymousUIList
import com.ruimendes.chat.presentation.model.MessageUI
import com.ruimendes.core.domain.util.DataErrorException
import com.ruimendes.core.domain.util.Paginator
import com.ruimendes.core.presentation.util.UiText
import com.ruimendes.core.presentation.util.toUiText
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AnonymousInboxViewModel(
    private val anonymousMessageRepository: AnonymousMessageRepository,
    private val connectionClient: ChatConnectionClient
) : ViewModel() {

    private val eventChannel = Channel<AnonymousInboxEvent>()
    val events = eventChannel.receiveAsFlow()

    private var hasLoadedInitialData = false

    private val _state = MutableStateFlow(AnonymousInboxState())

    private val paginator = Paginator<String?, AnonymousMessage>(
        initialKey = null,
        onLoadUpdated = { isLoading ->
            _state.update { it.copy(isPaginationLoading = isLoading) }
        },
        onRequest = { beforeTimestamp ->
            val before = beforeTimestamp?.let { kotlinx.datetime.Instant.parse(it) }
            anonymousMessageRepository.fetchMessages(
                before = before,
                pageSize = PAGE_SIZE
            )
        },
        getNextKey = { messages ->
            messages.minOfOrNull { it.createdAt }?.toString()
        },
        onError = { throwable ->
            if (throwable is DataErrorException) {
                _state.update {
                    it.copy(paginationError = throwable.error.toUiText())
                }
            }
        },
        onSuccess = { messages, _ ->
            _state.update {
                it.copy(
                    endReached = messages.isEmpty(),
                    paginationError = null
                )
            }
        }
    )

    val state = combine(
        _state,
        anonymousMessageRepository.getMessages()
    ) { currentState, messages ->
        currentState.copy(
            messages = messages.toAnonymousUIList()
        )
    }
        .onStart {
            if (!hasLoadedInitialData) {
                observeConnectionState()
                observeNewMessages()
                hasLoadedInitialData = true
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000L),
            initialValue = AnonymousInboxState()
        )

    fun onAction(action: AnonymousInboxAction) {
        when (action) {
            AnonymousInboxAction.OnScrollToTop -> loadNextItems()
            AnonymousInboxAction.OnRetryPaginationClick -> loadNextItems()
            AnonymousInboxAction.OnHideBanner -> hideBanner()
            is AnonymousInboxAction.OnTopVisibleIndexChanged -> updateBanner(action.topVisibleIndex)
            is AnonymousInboxAction.OnFirstVisibleIndexChanged -> updateNearBottom(action.index)
        }
    }

    private fun updateNearBottom(firstVisibleIndex: Int) {
        _state.update { it.copy(isNearBottom = firstVisibleIndex <= 3) }
    }

    private fun updateBanner(topVisibleIndex: Int) {
        val visibleDate = calculateBannerDateFromIndex(
            messages = state.value.messages,
            index = topVisibleIndex
        )
        _state.update {
            it.copy(
                bannerState = BannerState(
                    formattedDate = visibleDate,
                    isVisible = visibleDate != null
                )
            )
        }
    }

    private fun calculateBannerDateFromIndex(
        messages: List<MessageUI>,
        index: Int
    ): UiText? {
        if (messages.isEmpty() || index < 0 || index >= messages.size) return null

        val nearestDateSeparator = (index until messages.size)
            .asSequence()
            .mapNotNull { i ->
                val item = messages.getOrNull(i)
                if (item is MessageUI.DateSeparator) item.date else null
            }
            .firstOrNull()

        return when (nearestDateSeparator) {
            is UiText.Resource -> {
                if (nearestDateSeparator.id == Res.string.today) null else nearestDateSeparator
            }
            else -> nearestDateSeparator
        }
    }

    private fun hideBanner() {
        _state.update {
            it.copy(bannerState = it.bannerState.copy(isVisible = false))
        }
    }

    private fun loadNextItems() {
        viewModelScope.launch {
            paginator.loadNextItems()
        }
    }

    private fun observeConnectionState() {
        connectionClient.connectionState
            .onEach { connectionState ->
                if (connectionState == ConnectionState.CONNECTED) {
                    paginator.loadNextItems()
                }
                _state.update { it.copy(connectionState = connectionState) }
            }
            .launchIn(viewModelScope)
    }

    companion object {
        private const val PAGE_SIZE = 20
    }

    private fun observeNewMessages() {
        val currentMessages = state.map { it.messages }.distinctUntilChanged()
        val newMessages = anonymousMessageRepository.getMessages()
        val isNearBottom = state.map { it.isNearBottom }.distinctUntilChanged()

        combine(
            currentMessages,
            newMessages,
            isNearBottom
        ) { current, new, nearBottom ->
            val lastNewId = new.maxByOrNull { it.createdAt }?.id
            val lastCurrentId = current.firstOrNull { it !is MessageUI.DateSeparator }?.id
            if (lastNewId != lastCurrentId && nearBottom) {
                eventChannel.send(AnonymousInboxEvent.OnNewMessage)
            }
        }.launchIn(viewModelScope)
    }
}
```

- [ ] **Step 4: Verify presentation module compiles**

Run: `./gradlew :feature:chat:presentation:compileKotlinAndroid`
Expected: BUILD SUCCESSFUL

- [ ] **Step 5: Commit**

```bash
git add feature/chat/presentation/src/commonMain/kotlin/com/ruimendes/chat/presentation/anonymous_inbox/ \
       feature/chat/presentation/src/commonMain/kotlin/com/ruimendes/chat/presentation/mappers/AnonymousMessageUiMappers.kt
git commit -m "feat: add anonymous inbox MVI ViewModel with pagination"
```

---

## Task 10: Anonymous Inbox Screen & Adaptive Layout Integration

**Files:**
- Create: `feature/chat/presentation/src/commonMain/kotlin/com/ruimendes/chat/presentation/anonymous_inbox/AnonymousInboxScreen.kt`
- Modify: `feature/chat/presentation/src/commonMain/kotlin/com/ruimendes/chat/presentation/chat_list_detail/ChatListDetailAdaptiveLayout.kt`
- Modify: `feature/chat/presentation/src/commonMain/kotlin/com/ruimendes/chat/presentation/chat_list_detail/ChatListDetailState.kt`
- Modify: `feature/chat/presentation/src/commonMain/kotlin/com/ruimendes/chat/presentation/di/ChatPresentationModule.kt`

- [ ] **Step 1: Create AnonymousInboxScreen composable**

Create `feature/chat/presentation/src/commonMain/kotlin/com/ruimendes/chat/presentation/anonymous_inbox/AnonymousInboxScreen.kt`:

```kotlin
package com.ruimendes.chat.presentation.anonymous_inbox

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.backhandler.BackHandler
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.safeDrawing
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ruimendes.chat.presentation.chat_detail.ChatDetailScreen
import com.ruimendes.chat.presentation.chat_detail.MessageBannerListener
import com.ruimendes.chat.presentation.chat_detail.PaginationScrollListener
import com.ruimendes.chat.presentation.chat_detail.components.ChatDetailHeader
import com.ruimendes.chat.presentation.chat_detail.components.DateChip
import com.ruimendes.chat.presentation.chat_detail.components.MessageList
import com.ruimendes.chat.presentation.components.ChatHeader
import com.ruimendes.chat.presentation.model.MessageUI
import com.ruimendes.core.presentation.util.ObserveAsEvents
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.launch
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun AnonymousInboxRoot(
    isDetailPresent: Boolean,
    onBack: () -> Unit,
    viewModel: AnonymousInboxViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackbarState = remember { SnackbarHostState() }
    val messageListState = rememberLazyListState()
    val scope = rememberCoroutineScope()

    ObserveAsEvents(viewModel.events) { event ->
        when (event) {
            is AnonymousInboxEvent.OnError -> {
                snackbarState.showSnackbar(event.error.asStringAsync())
            }
            AnonymousInboxEvent.OnNewMessage -> {
                scope.launch {
                    messageListState.animateScrollToItem(0)
                }
            }
        }
    }

    BackHandler(enabled = !isDetailPresent) {
        onBack()
    }

    AnonymousInboxScreen(
        state = state,
        messageListState = messageListState,
        snackbarState = snackbarState,
        onAction = viewModel::onAction
    )
}

@Composable
fun AnonymousInboxScreen(
    state: AnonymousInboxState,
    messageListState: LazyListState,
    snackbarState: SnackbarHostState,
    onAction: (AnonymousInboxAction) -> Unit,
) {
    val realMessageItemCount = remember(state.messages) {
        state.messages.count { it is MessageUI.OtherUserMessage }
    }

    LaunchedEffect(messageListState) {
        snapshotFlow {
            messageListState.firstVisibleItemIndex to messageListState.layoutInfo.totalItemsCount
        }.filter { (firstVisible, total) ->
            firstVisible >= 0 && total > 0
        }.collect { (firstVisible, _) ->
            onAction(AnonymousInboxAction.OnFirstVisibleIndexChanged(firstVisible))
        }
    }

    MessageBannerListener(
        lazyListState = messageListState,
        messages = state.messages,
        isBannerVisible = state.bannerState.isVisible,
        onShowBanner = { index ->
            onAction(AnonymousInboxAction.OnTopVisibleIndexChanged(index))
        },
        onHide = {
            onAction(AnonymousInboxAction.OnHideBanner)
        }
    )

    PaginationScrollListener(
        lazyListState = messageListState,
        itemCount = realMessageItemCount,
        isPaginationLoading = state.isPaginationLoading,
        isEndReached = state.endReached,
        onNearTop = {
            onAction(AnonymousInboxAction.OnScrollToTop)
        }
    )

    var headerHeight by remember { mutableStateOf(0.dp) }
    val density = LocalDensity.current

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        snackbarHost = { SnackbarHost(snackbarState) },
        contentWindowInsets = WindowInsets.safeDrawing,
    ) { innerPadding ->
        Box(
            modifier = Modifier.padding(innerPadding)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                ChatHeader(
                    modifier = Modifier.onSizeChanged {
                        headerHeight = with(density) { it.height.toDp() }
                    }
                ) {
                    // Simple header with just title "ADMIN"
                    Text(
                        text = "ADMIN",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(16.dp)
                    )
                }

                MessageList(
                    messages = state.messages,
                    messageWithOpenMenu = null,
                    listState = messageListState,
                    isPaginationLoading = state.isPaginationLoading,
                    paginationError = state.paginationError?.toString(),
                    onMessageLongClick = {},
                    onMessageRetryClick = {},
                    onDismissMessageMenu = {},
                    onDeleteMessageClick = {},
                    onRetryPaginationClick = {
                        onAction(AnonymousInboxAction.OnRetryPaginationClick)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                )

                // Read-only footer
                Text(
                    text = "Messages are read-only",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                )
            }

            AnimatedVisibility(
                visible = state.bannerState.isVisible,
                enter = fadeIn(),
                exit = fadeOut(),
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = headerHeight + 16.dp)
            ) {
                if (state.bannerState.formattedDate != null) {
                    DateChip(
                        date = state.bannerState.formattedDate.asString()
                    )
                }
            }
        }
    }
}
```

- [ ] **Step 2: Add isAdminInbox to ChatListDetailState**

Modify `feature/chat/presentation/src/commonMain/kotlin/com/ruimendes/chat/presentation/chat_list_detail/ChatListDetailState.kt`:

```kotlin
data class ChatListDetailState(
    val selectedChatId: String? = null,
    val isAdminInbox: Boolean = false,
    val dialogState: DialogState = DialogState.Hidden
)
```

- [ ] **Step 3: Update ChatListDetailAdaptiveLayout to route to inbox**

Modify the detail pane in `ChatListDetailAdaptiveLayout.kt`. Update the `detailPane` lambda:

Replace the current `detailPane` block with:

```kotlin
detailPane = {
    AnimatedPane {
        val listPane = scaffoldNavigator.scaffoldValue[ListDetailPaneScaffoldRole.List]
        val isDetailPresent = detailPane == PaneAdaptedValue.Expanded && listPane == PaneAdaptedValue.Expanded

        if (sharedState.isAdminInbox) {
            AnonymousInboxRoot(
                isDetailPresent = isDetailPresent,
                onBack = {
                    scope.launch {
                        if (scaffoldNavigator.canNavigateBack()) {
                            scaffoldNavigator.navigateBack()
                        }
                    }
                }
            )
        } else {
            ChatDetailRoot(
                chatId = sharedState.selectedChatId,
                isDetailPresent = isDetailPresent,
                onChatMembersClick = {
                    chatListDetailViewModel.onAction(ChatListDetailAction.OnManageChatClick)
                },
                onBack = {
                    scope.launch {
                        if (scaffoldNavigator.canNavigateBack()) {
                            scaffoldNavigator.navigateBack()
                        }
                    }
                }
            )
        }
    }
},
```

Add the import at the top:

```kotlin
import com.ruimendes.chat.presentation.anonymous_inbox.AnonymousInboxRoot
```

- [ ] **Step 4: Update ChatListDetailViewModel to track isAdminInbox**

In `ChatListDetailViewModel.kt`, update the `OnSelectChat` action handler to set `isAdminInbox`:

```kotlin
is ChatListDetailAction.OnSelectChat -> {
    _state.update {
        it.copy(
            selectedChatId = action.chatId,
            isAdminInbox = action.chatId == com.ruimendes.chat.presentation.chat_list.ChatListViewModel.ADMIN_INBOX_ID
        )
    }
}
```

Also update the `BackHandler` in the layout and the `LaunchedEffect` that clears selection to also reset `isAdminInbox`:

In `ChatListDetailAdaptiveLayout.kt`, update the `BackHandler`:

```kotlin
BackHandler(enabled = scaffoldNavigator.canNavigateBack()) {
    scope.launch {
        scaffoldNavigator.navigateBack()
        chatListDetailViewModel.onAction(ChatListDetailAction.OnSelectChat(null))
    }
}
```

And the `LaunchedEffect`:

```kotlin
LaunchedEffect(detailPane, sharedState.selectedChatId) {
    if (detailPane == PaneAdaptedValue.Hidden && sharedState.selectedChatId != null) {
        chatListDetailViewModel.onAction(ChatListDetailAction.OnSelectChat(null))
    }
}
```

These already call `OnSelectChat(null)` which will set `isAdminInbox = false`.

- [ ] **Step 5: Register AnonymousInboxViewModel in DI**

Modify `feature/chat/presentation/src/commonMain/kotlin/com/ruimendes/chat/presentation/di/ChatPresentationModule.kt`:

```kotlin
import com.ruimendes.chat.presentation.anonymous_inbox.AnonymousInboxViewModel

val chatPresentationModule = module {
    viewModelOf(::ChatListViewModel)
    viewModelOf(::ChatListDetailViewModel)
    viewModelOf(::CreateChatViewModel)
    viewModelOf(::ChatDetailViewModel)
    viewModelOf(::ManageChatViewModel)
    viewModelOf(::ProfileViewModel)
    viewModelOf(::AnonymousInboxViewModel)
}
```

- [ ] **Step 6: Verify full project compiles**

Run: `./gradlew :composeApp:compileKotlinAndroid`
Expected: BUILD SUCCESSFUL

- [ ] **Step 7: Commit**

```bash
git add feature/chat/presentation/src/commonMain/kotlin/com/ruimendes/chat/presentation/anonymous_inbox/AnonymousInboxScreen.kt \
       feature/chat/presentation/src/commonMain/kotlin/com/ruimendes/chat/presentation/chat_list_detail/ChatListDetailAdaptiveLayout.kt \
       feature/chat/presentation/src/commonMain/kotlin/com/ruimendes/chat/presentation/chat_list_detail/ChatListDetailState.kt \
       feature/chat/presentation/src/commonMain/kotlin/com/ruimendes/chat/presentation/chat_list_detail/ChatListDetailViewModel.kt \
       feature/chat/presentation/src/commonMain/kotlin/com/ruimendes/chat/presentation/di/ChatPresentationModule.kt
git commit -m "feat: add anonymous inbox screen and integrate with adaptive layout"
```

---

## Task 11: ViewModel Tests

**Files:**
- Create: `feature/chat/presentation/src/commonTest/kotlin/com/ruimendes/chat/presentation/anonymous_inbox/AnonymousInboxViewModelTest.kt`

- [ ] **Step 1: Write AnonymousInboxViewModel tests**

Create `feature/chat/presentation/src/commonTest/kotlin/com/ruimendes/chat/presentation/anonymous_inbox/AnonymousInboxViewModelTest.kt`:

```kotlin
package com.ruimendes.chat.presentation.anonymous_inbox

import app.cash.turbine.test
import assertk.assertThat
import assertk.assertions.hasSize
import assertk.assertions.isEmpty
import assertk.assertions.isEqualTo
import assertk.assertions.isFalse
import assertk.assertions.isTrue
import com.ruimendes.chat.domain.anonymous.AnonymousMessage
import com.ruimendes.chat.domain.models.ConnectionState
import com.ruimendes.chat.domain.chat.ChatConnectionClient
import com.ruimendes.chat.presentation.model.MessageUI
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlinx.datetime.Instant
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test

@OptIn(ExperimentalCoroutinesApi::class)
class AnonymousInboxViewModelTest {

    private val testDispatcher = UnconfinedTestDispatcher()
    private lateinit var fakeRepository: FakeAnonymousMessageRepository
    private lateinit var fakeConnectionClient: FakeConnectionClient
    private lateinit var viewModel: AnonymousInboxViewModel

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        fakeRepository = FakeAnonymousMessageRepository()
        fakeConnectionClient = FakeConnectionClient()
        viewModel = AnonymousInboxViewModel(
            anonymousMessageRepository = fakeRepository,
            connectionClient = fakeConnectionClient
        )
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state has empty messages`() = runTest {
        viewModel.state.test {
            val state = awaitItem()
            assertThat(state.messages).isEmpty()
        }
    }

    @Test
    fun `messages from repository appear in state`() = runTest {
        val messages = listOf(
            AnonymousMessage(
                id = "1",
                senderEmail = "john@example.com",
                content = "Hello",
                createdAt = Instant.parse("2026-03-25T10:30:00Z")
            )
        )
        fakeRepository.emit(messages)

        viewModel.state.test {
            val state = awaitItem()
            val otherMessages = state.messages.filterIsInstance<MessageUI.OtherUserMessage>()
            assertThat(otherMessages).hasSize(1)
            assertThat(otherMessages.first().content).isEqualTo("Hello")
            assertThat(otherMessages.first().sender.username).isEqualTo("john@example.com")
            assertThat(otherMessages.first().sender.initials).isEqualTo("JO")
        }
    }

    @Test
    fun `connection state is reflected in state`() = runTest {
        viewModel.state.test {
            awaitItem() // initial

            fakeConnectionClient.emitConnectionState(ConnectionState.CONNECTED)
            val state = awaitItem()
            assertThat(state.connectionState).isEqualTo(ConnectionState.CONNECTED)
        }
    }
}

class FakeConnectionClient : ChatConnectionClient {
    private val _connectionState = MutableStateFlow(ConnectionState.DISCONNECTED)
    override val chatMessages = flowOf<com.ruimendes.chat.domain.models.ChatMessage>()
    override val connectionState: StateFlow<ConnectionState> = _connectionState

    fun emitConnectionState(state: ConnectionState) {
        _connectionState.value = state
    }
}
```

- [ ] **Step 2: Run tests**

Run: `./gradlew :feature:chat:presentation:testDebugUnitTest --tests "com.ruimendes.chat.presentation.anonymous_inbox.AnonymousInboxViewModelTest" --info`
Expected: ALL PASS

- [ ] **Step 3: Commit**

```bash
git add feature/chat/presentation/src/commonTest/kotlin/com/ruimendes/chat/presentation/anonymous_inbox/AnonymousInboxViewModelTest.kt
git commit -m "test: add anonymous inbox ViewModel unit tests"
```

---

## Task 12: Final Integration Verification

- [ ] **Step 1: Run all tests**

Run: `./gradlew testDebugUnitTest --info`
Expected: ALL PASS

- [ ] **Step 2: Build full project**

Run: `./gradlew :composeApp:assembleDebug`
Expected: BUILD SUCCESSFUL

- [ ] **Step 3: Manual smoke test**

1. Launch app on Android emulator/device
2. Log in as admin user
3. Verify "ADMIN" conversation appears pinned at top of chat list
4. Tap it — verify anonymous inbox opens
5. Verify messages load with sender email as username
6. Verify send button is disabled / read-only footer shows
7. Verify scrolling loads older messages (pagination)
8. Log in as non-admin user — verify "ADMIN" conversation is NOT visible
