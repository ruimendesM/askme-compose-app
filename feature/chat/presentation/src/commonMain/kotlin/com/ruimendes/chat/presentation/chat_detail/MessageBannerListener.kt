package com.ruimendes.chat.presentation.chat_detail

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import com.ruimendes.chat.presentation.model.MessageUI
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.distinctUntilChanged

@Composable
fun MessageBannerListener(
    lazyListState: LazyListState,
    messages: List<MessageUI>,
    isBannerVisible: Boolean,
    onShowBanner: (topVisibleItemIndex: Int) -> Unit,
    onHide: () -> Unit
) {

    val isBannerVisibleUpdated by rememberUpdatedState(isBannerVisible)

    LaunchedEffect(lazyListState, messages) {
        snapshotFlow {
            val info = lazyListState.layoutInfo
            val visibleItems = info.visibleItemsInfo
            val total = info.totalItemsCount

            val oldestVisibleMessageIndex = visibleItems.maxOfOrNull { it.index } ?: -1
            val isAtTop = oldestVisibleMessageIndex >= total - 1
            val isAtBottom = visibleItems.any { it.index == 0 }

            MessageBannerScrollState(
                oldestVisibleMessageIndex = oldestVisibleMessageIndex,
                isScrollInProgress = lazyListState.isScrollInProgress,
                isAtEdgeOfList = isAtTop || isAtBottom
            )
        }
            .distinctUntilChanged()
            .collect { (oldestVisbileMessageIndex, isScrollInProgress, isAtEdgeOfList) ->
                val shouldShowBanner = isScrollInProgress &&
                        !isAtEdgeOfList && oldestVisbileMessageIndex >= 0

                when {
                    shouldShowBanner -> onShowBanner(oldestVisbileMessageIndex)
                    !shouldShowBanner && isBannerVisibleUpdated -> {
                        delay(1000)
                        onHide()
                    }
                }
            }
    }
}

data class MessageBannerScrollState(
    val oldestVisibleMessageIndex: Int,
    val isScrollInProgress: Boolean,
    val isAtEdgeOfList: Boolean
)