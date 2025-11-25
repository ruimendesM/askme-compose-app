package com.ruimendes.chat.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import askme.feature.chat.presentation.generated.resources.Res
import askme.feature.chat.presentation.generated.resources.add
import askme.feature.chat.presentation.generated.resources.email_or_username
import com.ruimendes.core.designsystem.components.buttons.AppButton
import com.ruimendes.core.designsystem.components.buttons.AppButtonStyle
import com.ruimendes.core.designsystem.components.textfields.AppTextField
import com.ruimendes.core.presentation.util.UiText
import org.jetbrains.compose.resources.stringResource

@Composable
fun ChatParticipantSearchTextSection(
    queryState: TextFieldState,
    onAddClick: () -> Unit,
    isSearchEnabled: Boolean,
    isLoading: Boolean,
    modifier: Modifier = Modifier,
    error: UiText? = null,
    onFocusChanged: (Boolean) -> Unit
) {
    Row(
        modifier = modifier
            .padding(horizontal = 20.dp, vertical = 16.dp),
        verticalAlignment = Alignment.Top,
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        AppTextField(
            state = queryState,
            modifier = Modifier.weight(1f),
            placeholder = stringResource(Res.string.email_or_username),
            title = null,
            supportingText = error?.asString(),
            isError = error != null,
            singleLine = true,
            keyboardType = KeyboardType.Email,
            onFocusChanged = onFocusChanged
        )

        AppButton(
            text = stringResource(Res.string.add),
            onClick = onAddClick,
            style = AppButtonStyle.SECONDARY,
            enabled = isSearchEnabled,
            isLoading = isLoading
        )
    }
}