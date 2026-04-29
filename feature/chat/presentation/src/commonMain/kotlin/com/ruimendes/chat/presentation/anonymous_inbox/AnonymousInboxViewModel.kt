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
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.time.Instant

class AnonymousInboxViewModel(
    private val anonymousMessageRepository: AnonymousMessageRepository,
    private val connectionClient: ChatConnectionClient
) : ViewModel() {

    private val eventChannel = Channel<AnonymousInboxEvent>()
    val events = eventChannel.receiveAsFlow()

    private val paginator: Paginator<String?, AnonymousMessage> = Paginator(
        initialKey = null,
        onLoadUpdated = { isLoading ->
            _state.update { it.copy(isPaginationLoading = isLoading) }
        },
        onRequest = { beforeTimestamp ->
            val instant = beforeTimestamp?.let { Instant.parse(it) }
            anonymousMessageRepository.fetchMessages(before = instant, pageSize = 30)
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

    private val _state = MutableStateFlow(AnonymousInboxState())

    val state = combine(
        _state,
        anonymousMessageRepository.getMessages()
    ) { currentState, messages ->
        currentState.copy(messages = messages.toAnonymousUIList())
    }
        .onEach { /* side-effects handled separately */ }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000L),
            initialValue = AnonymousInboxState()
        )

    init {
        observeConnectionState()
        observeNewMessages()
    }

    fun onAction(action: AnonymousInboxAction) {
        when (action) {
            AnonymousInboxAction.OnScrollToTop -> onScrollToTop()
            AnonymousInboxAction.OnRetryPaginationClick -> retryPagination()
            is AnonymousInboxAction.OnFirstVisibleIndexChanged -> updateNearBottom(action.index)
            is AnonymousInboxAction.OnTopVisibleIndexChanged -> updateBanner(action.topVisibleIndex)
            AnonymousInboxAction.OnHideBanner -> hideBanner()
        }
    }

    private fun updateNearBottom(firstVisibleIndex: Int) {
        _state.update {
            it.copy(isNearBottom = firstVisibleIndex <= 3)
        }
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
        if (messages.isEmpty() || index < 0 || index >= messages.size) {
            return null
        }

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

    private fun retryPagination() {
        loadNextItems()
    }

    private fun onScrollToTop() {
        loadNextItems()
    }

    private fun loadNextItems() {
        viewModelScope.launch {
            paginator.loadNextItems()
        }
    }

    private fun observeConnectionState() {
        connectionClient
            .connectionState
            .onEach { connectionState ->
                if (connectionState == ConnectionState.CONNECTED) {
                    paginator.loadNextItems()
                }
                _state.update {
                    it.copy(connectionState = connectionState)
                }
            }
            .launchIn(viewModelScope)
    }

    private fun observeNewMessages() {
        val currentMessages = state
            .map { it.messages }
            .distinctUntilChanged()

        val latestMessage = anonymousMessageRepository.getLatestMessage()
        val isNearBottom = state.map { it.isNearBottom }.distinctUntilChanged()

        combine(
            currentMessages,
            latestMessage,
            isNearBottom
        ) { messages, latest, nearBottom ->
            val lastCurrentId = messages.filterIsInstance<MessageUI.OtherUserMessage>().firstOrNull()?.id
            if (latest != null && latest.id != lastCurrentId && nearBottom) {
                eventChannel.send(AnonymousInboxEvent.OnNewMessage)
            }
        }.launchIn(viewModelScope)
    }
}
