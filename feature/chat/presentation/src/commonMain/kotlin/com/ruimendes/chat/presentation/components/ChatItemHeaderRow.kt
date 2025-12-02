package com.ruimendes.chat.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import askme.feature.chat.presentation.generated.resources.Res
import askme.feature.chat.presentation.generated.resources.group_chat
import askme.feature.chat.presentation.generated.resources.username_you
import com.ruimendes.chat.presentation.model.ChatUI
import com.ruimendes.core.designsystem.components.avatar.AppStackedAvatars
import com.ruimendes.core.designsystem.theme.extended
import com.ruimendes.core.designsystem.theme.titleXSmall
import org.jetbrains.compose.resources.stringResource

@Composable
fun ChatItemHeaderRow(
    chat: ChatUI,
    isGroupChat: Boolean,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        AppStackedAvatars(avatars = chat.otherParticipants)
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(
                text = if (!isGroupChat) {
                    chat.otherParticipants.first().username
                } else {
                    stringResource(Res.string.group_chat)
                },
                style = MaterialTheme.typography.titleXSmall,
                color = MaterialTheme.colorScheme.extended.textPrimary,
                overflow = TextOverflow.Ellipsis,
                maxLines = 1,
                modifier = Modifier.fillMaxWidth()
            )
            if (isGroupChat) {
                val youString = stringResource(Res.string.username_you)
                val formattedUsernames = remember(chat.otherParticipants) {
                    mutableListOf(youString).apply {
                        addAll(chat.otherParticipants.map { it.username })
                    }.joinToString()
                }
                Text(
                    text = formattedUsernames,
                    color = MaterialTheme.colorScheme.extended.textPlaceholder,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}