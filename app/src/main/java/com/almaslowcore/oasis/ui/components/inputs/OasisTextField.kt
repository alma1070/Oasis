package com.almaslowcore.oasis.ui.components.inputs

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.InputTransformation
import androidx.compose.foundation.text.input.KeyboardActionHandler
import androidx.compose.foundation.text.input.OutputTransformation
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp

@Composable
fun OasisTextField(
    state: TextFieldState,
    label: String,
    modifier: Modifier = Modifier,
    placeholder: String? = null,
    errorText: String? = null,
    supportingText: String? = null,
    enabled: Boolean = true,
    readOnly: Boolean = false,
    lineLimits: TextFieldLineLimits = TextFieldLineLimits.SingleLine,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    onKeyboardAction: KeyboardActionHandler? = null,
    inputTransformation: InputTransformation? = null,
    outputTransformation: OutputTransformation? = null,
    shape: Shape = RoundedCornerShape(16.dp),
    scrollState: ScrollState = ScrollState(0),
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() }
) {
    val hasError = errorText != null

    OutlinedTextField(
        state = state,
        modifier = modifier,
        enabled = enabled,
        readOnly = readOnly,
        label = {
            Text(text = label)
        },
        placeholder = placeholder?.let {
            {
                Text(text = it)
            }
        },
        isError = hasError,
        supportingText = when {
            errorText != null -> {
                {
                    Text(text = errorText)
                }
            }

            supportingText != null -> {
                {
                    Text(text = supportingText)
                }
            }

            else -> null
        },
        leadingIcon = leadingIcon,
        trailingIcon = trailingIcon,
        keyboardOptions = keyboardOptions,
        onKeyboardAction = onKeyboardAction,
        inputTransformation = inputTransformation,
        outputTransformation = outputTransformation,
        lineLimits = lineLimits,
        shape = shape,
        scrollState = scrollState,
        interactionSource = interactionSource
    )
}