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
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.distributedLab.rarime.ui.theme.RarimeTheme

@Composable
fun AppTextField(
    modifier: Modifier = Modifier,
    value: String,
    onValueChange: (String) -> Unit,
    enabled: Boolean = true,
    label: String = "",
    placeholder: String = "",
    errorMessage: String = "",
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
            value = value,
            onValueChange = onValueChange,
            placeholder = {
                Text(
                    text = placeholder,
                    style = RarimeTheme.typography.body3,
                )
            },
            enabled = enabled,
            isError = errorMessage.isNotEmpty(),
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
        if (errorMessage.isNotEmpty()) {
            Text(
                text = errorMessage,
                style = RarimeTheme.typography.caption2,
                color = RarimeTheme.colors.errorMain
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun AppTextFieldPreview() {
    var textFieldValue by remember { mutableStateOf("") }

    Column(
        modifier = Modifier.padding(12.dp, 16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        AppTextField(
            value = textFieldValue,
            label = "Regular",
            placeholder = "Placeholder",
            onValueChange = { textFieldValue = it }
        )
        AppTextField(
            value = textFieldValue,
            label = "Disabled",
            placeholder = "Placeholder",
            enabled = false,
            onValueChange = { textFieldValue = it }
        )
        AppTextField(
            value = textFieldValue,
            label = "Error",
            placeholder = "Placeholder",
            errorMessage = "Error message",
            onValueChange = { textFieldValue = it }
        )
    }
}
