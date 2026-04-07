package com.ruimendes.chat.presentation.anonymous_inbox

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.backhandler.BackHandler
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ruimendes.chat.presentation.chat_detail.MessageBannerListener
import com.ruimendes.chat.presentation.chat_detail.PaginationScrollListener
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
        isDetailPresent = isDetailPresent,
        snackbarState = snackbarState,
        onAction = { action ->
            viewModel.onAction(action)
        }
    )
}

@Composable
fun AnonymousInboxScreen(
    state: AnonymousInboxState,
    isDetailPresent: Boolean,
    snackbarState: SnackbarHostState,
    onAction: (AnonymousInboxAction) -> Unit,
) {
    val messageListState = rememberLazyListState()

    val realMessageItemCount = remember(state.messages) {
        state.messages
            .filter {
                it is MessageUI.OtherUserMessage
            }
            .size
    }

    LaunchedEffect(messageListState) {
        snapshotFlow {
            messageListState.firstVisibleItemIndex to messageListState.layoutInfo.totalItemsCount
        }.filter { (firstVisibleIndex, totalItemsCount) ->
            firstVisibleIndex >= 0 && totalItemsCount > 0
        }.collect { (firstVisibleIndex, _) ->
            onAction(AnonymousInboxAction.OnFirstVisibleIndexChanged(firstVisibleIndex))
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

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        snackbarHost = {
            SnackbarHost(snackbarState)
        },
        contentWindowInsets = WindowInsets.safeDrawing,
        containerColor = MaterialTheme.colorScheme.surface
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .padding(innerPadding)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                ChatHeader {
                    Text(
                        text = "ADMIN",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                MessageList(
                    messages = state.messages,
                    messageWithOpenMenu = null,
                    listState = messageListState,
                    isPaginationLoading = state.isPaginationLoading,
                    paginationError = state.paginationError?.asString(),
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

                Text(
                    text = "Messages are read-only",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(vertical = 12.dp)
                )
            }

            AnimatedVisibility(
                visible = state.bannerState.isVisible,
                enter = fadeIn(),
                exit = fadeOut(),
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 80.dp + 16.dp)
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
