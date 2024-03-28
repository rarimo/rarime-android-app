package com.distributedLab.rarime.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.distributedLab.rarime.ui.theme.RarimeTheme

@Composable
fun PasscodeField(
    modifier: Modifier = Modifier,
    maxLength: Int = 4,
    state: AppTextFieldState = rememberAppTextFieldState(""),
    onFilled: () -> Unit = {}
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        BasicTextField(
            modifier = modifier,
            value = state.text,
            onValueChange = {
                if (it.length <= maxLength) {
                    state.updateText(it)
                    state.updateErrorMessage("")
                }

                if (it.length == maxLength) {
                    onFilled()
                }
            },
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.NumberPassword
            )
        ) {
            Row {
                repeat(maxLength) { index ->
                    Box(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .width(16.dp)
                                .height(16.dp)
                                .background(
                                    if (state.isError) {
                                        RarimeTheme.colors.errorDark
                                    } else if (index < state.text.length) {
                                        RarimeTheme.colors.primaryMain
                                    } else {
                                        RarimeTheme.colors.componentPrimary
                                    },
                                    CircleShape
                                )
                        )
                    }
                }
            }
        }
        if (state.isError) {
            Text(
                text = state.errorMessage,
                style = RarimeTheme.typography.caption2,
                color = RarimeTheme.colors.errorMain,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun PasscodeFieldPreview() {
    val passcodeState = rememberAppTextFieldState("")
    val errorPasscodeState = rememberAppTextFieldState("", "Error message")

    Column(
        modifier = Modifier.padding(12.dp, 16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        PasscodeField(state = passcodeState)
        PasscodeField(state = errorPasscodeState)
    }
}
