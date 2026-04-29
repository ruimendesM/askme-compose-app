# Anonymous Messages Inbox for Admin

## Overview

Extend the existing chat feature to show an "ADMIN" conversation pinned at the top of the chat list for admin users. This conversation displays anonymous messages received via a new server endpoint, presented in a read-only chat detail view.

The server exposes `GET /api/anonymous-messages` (paginated, admin-only) and pushes `NEW_ANONYMOUS_MESSAGE` WebSocket events to the admin user. The mobile app consumes both, stores messages offline-first in Room, and displays them using shared chat UI components.

## Decisions

- **Admin detection:** Decode the `role` claim from the JWT access token. No server secrets are exposed — JWT payloads are signed but not encrypted.
- **Architecture:** Separate domain with shared UI components (Approach B). Anonymous messages have their own models, repository, and Room table, but reuse existing chat detail composables (message bubbles, date separators, lazy list).
- **Chat list placement:** ADMIN conversation is pinned at the top of the chat list, always first regardless of last activity.
- **Message display:** Each anonymous message renders as `OtherUserMessage` with the sender's email as the username and initials derived from the email prefix.
- **Send disabled:** The inbox is read-only. The message input shows a "Messages are read-only" placeholder with a greyed-out send button.
- **WebSocket handling:** Handled in the existing `WebSocketChatConnectionClient` as a new case in the message type routing.
- **Pagination:** Reuses the existing `Paginator` pattern with cursor-based `before`/`pageSize` matching the server API.
- **Offline-first:** Messages are stored in Room and read via Flow, following the same pattern as regular chat messages.

## JWT Role Decoding

Add a `role: String?` field to `User` (domain) and its serializable counterparts.

New utility function in `core:data`:

```
decodeJwtRole(accessToken: String): String?
```

Splits the token by `.`, base64-decodes the middle segment, parses the JSON payload, and extracts the `"role"` claim. Called during login and token refresh mapping.

`AuthInfo` gains a computed property:

```
val isAdmin: Boolean get() = user.role == "ADMIN"
```

Updated models:
- `User` — add `role: String?`
- `UserSerializable` — add `role: String? = null`
- `AuthInfoMapper` — call `decodeJwtRole()` when mapping from server response to domain

## Data Layer

### Room Database

New entity in the existing chat database:

**`AnonymousMessageEntity`**
- `id: String` (PrimaryKey)
- `senderEmail: String`
- `content: String`
- `createdAt: Long` (timestamp millis)

**`AnonymousMessageDao`**
- `upsertMessage(entity)` / `upsertMessages(entities)`
- `getMessages(): Flow<List<AnonymousMessageEntity>>` — ordered by `createdAt DESC`
- `getLatestMessage(): Flow<AnonymousMessageEntity?>` — for chat list preview
- Pagination query: `WHERE createdAt < :before ORDER BY createdAt DESC LIMIT :pageSize`

### Network

**`AnonymousMessageDto`** (Kotlinx Serialization)
- `id: String`
- `senderEmail: String`
- `content: String`
- `createdAt: Instant`

**`AnonymousMessageService`** (interface) / **`KtorAnonymousMessageService`** (implementation)
- `getMessages(before: Instant?, pageSize: Int): Result<List<AnonymousMessageDto>, DataError.Remote>`
- Calls `GET /api/anonymous-messages?before={before}&pageSize={pageSize}`

### Domain Model

**`AnonymousMessage`**
- `id: String`
- `senderEmail: String`
- `content: String`
- `createdAt: Instant`

### Repository

**`AnonymousMessageRepository`** (interface in domain)
- `getMessages(): Flow<List<AnonymousMessage>>` — reads from Room
- `getLatestMessage(): Flow<AnonymousMessage?>` — for chat list preview
- `fetchMessages(before: Instant?, pageSize: Int): Result<List<AnonymousMessage>, DataError>` — fetches from server, upserts into Room
- `saveMessage(message: AnonymousMessage): EmptyResult<DataError.Local>` — for WebSocket incoming

**`OfflineFirstAnonymousMessageRepository`** (implementation in data)
- Follows the same offline-first pattern as `OfflineFirstMessageRepository`
- Reads from Room via Flow
- Fetches from server and syncs to Room on demand

### Mappers

- `AnonymousMessageDto.toDomain(): AnonymousMessage`
- `AnonymousMessageEntity.toDomain(): AnonymousMessage`
- `AnonymousMessage.toEntity(): AnonymousMessageEntity`

## WebSocket Integration

In `WebSocketChatConnectionClient`, add a new case to the `when` block that routes by message type:

- Type: `"NEW_ANONYMOUS_MESSAGE"`
- Deserialize payload as `AnonymousMessageDto`
- Map to `AnonymousMessage` domain model
- Call `anonymousMessageRepository.saveMessage()`
- Room Flow automatically notifies subscribed UI

Guard: only process this event if the current user is admin (belt-and-suspenders — the server only sends it to admin, but the client validates too).

## Presentation Layer

### Chat List Changes

**`ChatUI`** — add `isAdminInbox: Boolean = false`

