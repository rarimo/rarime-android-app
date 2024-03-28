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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.distributedLab.rarime.ui.theme.RarimeTheme

class PasscodeFieldState(initialValue: String) {
    var value by mutableStateOf(initialValue)
        private set

    fun updateValue(newValue: String) {
        value = newValue
    }

    companion object {
        val Saver: Saver<PasscodeFieldState, *> = listSaver(
            save = { listOf(it.value) },
            restore = { PasscodeFieldState(initialValue = it[0]) }
        )
    }
}

@Composable
fun rememberPasscodeFieldState(initialText: String) =
    rememberSaveable(initialText, saver = PasscodeFieldState.Saver) {
        PasscodeFieldState(initialText)
    }

@Composable
fun PasscodeField(
    modifier: Modifier = Modifier,
    maxLength: Int = 4,
    state: PasscodeFieldState = rememberPasscodeFieldState(""),
    onFilled: () -> Unit = {}
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        BasicTextField(
            modifier = modifier,
            value = state.value,
            onValueChange = {
                if (it.length <= maxLength) {
                    state.updateValue(it)
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
                                    if (index < state.value.length) {
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
    }
}

@Preview(showBackground = true)
@Composable
private fun PasscodeFieldPreview() {
    val state = rememberPasscodeFieldState("")

    Column(
        modifier = Modifier.padding(12.dp, 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        PasscodeField(state = state)
    }
}
