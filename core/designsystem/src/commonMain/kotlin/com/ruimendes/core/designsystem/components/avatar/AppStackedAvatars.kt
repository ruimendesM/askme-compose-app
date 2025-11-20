package com.ruimendes.core.designsystem.components.avatar

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.ruimendes.core.designsystem.theme.AppTheme
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun AppStackedAvatars(
    avatars: List<AvatarUi>,
    modifier: Modifier = Modifier,
    size: AvatarSize = AvatarSize.SMALL,
    maxVisible: Int = 2,
    overlapPercent: Float = 0.4f
) {
    val overlapOffset = -(size.dp * overlapPercent)

    val visibleAvatars = avatars.take(maxVisible)
    val remainingCount = (avatars.size - maxVisible).coerceAtLeast(0)

    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(overlapOffset),
        verticalAlignment = Alignment.CenterVertically
    ) {
        visibleAvatars.forEach { avatarUi ->
            AppAvatarPhoto(
                displayText = avatarUi.initials,
                size = size,
                imageUrl = avatarUi.imageUrl
            )
        }

        if (remainingCount > 0) {
            AppAvatarPhoto(
                displayText = "$remainingCount+",
                size = size,
                textColor = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
@Preview
fun AppStackedAvatarsPreview() {
    AppTheme {
        AppStackedAvatars(
            avatars = listOf(
                AvatarUi(
                    id = "1",
                    username = "Rui",
                    initials = "RM"
                ),
                AvatarUi(
                    id = "3",
                    username = "John",
                    initials = "JW"
                ),
                AvatarUi(
                    id = "2",
                    username = "Sabrina",
                    initials = "SA"
                ),
                AvatarUi(
                    id = "4",
                    username = "Amanda",
                    initials = "AN"
                ),
            )
        )
    }
}