package com.rarilabs.rarime.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.rarilabs.rarime.R
import com.rarilabs.rarime.ui.theme.RarimeTheme

@Composable
fun PasscodeField(
    modifier: Modifier = Modifier,
    maxLength: Int = 4,
    state: AppTextFieldState = rememberAppTextFieldState(""),
    enabled: Boolean = true,
    onFilled: () -> Unit = {},
    action: @Composable () -> Unit = {}
) {
    fun handleValueChange(value: String) {
        if (!enabled) return

        if (value.length <= maxLength) {
            state.updateText(value)
            state.updateErrorMessage("")
        }

        if (value.length == maxLength) {
            onFilled()
        }
    }

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Bottom
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            CodeValue(
                value = state.text,
                maxLength = maxLength,
                isError = state.isError
            )

            Spacer(modifier = Modifier.height(32.dp))
            Text(
                text = state.errorMessage,
                style = RarimeTheme.typography.caption2,
                color = RarimeTheme.colors.errorDark,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .alpha(if (state.errorMessage == "") 0f else 1f)
                    .clip(RoundedCornerShape(102.dp))
                    .background(RarimeTheme.colors.errorLight)
                    .padding(vertical = 12.dp, horizontal = 60.dp)
            )
            Spacer(modifier = Modifier.height(64.dp))
        }
        PasscodeKeyboard(
            value = state.text,
            action = action,
            onValueChange = { handleValueChange(it) }
        )
    }
}

@Composable
private fun CodeValue(
    value: String,
    maxLength: Int,
    isError: Boolean
) {
    Row {
        repeat(maxLength) { index ->
            Box(modifier = Modifier.padding(16.dp)) {
                Box(
                    modifier = Modifier
                        .width(16.dp)
                        .height(16.dp)
                        .background(
                            if (index < value.length) {
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

@Composable
private fun PasscodeKeyboard(
    value: String,
    onValueChange: (String) -> Unit,
    action: @Composable () -> Unit
) {
    Column {
        repeat(3) { rowIndex ->
            Row {
                repeat(3) { columnIndex ->
                    val number = rowIndex * 3 + columnIndex + 1
                    PasscodeKey(
                        modifier = Modifier.weight(1f),
                        onClick = { onValueChange(value + number) }
                    ) {
                        Text(
                            text = number.toString(),
                            style = RarimeTheme.typography.subtitle2,
                        )
                    }
                }
            }
        }
        Row {
            Box(modifier = Modifier.weight(1f)) {
                action()
            }
            PasscodeKey(
                modifier = Modifier.weight(1f),
                onClick = { onValueChange(value + 0) }
            ) {
                Text(
                    text = "0",
                    style = RarimeTheme.typography.subtitle2,
                )
            }
            PasscodeKey(
                modifier = Modifier.weight(1f),
                onClick = {
                    if (value.isNotEmpty()) {
                        onValueChange(value.dropLast(1))
                    }
                }
            ) {
                AppIcon(id = R.drawable.ic_backspace)
            }
        }
    }
}

@Composable
private fun PasscodeKey(
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {},
    content: @Composable () -> Unit,
) {
    val haptic = LocalHapticFeedback.current

    Button(
        modifier = modifier.height(64.dp),
        shape = RoundedCornerShape(8.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.Transparent,
            contentColor = RarimeTheme.colors.textPrimary
        ),
        onClick = {
            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
            onClick()
        }
    ) {
        content()
    }
}

@Preview(showBackground = true)
@Composable
private fun PasscodeFieldPreview() {
    val passcodeState = rememberAppTextFieldState("12", "Error message")
    PasscodeField(state = passcodeState)
}
