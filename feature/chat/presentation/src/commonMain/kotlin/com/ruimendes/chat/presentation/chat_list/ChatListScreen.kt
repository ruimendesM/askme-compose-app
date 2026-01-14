package com.ruimendes.chat.presentation.chat_list

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import askme.feature.chat.presentation.generated.resources.Res
import askme.feature.chat.presentation.generated.resources.cancel
import askme.feature.chat.presentation.generated.resources.create_chat
import askme.feature.chat.presentation.generated.resources.logout
import askme.feature.chat.presentation.generated.resources.logout_confirmation_description
import askme.feature.chat.presentation.generated.resources.logout_confirmation_title
import askme.feature.chat.presentation.generated.resources.no_chats
import askme.feature.chat.presentation.generated.resources.no_chats_subtitle
import com.ruimendes.chat.presentation.chat_list.components.ChatListHeader
import com.ruimendes.chat.presentation.chat_list.components.ChatListItemUI
import com.ruimendes.chat.presentation.components.EmptyListSection
import com.ruimendes.core.designsystem.components.brand.AppHorizontalDivider
import com.ruimendes.core.designsystem.components.buttons.AppFloatingActionButton
import com.ruimendes.core.designsystem.components.dialogs.DestructiveConfirmationDialog
import com.ruimendes.core.designsystem.theme.AppTheme
import com.ruimendes.core.designsystem.theme.extended
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun ChatListRoot(
    selectedChatId: String?,
    onChatClick: (String?) -> Unit,
    onConfirmLogoutClick: () -> Unit,
    onCreateChatClick: () -> Unit,
    onProfileSettingsClick: () -> Unit,
    viewModel: ChatListViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(selectedChatId) {
        viewModel.onAction(ChatListAction.OnSelectChat(selectedChatId))
    }

    ChatListScreen(
        state = state,
        onAction = { action ->
            when(action) {
                is ChatListAction.OnSelectChat -> onChatClick(action.chatId)
                ChatListAction.OnConfirmLogout -> onConfirmLogoutClick()
                ChatListAction.OnCreateChatClick -> onCreateChatClick()
                ChatListAction.OnProfileSettingsClick -> onProfileSettingsClick()
                else -> Unit
            }
            viewModel.onAction(action)
        },
        snackbarHostState = snackbarHostState
    )
}

@Composable
fun ChatListScreen(
    state: ChatListState,
    onAction: (ChatListAction) -> Unit,
    snackbarHostState: SnackbarHostState
) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.extended.surfaceLower,
        contentWindowInsets = WindowInsets.safeDrawing,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        floatingActionButton = {
            AppFloatingActionButton(
                onClick = {
                    onAction(ChatListAction.OnCreateChatClick)
                }
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = stringResource(Res.string.create_chat)
                )
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            ChatListHeader(
                localParticipant = state.localParticipant,
                isUserMenuOpen = state.isUserMenuOpen,
                onUserAvatarClick = {
                    onAction(ChatListAction.OnUserAvatarClick)
                },
                onLogoutClick = {
                    onAction(ChatListAction.OnLogoutClick)
                },
                onDismissMenu = {
                    onAction(ChatListAction.OnDismissUserMenu)
                },
                onProfileSettingsClick = {
                    onAction(ChatListAction.OnProfileSettingsClick)
                }
            )
            when {
                state.isLoading -> {
                    CircularProgressIndicator(
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                state.chats.isEmpty() -> {
                    EmptyListSection(
                        title = stringResource(Res.string.no_chats),
                        description = stringResource(Res.string.no_chats_subtitle),
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp)
                    )
                }

                else -> {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                    ) {
                        items(state.chats, key = { it.id }) { chat ->
                            ChatListItemUI(
                                chat = chat,
                                isSelected = chat.id == state.selectedChatId,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        onAction(ChatListAction.OnSelectChat(chat.id))
                                    }
                            )
                            AppHorizontalDivider()
                        }
                    }
                }
            }
        }
    }

    if (state.showLogoutConfirmation) {
        DestructiveConfirmationDialog(
            title = stringResource(Res.string.logout_confirmation_title),
            description = stringResource(Res.string.logout_confirmation_description),
            confirmButtonText = stringResource(Res.string.logout),
            cancelButtonText = stringResource(Res.string.cancel),
            onCancelClick = {
                onAction(ChatListAction.OnDismissLogoutDialog)
            },
            onConfirmClick = {
                onAction(ChatListAction.OnConfirmLogout)
            },
            onDismiss = {
                onAction(ChatListAction.OnDismissLogoutDialog)
            }
        )
    }
}

@Composable
@Preview
private fun LightPreview() {
    Preview(darkTheme = false)
}

@Composable
@Preview
private fun DarkPreview() {
    Preview(darkTheme = true)
}

@Composable
@Preview(widthDp = 840, heightDp = 481)
private fun LightMobilePreview() {
    Preview(darkTheme = false)
}

@Composable
@Preview(widthDp = 840, heightDp = 481)
private fun DarkMobilePreview() {
    Preview(darkTheme = true)
}

@Composable
@Preview(widthDp = 1000, heightDp = 600)
private fun LightTabletPreview() {
    Preview(darkTheme = false)
}

@Composable
@Preview(widthDp = 1000, heightDp = 600)
private fun DarkTabletPreview() {
    Preview(darkTheme = true)
}

@Preview
@Composable
private fun Preview(darkTheme: Boolean) {
    AppTheme(darkTheme = darkTheme) {
        ChatListScreen(
            state = ChatListState(),
            onAction = {},
            snackbarHostState = remember { SnackbarHostState() }
        )
    }
}