package com.ruimendes.chat.presentation.create_chat

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import askme.feature.chat.presentation.generated.resources.Res
import askme.feature.chat.presentation.generated.resources.cancel
import askme.feature.chat.presentation.generated.resources.create_chat
import com.ruimendes.chat.domain.models.Chat
import com.ruimendes.chat.presentation.components.ChatParticipantSearchTextSection
import com.ruimendes.chat.presentation.components.ChatParticipantsSelectionSection
import com.ruimendes.chat.presentation.components.ManageChatButtonSection
import com.ruimendes.chat.presentation.components.ManageChatHeaderRow
import com.ruimendes.core.designsystem.components.brand.AppHorizontalDivider
import com.ruimendes.core.designsystem.components.buttons.AppButton
import com.ruimendes.core.designsystem.components.buttons.AppButtonStyle
import com.ruimendes.core.designsystem.components.dialogs.AppAdaptativeDialogSheetLayout
import com.ruimendes.core.designsystem.theme.AppTheme
import com.ruimendes.core.presentation.util.DeviceConfiguration
import com.ruimendes.core.presentation.util.ObserveAsEvents
import com.ruimendes.core.presentation.util.clearFocusOnTap
import com.ruimendes.core.presentation.util.currentDeviceConfiguration
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun CreateChatRoot(
    onDismiss: () -> Unit,
    onChatCreated: (Chat) -> Unit,
    viewModel: CreateChatViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    ObserveAsEvents(viewModel.events) { event ->
        when (event) {
            is CreateChatEvent.OnChatCreated -> onChatCreated(event.chat)
        }
    }

    AppAdaptativeDialogSheetLayout(
        onDismiss = onDismiss
    ) {
        CreateChatScreen(
            state = state,
            onAction = { action ->
                when (action) {
                    CreateChatAction.OnDismissDialog -> onDismiss()
                    else -> Unit
                }
                viewModel.onAction(action)
            }
        )
    }
}

@Composable
fun CreateChatScreen(
    state: CreateChatState,
    onAction: (CreateChatAction) -> Unit,
) {
    var isTextFieldFocused by remember { mutableStateOf(false) }
    val imeHeight = WindowInsets.ime.getBottom(LocalDensity.current)
    val isKeyboardVisible = imeHeight > 0
    val configuration = currentDeviceConfiguration()

    val shouldHideHeader =
        configuration == DeviceConfiguration.MOBILE_LANDSCAPE ||
                (isKeyboardVisible && configuration != DeviceConfiguration.DESKTOP) ||
                isTextFieldFocused

    Column(
        modifier = Modifier
            .clearFocusOnTap()
            .fillMaxWidth()
            .wrapContentHeight()
            .imePadding()
            .background(MaterialTheme.colorScheme.surface)
            .navigationBarsPadding()
    ) {
        AnimatedVisibility(
            visible = !shouldHideHeader
        ) {
            Column {
                ManageChatHeaderRow(
                    title = stringResource(Res.string.create_chat),
                    onCloseClick = { onAction(CreateChatAction.OnDismissDialog) },
                    modifier = Modifier.fillMaxWidth()
                )

                AppHorizontalDivider()
            }
        }
        ChatParticipantSearchTextSection(
            queryState = state.queryTextState,
            onAddClick = {
                onAction(CreateChatAction.OnAddClick)
            },
            isSearchEnabled = state.canAddParticipant,
            isLoading = state.isSearching,
            modifier = Modifier.fillMaxWidth(),
            error = state.searchError,
            onFocusChanged = {
                isTextFieldFocused = it
            }
        )
        AppHorizontalDivider()
        ChatParticipantsSelectionSection(
            selectedParticipants = state.selectedChatParticipants,
            modifier = Modifier.fillMaxWidth(),
            searchResult = state.currentSearchResult
        )
        AppHorizontalDivider()
        ManageChatButtonSection(
            primaryButton = {
                AppButton(
                    text = stringResource(Res.string.create_chat),
                    onClick = {
                        onAction(CreateChatAction.OnCreateChatClick)
                    },
                    enabled = state.selectedChatParticipants.isNotEmpty(),
                    isLoading = state.isCreatingChat
                )
            },
            secondaryButton = {
                AppButton(
                    text = stringResource(Res.string.cancel),
                    onClick = {
                        onAction(CreateChatAction.OnCreateChatClick)
                    },
                    style = AppButtonStyle.SECONDARY
                )
            },
            error = state.createChatError?.asString(),
            modifier = Modifier.fillMaxWidth()
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
        CreateChatScreen(
            state = CreateChatState(),
            onAction = {}
        )
    }
}