package com.ruimendes.chat.presentation.chat_list

import app.cash.turbine.test
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
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import kotlin.time.Instant

@OptIn(ExperimentalCoroutinesApi::class)
class ChatListViewModelTest {

    private val testDispatcher = UnconfinedTestDispatcher()

    private lateinit var fakeChatRepository: FakeChatRepository
    private lateinit var fakeSessionStorage: FakeSessionStorage
    private lateinit var fakeAnonymousMessageRepository: FakeAnonymousMessageRepository
    private lateinit var viewModel: ChatListViewModel

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        fakeChatRepository = FakeChatRepository()
        fakeSessionStorage = FakeSessionStorage()
        fakeAnonymousMessageRepository = FakeAnonymousMessageRepository()
        viewModel = ChatListViewModel(
            repository = fakeChatRepository,
            sessionStorage = fakeSessionStorage,
            anonymousMessageRepository = fakeAnonymousMessageRepository
        )
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `admin user sees ADMIN inbox pinned first`() = runTest {
        viewModel.state.test {
            awaitItem() // initial empty state

            fakeSessionStorage.emit(adminAuthInfo())

            val state = awaitItem()
            assertTrue(state.chats.isNotEmpty(), "Expected at least one chat for admin")
            val first = state.chats.first()
            assertTrue(first.isAdminInbox, "Expected first chat to be admin inbox")
            assertEquals(ChatListViewModel.ADMIN_INBOX_ID, first.id)

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `non-admin user does not see ADMIN inbox`() = runTest {
        viewModel.state.test {
            awaitItem() // initial empty state

            fakeSessionStorage.emit(regularAuthInfo())

            val state = awaitItem()
            assertFalse(
                state.chats.any { it.isAdminInbox },
                "Non-admin should not see admin inbox"
            )

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `ADMIN item shows latest anonymous message preview`() = runTest {
        val messageContent = "Hello from anonymous"
        val senderEmail = "anon@example.com"
        val message = AnonymousMessage(
            id = "msg-1",
            senderEmail = senderEmail,
            content = messageContent,
            createdAt = Instant.parse("2026-03-25T10:30:00Z")
        )

        viewModel.state.test {
            awaitItem() // initial empty state

            fakeSessionStorage.emit(adminAuthInfo())
            awaitItem() // state with admin inbox but no message

            fakeAnonymousMessageRepository.emit(listOf(message))

            val state = awaitItem()
            val adminInbox = state.chats.first { it.isAdminInbox }
            assertEquals(messageContent, adminInbox.lastMessage?.content)
            assertEquals(senderEmail, adminInbox.lastMessageSenderUsername)

            cancelAndIgnoreRemainingEvents()
        }
    }

    private fun adminAuthInfo(): AuthInfo = AuthInfo(
        accessToken = "token",
        refreshToken = "refresh",
        user = User(
            id = "admin-user-1",
            email = "admin@example.com",
            username = "Admin",
            hasEmailVerified = true,
            role = "ADMIN"
        )
    )

    private fun regularAuthInfo(): AuthInfo = AuthInfo(
        accessToken = "token",
        refreshToken = "refresh",
        user = User(
            id = "user-1",
            email = "user@example.com",
            username = "RegularUser",
            hasEmailVerified = true,
            role = "USER"
        )
    )
}
