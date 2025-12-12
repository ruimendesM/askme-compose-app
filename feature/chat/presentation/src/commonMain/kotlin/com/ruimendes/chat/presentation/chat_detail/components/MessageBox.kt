package com.ruimendes.chat.presentation.chat_detail.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import askme.feature.chat.presentation.generated.resources.Res
import askme.feature.chat.presentation.generated.resources.cloud_off_icon
import askme.feature.chat.presentation.generated.resources.send
import askme.feature.chat.presentation.generated.resources.send_a_message
import com.ruimendes.chat.domain.models.ConnectionState
import com.ruimendes.chat.presentation.util.toUiText
import com.ruimendes.core.designsystem.components.buttons.AppButton
import com.ruimendes.core.designsystem.components.textfields.AppMultiLineTextField
import com.ruimendes.core.designsystem.theme.AppTheme
import com.ruimendes.core.designsystem.theme.extended
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun MessageBox(
    messageTextFieldState: TextFieldState,
    isTextInputEnabled: Boolean,
    connectionState: ConnectionState,
    onSendClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isConnected = connectionState == ConnectionState.CONNECTED
    AppMultiLineTextField(
        state = messageTextFieldState,
        modifier = modifier,
        placeholder = stringResource(Res.string.send_a_message),
        enabled = isTextInputEnabled,
        keyboardOptions = KeyboardOptions(
            imeAction = ImeAction.Send
        ),
        onKeyboardAction = onSendClick,
        bottomContent = {
            Spacer(modifier = Modifier.weight(1f))
            if (!isConnected) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = vectorResource(Res.drawable.cloud_off_icon),
                        contentDescription = connectionState.toUiText().asString(),
                        modifier = Modifier.size(16.dp),
                        tint = MaterialTheme.colorScheme.extended.textDisabled
                    )
                    Text(
                        text = connectionState.toUiText().asString(),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.extended.textDisabled
                    )
                }
            }
            AppButton(
                text = stringResource(Res.string.send),
                onClick = onSendClick,
                enabled = isConnected && isTextInputEnabled
            )
        }
    )
}

@Composable
@Preview
fun MessageBoxDisconnectedPreview() {
    AppTheme {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(250.dp)
        ) {
            MessageBox(
                messageTextFieldState = TextFieldState(),
                isTextInputEnabled = false,
                connectionState = ConnectionState.DISCONNECTED,
                onSendClick = {},
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
@Preview
fun MessageBoxConnectedPreview() {
    AppTheme {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(250.dp)
        ) {
            MessageBox(
                messageTextFieldState = TextFieldState("Hello! This is a test message!"),
                isTextInputEnabled = true,
                connectionState = ConnectionState.CONNECTED,
                onSendClick = {},
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}