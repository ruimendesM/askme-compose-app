package com.ruimendes.chat.presentation.chat_detail.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.ruimendes.chat.domain.models.ChatMessageDeliveryStatus
import com.ruimendes.chat.presentation.model.MessageUI
import com.ruimendes.chat.presentation.util.getChatBubbleColorForUser
import com.ruimendes.core.designsystem.components.avatar.ChatParticipantUI
import com.ruimendes.core.designsystem.theme.AppTheme
import com.ruimendes.core.designsystem.theme.extended
import com.ruimendes.core.presentation.util.UiText
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun MessageListItem(
    messageUI: MessageUI,
    messageWithOpenMenu: MessageUI.LocalUserMessage?,
    onMessageLongClick: (MessageUI.LocalUserMessage) -> Unit,
    onDismissMessageMenu: () -> Unit,
    onDeleteClick: (MessageUI.LocalUserMessage) -> Unit,
    onRetryClick: (MessageUI.LocalUserMessage) -> Unit,
    modifier: Modifier = Modifier
) {

    Box(
        modifier = modifier
    ) {
        when (messageUI) {
            is MessageUI.DateSeparator -> {
                DateSeparatorItem(
                    date = messageUI.date.asString(),
                    modifier = Modifier.fillMaxWidth()
                )
            }

            is MessageUI.LocalUserMessage -> {
                LocalUserMessageItem(
                    message = messageUI,
                    messageWithOpenMenu = messageWithOpenMenu,
                    onMessageLongClick = { onMessageLongClick(messageUI) },
                    onDismissMessageMenu = onDismissMessageMenu,
                    onDeleteClick = { onDeleteClick(messageUI) },
                    onRetryClick = { onRetryClick(messageUI) }
                )
            }

            is MessageUI.OtherUserMessage -> {
                OtherUserMessageItem(
                    message = messageUI,
                    color = getChatBubbleColorForUser(messageUI.sender.id),
                )
            }
        }
    }
}

@Composable
private fun DateSeparatorItem(
    date: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        HorizontalDivider(modifier = Modifier.weight(1f))
        Text(
            text = date,
            modifier = Modifier.padding(horizontal = 40.dp),
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.extended.textPlaceholder
        )
        HorizontalDivider(modifier = Modifier.weight(1f))
    }
}

@Composable
@Preview
fun MessageListItemLocalMessageUIPreview() {
    AppTheme {
        MessageListItem(
            messageUI = MessageUI.LocalUserMessage(
                id = "1",
                content = "Hello! This is a very long message that should be displayed in the chat. I hope it looks good!",
                deliveryStatus = ChatMessageDeliveryStatus.SENT,
                formattedSentTime = UiText.DynamicString("Friday 3:45pm")
            ),
            messageWithOpenMenu = MessageUI.LocalUserMessage(
                id = "1",
                content = "Hello! This is a very long message that should be displayed in the chat. I hope it looks good!",
                deliveryStatus = ChatMessageDeliveryStatus.SENT,
                formattedSentTime = UiText.DynamicString("Friday 3:45pm")
            ),
            onMessageLongClick = {},
            onDismissMessageMenu = {},
            onDeleteClick = {},
            onRetryClick = {},
            modifier = Modifier
                .fillMaxWidth()
                .height(250.dp)
        )
    }
}

@Composable
@Preview
fun MessageListItemLocalMessageFailedUIPreview() {
    AppTheme {
        MessageListItem(
            messageUI = MessageUI.LocalUserMessage(
                id = "1",
                content = "Hello! This is a very long message that should be displayed in the chat. I hope it looks good!",
                deliveryStatus = ChatMessageDeliveryStatus.FAILED,
                formattedSentTime = UiText.DynamicString("Friday 3:45pm")
            ),
            messageWithOpenMenu = null,
            onMessageLongClick = {},
            onDismissMessageMenu = {},
            onDeleteClick = {},
            onRetryClick = {},
            modifier = Modifier
                .fillMaxWidth()
        )
    }
}

@Composable
@Preview
fun MessageListItemOtherMessageUIPreview() {
    AppTheme {
        MessageListItem(
            messageUI = MessageUI.OtherUserMessage(
                id = "1",
                content = "Hello! This is a very long message that should be displayed in the chat. I hope it looks good!",
                formattedSentTime = UiText.DynamicString("Friday 3:45pm"),
                sender = ChatParticipantUI(
                    id = "1",
                    username = "Rui Mendes",
                    initials = "RM",
                    imageUrl = null
                )
            ),
            messageWithOpenMenu = null,
            onMessageLongClick = {},
            onDismissMessageMenu = {},
            onDeleteClick = {},
            onRetryClick = {},
            modifier = Modifier
                .fillMaxWidth()
                .height(250.dp)
        )
    }
}