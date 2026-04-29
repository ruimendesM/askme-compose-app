package com.ruimendes.chat.presentation.anonymous_inbox

import app.cash.turbine.test
import com.ruimendes.chat.domain.anonymous.AnonymousMessage
import com.ruimendes.chat.domain.chat.ChatConnectionClient
import com.ruimendes.chat.domain.models.ConnectionState
import com.ruimendes.chat.presentation.model.MessageUI
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.time.Instant

class AnonymousInboxViewModelTest {

    private lateinit var fakeRepository: FakeAnonymousMessageRepository
    private lateinit var fakeConnectionClient: FakeConnectionClient
    private lateinit var viewModel: AnonymousInboxViewModel

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
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
            assertTrue(state.messages.isEmpty())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `messages from repository appear in state`() = runTest {
        val message = AnonymousMessage(
            id = "1",
            senderEmail = "john@example.com",
            content = "Hello",
            createdAt = Instant.parse("2026-03-25T10:30:00Z")
        )

        viewModel.state.test {
            // consume initial state
            awaitItem()

            fakeRepository.emit(listOf(message))

            val state = awaitItem()
            val otherUserMessages = state.messages.filterIsInstance<MessageUI.OtherUserMessage>()
            assertTrue(otherUserMessages.isNotEmpty())
            val uiMessage = otherUserMessages.first()
            assertEquals("Hello", uiMessage.content)
            assertEquals("john@example.com", uiMessage.sender.username)
            assertEquals("JO", uiMessage.sender.initials)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `connection state is reflected in state`() = runTest {
        viewModel.state.test {
            // consume initial state
            awaitItem()

            fakeConnectionClient.connectionState.value = ConnectionState.CONNECTED

            val state = awaitItem()
            assertEquals(ConnectionState.CONNECTED, state.connectionState)
            cancelAndIgnoreRemainingEvents()
        }
    }
}

class FakeConnectionClient : ChatConnectionClient {
    override val chatMessages: Flow<com.ruimendes.chat.domain.models.ChatMessage> = flowOf()
    override val connectionState: MutableStateFlow<ConnectionState> =
        MutableStateFlow(ConnectionState.DISCONNECTED)
}
