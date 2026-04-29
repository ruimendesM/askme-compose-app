package com.ruimendes.chat.presentation.chat_list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ruimendes.chat.domain.anonymous.AnonymousMessage
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

        val adminInboxList = if (authInfo.isAdmin) {
            listOf(buildAdminInboxItem(authInfo.user.id, latestAnonymousMessage))
        } else {
            emptyList()
        }

        currentState.copy(
            chats = adminInboxList + regularChats,
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

        fun buildAdminInboxItem(
            localUserId: String,
            latestMessage: AnonymousMessage?
        ): ChatUI {
            val adminParticipant = ChatParticipantUI(
                id = ADMIN_INBOX_ID,
                username = "ADMIN",
                initials = "AD"
            )
            val localParticipant = ChatParticipantUI(
                id = localUserId,
                username = "",
                initials = ""
            )
            val lastMessage = latestMessage?.let {
                ChatMessage(
                    id = it.id,
                    chatId = ADMIN_INBOX_ID,
                    content = it.content,
                    createdAt = it.createdAt,
                    senderId = it.senderEmail,
                    deliveryStatus = ChatMessageDeliveryStatus.SENT
                )
            }
            return ChatUI(
                id = ADMIN_INBOX_ID,
                localParticipant = localParticipant,
                otherParticipants = listOf(adminParticipant),
                lastMessage = lastMessage,
                lastMessageSenderUsername = latestMessage?.senderEmail,
                isAdminInbox = true
            )
        }
    }
}
