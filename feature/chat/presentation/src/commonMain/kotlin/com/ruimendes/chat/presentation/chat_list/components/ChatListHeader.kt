package com.ruimendes.chat.presentation.chat_list.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import askme.core.designsystem.generated.resources.logout_icon
import askme.core.designsystem.generated.resources.settings_icon
import askme.core.designsystem.generated.resources.Res as DesignRes
import askme.feature.chat.presentation.generated.resources.Res
import askme.feature.chat.presentation.generated.resources.app_name
import askme.feature.chat.presentation.generated.resources.logout
import askme.feature.chat.presentation.generated.resources.profile_settings
import com.ruimendes.chat.presentation.components.ChatHeader
import com.ruimendes.core.designsystem.components.avatar.AppAvatarPhoto
import com.ruimendes.core.designsystem.components.avatar.ChatParticipantUI
import com.ruimendes.core.designsystem.components.brand.AppBrandLogo
import com.ruimendes.core.designsystem.components.brand.AppHorizontalDivider
import com.ruimendes.core.designsystem.theme.AppTheme
import com.ruimendes.core.designsystem.theme.extended
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun ChatListHeader(
    localParticipant: ChatParticipantUI?,
    isUserMenuOpen: Boolean,
    onUserAvatarClick: () -> Unit,
    onDismissMenu: () -> Unit,
    onProfileSettingsClick: () -> Unit,
    onLogoutClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    ChatHeader(
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            AppBrandLogo(tint = MaterialTheme.colorScheme.tertiary)

            Text(
                text = stringResource(Res.string.app_name),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.extended.textPrimary
            )
            Spacer(modifier = Modifier.weight(1f))
            ProfileAvatarSection(
                localParticipant = localParticipant,
                isMenuOpen = isUserMenuOpen,
                onClick = onUserAvatarClick,
                onDismissMenu = onDismissMenu,
                onProfileSettingsClick = onProfileSettingsClick,
                onLogoutClick = onLogoutClick
            )
        }
    }
}

@Composable
fun ProfileAvatarSection(
    localParticipant: ChatParticipantUI?,
    isMenuOpen: Boolean,
    onClick: () -> Unit,
    onDismissMenu: () -> Unit,
    onProfileSettingsClick: () -> Unit,
    onLogoutClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
    ) {
        if (localParticipant != null) {
            AppAvatarPhoto(
                displayText = localParticipant.initials,
                imageUrl = localParticipant.imageUrl,
                onClick = onClick
            )
        }

        DropdownMenu(
            expanded = isMenuOpen,
            shape = RoundedCornerShape(16.dp),
            onDismissRequest = onDismissMenu,
            containerColor = MaterialTheme.colorScheme.surface,
            border = BorderStroke(
                width = 1.dp,
                color = MaterialTheme.colorScheme.extended.surfaceOutline
            )
        ) {
            DropdownMenuItem(
                text = {
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        Icon(
                            imageVector = vectorResource(DesignRes.drawable.settings_icon),
                            contentDescription = stringResource(Res.string.profile_settings),
                            tint = MaterialTheme.colorScheme.extended.textSecondary,
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            text = stringResource(Res.string.profile_settings),
                            color = MaterialTheme.colorScheme.extended.textSecondary,
                            fontWeight = FontWeight.Medium
                        )
                    }
                },
                onClick = {
                    onDismissMenu()
                    onProfileSettingsClick()
                }
            )
            AppHorizontalDivider()
            DropdownMenuItem(
                text = {
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        Icon(
                            imageVector = vectorResource(DesignRes.drawable.logout_icon),
                            contentDescription = stringResource(Res.string.logout),
                            tint = MaterialTheme.colorScheme.extended.destructiveHover,
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            text = stringResource(Res.string.logout),
                            color = MaterialTheme.colorScheme.extended.destructiveHover,
                            fontWeight = FontWeight.Medium
                        )
                    }
                },
                onClick = {
                    onDismissMenu()
                    onLogoutClick()
                }
            )
        }
    }
}

@Composable
@Preview(showBackground = true)
fun ChatListHeaderPreview() {
    AppTheme {
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            ChatListHeader(
                localParticipant = ChatParticipantUI(
                    id = "1",
                    username = "John Doe",
                    initials = "RM",
                ),
                isUserMenuOpen = true,
                onUserAvatarClick = {},
                onDismissMenu = {},
                onProfileSettingsClick = {},
                onLogoutClick = {},
                modifier = Modifier
            )
        }
    }
}