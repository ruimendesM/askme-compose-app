package com.ruimendes.chat.presentation.util

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.ruimendes.core.designsystem.theme.extended

@Composable
fun getChatBubbleColorForUser(userId: String): Color {
    val colorPool = with(MaterialTheme.colorScheme.extended) {
        listOf(
            cakeRed,
            cakeOrange,
            cakeYellow,
            cakeGreen,
            cakeBlue,
            cakePurple,
            cakePink,
            cakeTeal,
            cakeMint
        )
    }

    val index = userId.hashCode().toUInt() % colorPool.size.toUInt()

    return colorPool[index.toInt()]
}