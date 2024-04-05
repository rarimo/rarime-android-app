package com.distributedLab.rarime.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.distributedLab.rarime.ui.theme.RarimeTheme

class AppTextFieldState(initialText: String, initialErrorMessage: String = "") {
    var text by mutableStateOf(initialText)
        private set

    var errorMessage by mutableStateOf(initialErrorMessage)
        private set

    fun updateText(newText: String) {
        text = newText
        errorMessage = ""
    }

    fun updateErrorMessage(newErrorMessage: String) {
        errorMessage = newErrorMessage
    }

    val isError: Boolean
        get() = errorMessage.isNotEmpty()

    companion object {
        val Saver: Saver<AppTextFieldState, *> = listSaver(
            save = { listOf(it.text, it.errorMessage) },
            restore = {
                AppTextFieldState(
                    initialText = it[0],
                    initialErrorMessage = it[1],
                )
            }
        )
    }
}

@Composable
fun rememberAppTextFieldState(initialText: String, initialErrorMessage: String = "") =
    rememberSaveable(initialText, initialErrorMessage, saver = AppTextFieldState.Saver) {
        AppTextFieldState(initialText, initialErrorMessage)
    }

@Composable
fun AppTextField(
    modifier: Modifier = Modifier,
    state: AppTextFieldState = rememberAppTextFieldState(""),
    enabled: Boolean = true,
    label: String = "",
    placeholder: String = "",
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        if (label.isNotEmpty()) {
            Text(
                text = label,
                style = RarimeTheme.typography.subtitle4,
                color = if (enabled) RarimeTheme.colors.textPrimary else RarimeTheme.colors.textDisabled
            )
        }
        OutlinedTextField(
            value = state.text,
            onValueChange = { state.updateText(it) },
            placeholder = {
                Text(
                    text = placeholder,
                    style = RarimeTheme.typography.body3,
                )
            },
            enabled = enabled,
            isError = state.isError,
            textStyle = RarimeTheme.typography.body3,
            singleLine = true,
            colors = TextFieldDefaults.colors(
                unfocusedContainerColor = Color.Transparent,
                unfocusedIndicatorColor = RarimeTheme.colors.componentPrimary,
                unfocusedPlaceholderColor = RarimeTheme.colors.textSecondary,
                unfocusedTextColor = RarimeTheme.colors.textPrimary,

                focusedContainerColor = Color.Transparent,
                focusedIndicatorColor = RarimeTheme.colors.componentPressed,
                focusedPlaceholderColor = RarimeTheme.colors.textSecondary,
                focusedTextColor = RarimeTheme.colors.textPrimary,

                disabledContainerColor = RarimeTheme.colors.componentDisabled,
                disabledIndicatorColor = Color.Transparent,
                disabledPlaceholderColor = RarimeTheme.colors.textDisabled,
                disabledTextColor = RarimeTheme.colors.textDisabled,

                errorContainerColor = Color.Transparent,
                errorIndicatorColor = RarimeTheme.colors.errorMain,
                errorPlaceholderColor = RarimeTheme.colors.textSecondary,
                errorTextColor = RarimeTheme.colors.textPrimary,

                cursorColor = RarimeTheme.colors.textPrimary,

                ),
            shape = RoundedCornerShape(12.dp),
            modifier = modifier.fillMaxWidth()
        )
        if (state.isError) {
            Text(
                text = state.errorMessage,
                style = RarimeTheme.typography.caption2,
                color = RarimeTheme.colors.errorMain
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun AppTextFieldPreview() {
    val textFieldState = rememberAppTextFieldState("")
    val errorTextFieldState = rememberAppTextFieldState("", "Error message")

    Column(
        modifier = Modifier.padding(12.dp, 16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        AppTextField(
            state = textFieldState,
            label = "Regular",
            placeholder = "Placeholder",
        )
        AppTextField(
            state = textFieldState,
            label = "Disabled",
            placeholder = "Placeholder",
            enabled = false,
        )
        AppTextField(
            state = errorTextFieldState,
            label = "Error",
            placeholder = "Placeholder",
        )
    }
}