**`ChatListViewModel`** changes:
- Observe `authInfo.isAdmin` from SessionStorage
- When admin, combine `chatRepository.getChats()` with `anonymousMessageRepository.getLatestMessage()`
- Prepend a synthetic `ChatUI` item:
  - `id = ADMIN_INBOX_ID` (well-known constant)
  - `isAdminInbox = true`
  - `otherParticipants` = single participant with username `"ADMIN"`, initials `"AD"`
  - `lastMessage` = latest anonymous message (for preview text)

### Adaptive Layout Routing

**`ChatListDetailAdaptiveLayout`** changes:
- Read `selectedChat.isAdminInbox` from the selected chat
- `true` → render `AnonymousInboxRoot` in the detail pane
- `false` → render `ChatDetailRoot` as before

### Anonymous Inbox ViewModel

**`AnonymousInboxViewModel`** — lean MVI ViewModel:

**State (`AnonymousInboxState`):**
- `messages: List<MessageUI>`
- `connectionState: ConnectionState`
- `isPaginationLoading: Boolean`
- `paginationError: UiText?`
- `endReached: Boolean`
- `isNearBottom: Boolean`
- `bannerState: BannerState`

Not included (vs `ChatDetailState`): `messageTextFieldState`, `canSendMessage`, `messageWithOpenMenu`, `isChatOptionsOpen`, `chat`.

**Actions (`AnonymousInboxAction`):**
- `OnScrollToTop`
- `OnRetryPaginationClick`
- `OnFirstVisibleIndexChanged(index: Int)`
- `OnTopVisibleIndexChanged(index: Int)`
- `OnHideBanner`

Not included: send, delete, retry, leave, menu actions.

**Events (`AnonymousInboxEvent`):**
- `OnNewMessage` — triggers auto-scroll
- `OnError(error: UiText)`

**Message mapping:** `AnonymousMessage` maps to `MessageUI.OtherUserMessage` with:
- `sender.username` = `senderEmail`
- `sender.initials` = first two characters of email prefix, uppercased (e.g., "john@example.com" → "JO")
- `sender.profilePictureUrl` = `null`

**Pagination:** Uses the existing `Paginator` utility with `createdAt` of the oldest loaded message as the `before` cursor.

### Screen Composable

**`AnonymousInboxScreen`** reuses extracted shared components:
- `OtherUserMessage` bubble composable
- `DateSeparator` composable
- Date banner overlay
- Message list `LazyColumn` with pagination loading
- Connection state banner

Specific to this screen:
- Header shows "ADMIN" title with back button (no options menu)
- Bottom bar shows disabled input placeholder ("Messages are read-only") with greyed-out send button
- No long-press menus on messages

### Component Extraction

The following composables need to be extracted from `ChatDetailScreen` into shared reusable components:
- Message bubble (OtherUserMessage variant)
- Date separator
- Date banner
- Message lazy list (parameterized — accepts list of `MessageUI`, scroll callbacks)
- Pagination loading indicator

## Koin DI

**`chatDataModule`** additions:
- `singleOf(::KtorAnonymousMessageService) bind AnonymousMessageService::class`
- `singleOf(::OfflineFirstAnonymousMessageRepository) bind AnonymousMessageRepository::class`
- DAO provided from existing database instance

**`chatPresentationModule`** addition:
- `viewModelOf(::AnonymousInboxViewModel)`

No new modules — everything integrates into the existing chat feature modules.

## Navigation

No new routes. The anonymous inbox is displayed in the detail pane of the existing `ChatListDetailRoute`. Routing is handled by `ChatListDetailAdaptiveLayout` based on `selectedChat.isAdminInbox`.

Back navigation works unchanged — clears selection, returns to list pane on phone.

No deep links for the admin inbox.

## Testing

### JWT Decoding
- `decodeJwtRole()` with valid token containing role claim
- Token without role claim returns null
- Malformed token returns null gracefully
- Expired token still returns role (claims are readable regardless of expiry)

### Mappers
- `AnonymousMessageDto` → `AnonymousMessage`
- `AnonymousMessageEntity` → `AnonymousMessage`
- `AnonymousMessage` → `AnonymousMessageEntity`

### Repository
- `OfflineFirstAnonymousMessageRepository`:
  - `getMessages()` emits from Room
  - `fetchMessages()` calls server and upserts into Room
  - `saveMessage()` persists to Room

### WebSocket Routing
- `NEW_ANONYMOUS_MESSAGE` event is parsed and saved via repository
- Event is ignored for non-admin users

### AnonymousInboxViewModel
- Initial state has empty messages
- Messages load from repository (verified with Turbine)
- Pagination triggers fetchMessages on scroll
- New WebSocket message appears in state
- Connection state is reflected in state

### ChatListViewModel
- Admin user sees ADMIN item pinned at first position
- Non-admin user does not see ADMIN item
- ADMIN item shows latest anonymous message as preview

### Test Infrastructure
- `FakeAnonymousMessageRepository` — in-memory implementation for ViewModel tests
- Reuse existing fakes where available

### Not in scope
- Compose UI tests (deferred to a follow-up)

## Out of Scope

- Reply/send functionality from the admin inbox
- Push notifications for anonymous messages
- Unread message counts on the ADMIN conversation
- Deep links to the admin inbox
- POST endpoint consumption (not relevant for mobile)
