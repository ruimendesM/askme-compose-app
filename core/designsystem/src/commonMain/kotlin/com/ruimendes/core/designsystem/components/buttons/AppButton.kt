package com.ruimendes.core.designsystem.components.buttons

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.ruimendes.core.designsystem.theme.AppTheme
import com.ruimendes.core.designsystem.theme.extended
import org.jetbrains.compose.ui.tooling.preview.Preview

enum class AppButtonStyle {
    PRIMARY,
    DESTRUCTIVE_PRIMARY,
    SECONDARY,
    DESTRUCTIVE_SECONDARY,
    TEXT
}

@Composable
fun AppButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    style: AppButtonStyle = AppButtonStyle.PRIMARY,
    enabled: Boolean = true,
    isLoading: Boolean = false,
    leadingIcon: @Composable (() -> Unit)? = null,
) {
    val colors = when(style) {
        AppButtonStyle.PRIMARY -> {
            ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                disabledContentColor = MaterialTheme.colorScheme.extended.disabledFill,
                disabledContainerColor = MaterialTheme.colorScheme.extended.textDisabled
            )
        }
        AppButtonStyle.DESTRUCTIVE_PRIMARY -> {
            ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.error,
                contentColor = MaterialTheme.colorScheme.onError,
                disabledContentColor = MaterialTheme.colorScheme.extended.disabledFill,
                disabledContainerColor = MaterialTheme.colorScheme.extended.textDisabled
            )
        }
        AppButtonStyle.SECONDARY -> {
            ButtonDefaults.buttonColors(
                containerColor = Color.Transparent,
                contentColor = MaterialTheme.colorScheme.extended.textSecondary,
                disabledContentColor = MaterialTheme.colorScheme.extended.textDisabled,
                disabledContainerColor = Color.Transparent
            )
        }
        AppButtonStyle.DESTRUCTIVE_SECONDARY -> {
            ButtonDefaults.buttonColors(
                containerColor = Color.Transparent,
                contentColor = MaterialTheme.colorScheme.error,
                disabledContentColor = Color.Transparent,
                disabledContainerColor = MaterialTheme.colorScheme.extended.textDisabled
            )
        }
        AppButtonStyle.TEXT -> {
            ButtonDefaults.buttonColors(
                containerColor = Color.Transparent,
                contentColor = MaterialTheme.colorScheme.tertiary,
                disabledContentColor = Color.Transparent,
                disabledContainerColor = MaterialTheme.colorScheme.extended.textDisabled
            )
        }
    }

    val defaultBorderStroke = BorderStroke(
        width = 1.dp,
        color = MaterialTheme.colorScheme.extended.disabledOutline
    )
    val border = when {
        style == AppButtonStyle.PRIMARY && !enabled -> {
            defaultBorderStroke
        }
        style == AppButtonStyle.SECONDARY -> {
            defaultBorderStroke
        }
        style == AppButtonStyle.DESTRUCTIVE_PRIMARY && !enabled -> {
            defaultBorderStroke
        }
        style == AppButtonStyle.DESTRUCTIVE_SECONDARY -> {
            val borderColor = if (enabled) {
                MaterialTheme.colorScheme.extended.destructiveSecondaryOutline
            } else {
                MaterialTheme.colorScheme.extended.disabledOutline
            }
            BorderStroke(
                width = 1.dp,
                color = borderColor
            )
        }
        else -> null
    }

    Button(
        onClick = onClick,
        modifier = modifier,
        enabled = enabled,
        shape = RoundedCornerShape(8.dp),
        colors = colors,
        border = border
    ) {
        Box(contentAlignment = Alignment.Center,
            modifier = Modifier.padding(6.dp)) {
            CircularProgressIndicator(
                modifier = Modifier
                    .size(15.dp)
                    .alpha(if (isLoading) 1.0f else 0f),
                strokeWidth = 1.5.dp,
                color = Color.Black
            )
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.alpha(
                    if(isLoading) 0f else 1f
                )
            ) {
                leadingIcon?.let {
                    it.invoke()
                    Spacer(modifier = Modifier.width(8.dp))
                }
                Text(
                    text = text,
                    style = MaterialTheme.typography.titleSmall
                )
            }
        }
    }
}

@Composable
@Preview
fun AppPrimaryButtonPreview() {
    AppTheme {
        AppButton(
            text = "Primary Button",
            onClick = {},
            style = AppButtonStyle.PRIMARY
        )
    }
}

@Composable
@Preview
fun AppSecondaryButtonPreview() {
    AppTheme {
        AppButton(
            text = "Secondary Button",
            onClick = {},
            style = AppButtonStyle.SECONDARY,
        )
    }
}

@Composable
@Preview
fun AppDestructivePrimaryButtonPreview() {
    AppTheme {
        AppButton(
            text = "Primary Destructive",
            onClick = {},
            style = AppButtonStyle.DESTRUCTIVE_PRIMARY
        )
    }
}

@Composable
@Preview
fun AppDestructiveSecondaryButtonPreview() {
    AppTheme {
        AppButton(
            text = "Secondary Destructive",
            onClick = {},
            style = AppButtonStyle.DESTRUCTIVE_SECONDARY
        )
    }
}

@Composable
@Preview
fun AppTextButtonPreview() {
    AppTheme {
        AppButton(
            text = "Text Button",
            onClick = {},
            style = AppButtonStyle.TEXT
        )
    }
}
