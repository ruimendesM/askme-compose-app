package com.ruimendes.chat.presentation.chat_detail.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.ruimendes.chat.presentation.model.MessageUI
import com.ruimendes.core.designsystem.components.avatar.AppAvatarPhoto
import com.ruimendes.core.designsystem.components.chat.AppChatBubble
import com.ruimendes.core.designsystem.components.chat.TrianglePosition

@Composable
fun OtherUserMessageItem(
    message: MessageUI.OtherUserMessage,
    color: Color,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth(),
        verticalAlignment = Alignment.Bottom,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        AppAvatarPhoto(
            displayText = message.sender.initials,
            imageUrl = message.sender.imageUrl
        )
        AppChatBubble(
            messageContent = message.content,
            sender = message.sender.username,
            trianglePosition = TrianglePosition.LEFT,
            formattedDateTime = message.formattedSentTime.asString(),
            color = color
        )
    }
    
}