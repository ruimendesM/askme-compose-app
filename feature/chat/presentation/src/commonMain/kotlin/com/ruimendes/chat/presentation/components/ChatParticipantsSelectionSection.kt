package com.ruimendes.chat.presentation.components

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.ruimendes.core.designsystem.components.avatar.AppAvatarPhoto
import com.ruimendes.core.designsystem.components.avatar.ChatParticipantUI
import com.ruimendes.core.designsystem.theme.extended
import com.ruimendes.core.designsystem.theme.titleXSmall
import com.ruimendes.core.presentation.util.DeviceConfiguration
import com.ruimendes.core.presentation.util.currentDeviceConfiguration

@Composable
fun ColumnScope.ChatParticipantsSelectionSection(
    selectedParticipants: List<ChatParticipantUI>,
    modifier: Modifier = Modifier,
    searchResult: ChatParticipantUI? = null
) {
    val deviceConfiguration = currentDeviceConfiguration()
    val rootHeightModifier = when (deviceConfiguration) {
        DeviceConfiguration.TABLET_PORTRAIT,
        DeviceConfiguration.TABLET_LANDSCAPE,
        DeviceConfiguration.DESKTOP -> {
            Modifier.animateContentSize()
                .heightIn(min = 200.dp, max = 300.dp)
        }

        else -> {
            Modifier
                .weight(1f)
        }
    }
    Box(
        modifier = rootHeightModifier
            .then(modifier)
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxWidth()
        ) {
            searchResult?.let {
                item {
                    ChatParticipantListItem(
                        participant = it,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
            if (selectedParticipants.isNotEmpty() && searchResult == null) {
                items(items = selectedParticipants, key = { it.id }) { participant ->
                    ChatParticipantListItem(
                        participant = participant,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}

@Composable
fun ChatParticipantListItem(
    participant: ChatParticipantUI,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        AppAvatarPhoto(
            displayText = participant.initials,
            imageUrl = participant.imageUrl,
        )
        Text(
            text = participant.username,
            style = MaterialTheme.typography.titleXSmall,
            maxLines = 1,
            color = MaterialTheme.colorScheme.extended.textPrimary,
            overflow = TextOverflow.Ellipsis
        )
    }
}