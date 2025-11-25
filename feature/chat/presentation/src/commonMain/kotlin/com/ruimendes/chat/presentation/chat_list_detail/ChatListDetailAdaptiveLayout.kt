package com.ruimendes.chat.presentation.chat_list_detail

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.layout.AnimatedPane
import androidx.compose.material3.adaptive.layout.ListDetailPaneScaffold
import androidx.compose.material3.adaptive.layout.ListDetailPaneScaffoldRole
import androidx.compose.material3.adaptive.navigation.rememberListDetailPaneScaffoldNavigator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.backhandler.BackHandler
import androidx.compose.ui.input.key.Key.Companion.R
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import askme.feature.chat.presentation.generated.resources.Res
import askme.feature.chat.presentation.generated.resources.create_chat
import com.ruimendes.chat.presentation.create_chat.CreateChatRoot
import com.ruimendes.core.designsystem.components.buttons.AppFloatingActionButton
import com.ruimendes.core.designsystem.theme.AppTheme
import com.ruimendes.core.designsystem.theme.extended
import com.ruimendes.core.presentation.util.DialogSheetScopedViewModel
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3AdaptiveApi::class, ExperimentalComposeUiApi::class)
@Composable
fun ChatListDetailAdaptiveLayout(
    chatListDetailViewModel: ChatListDetailViewModel = koinViewModel(),
) {
    val sharedState by chatListDetailViewModel.state.collectAsStateWithLifecycle()
    val scaffoldDirective = createNoSpacingPaneScaffoldDirective()
    val scaffoldNavigator = rememberListDetailPaneScaffoldNavigator(
        scaffoldDirective = scaffoldDirective
    )
    val scope = rememberCoroutineScope()

    BackHandler(enabled = scaffoldNavigator.canNavigateBack()) {
        scope.launch {
            scaffoldNavigator.navigateBack()
        }
    }

    ListDetailPaneScaffold(
        directive = scaffoldDirective,
        value = scaffoldNavigator.scaffoldValue,
        modifier = Modifier.background(MaterialTheme.colorScheme.extended.surfaceLower),
        listPane = {
            AnimatedPane {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    floatingActionButton = {
                        AppFloatingActionButton(
                            onClick = {
                                chatListDetailViewModel.onAction(ChatListDetailAction.OnCreateChatClick)
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = stringResource(Res.string.create_chat)
                            )
                        }
                    }
                ) { innerPadding ->
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = innerPadding
                    ) {
                        items(100) { chatIndex ->
                            Text(
                                text = "Chat $chatIndex",
                                modifier = Modifier
                                    .clickable {
                                        chatListDetailViewModel.onAction(ChatListDetailAction.OnCreateChatClick)
                                        chatListDetailViewModel.onAction(
                                            ChatListDetailAction.OnChatClick(
                                                chatIndex.toString()
                                            )
                                        )
                                        scope.launch {
                                            scaffoldNavigator.navigateTo(ListDetailPaneScaffoldRole.Detail)
                                        }
                                    }
                                    .padding(16.dp)
                            )
                        }
                    }
                }
            }
        },
        detailPane = {
            AnimatedPane {
                Box(
                    modifier = Modifier
                        .fillMaxSize(),
                    contentAlignment = androidx.compose.ui.Alignment.Center
                ) {
                    sharedState.selectedChatId?.let {
                        Text(
                            text = it
                        )
                    }
                }
            }
        }
    )

    DialogSheetScopedViewModel(
        visible = sharedState.dialogState is DialogState.CreateChat,
    ) {
        CreateChatRoot(
            onChatCreated = {
                chatListDetailViewModel.onAction(ChatListDetailAction.OnDismissCurrentDialog)
                chatListDetailViewModel.onAction(ChatListDetailAction.OnChatClick(it.id))
                scope.launch {
                    scaffoldNavigator.navigateTo(ListDetailPaneScaffoldRole.Detail)
                }
            },
            onDismiss = {
                chatListDetailViewModel.onAction(ChatListDetailAction.OnDismissCurrentDialog)
            }
        )
    }
}

@Composable
@Preview
fun ChatListDetailAdaptiveLayoutPreview() {
    AppTheme {
        ChatListDetailAdaptiveLayout()
    }
}