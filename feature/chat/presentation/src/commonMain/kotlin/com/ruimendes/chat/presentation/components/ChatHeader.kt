package com.ruimendes.chat.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.ruimendes.core.designsystem.components.brand.AppHorizontalDivider
import com.ruimendes.core.designsystem.theme.AppTheme
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun ChatHeader(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit = {}
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(max = 80.dp)
                .padding(
                    vertical = 20.dp,
                    horizontal = 16.dp
                ),
            contentAlignment = Alignment.Center
        ) {
            content()
        }
        AppHorizontalDivider()
    }
}

@Composable
@Preview
fun ChatHeaderPreview() {
    AppTheme {
        ChatHeader {
            Text(
                text = "Chat Header",
                modifier = Modifier.background(MaterialTheme.colorScheme.errorContainer)
            )

        }
    }
}