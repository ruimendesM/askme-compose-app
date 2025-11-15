package com.ruimendes.chat.presentation.chat_list

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ruimendes.core.designsystem.theme.AppTheme
import kotlinx.serialization.Serializable

@Composable
fun ChatListRoot(
    viewModel: ChatListViewModel = viewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    ChatListScreen(
        state = state,
        onAction = viewModel::onAction
    )
}

@Composable
fun ChatListScreen(
    state: ChatListState,
    onAction: (ChatListAction) -> Unit,
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text("Chat list screen")
    }
}

//@Preview
//@Composable
//private fun Preview() {
//    AppTheme {
//        ChatListScreen(
//            state = ChatListState(),
//            onAction = {}
//        )
//    }
//}
@Serializable
data object ChatListRoute