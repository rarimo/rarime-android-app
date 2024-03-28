package com.distributedLab.rarime.modules.security

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.distributedLab.rarime.ui.components.AppTextFieldState
import com.distributedLab.rarime.ui.components.PasscodeField
import com.distributedLab.rarime.ui.components.rememberAppTextFieldState
import com.distributedLab.rarime.ui.theme.RarimeTheme

@Composable
fun PasscodeScreenLayout(
    title: String,
    passcodeState: AppTextFieldState,
    onPasscodeFilled: () -> Unit
) {
    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
            .background(RarimeTheme.colors.backgroundPrimary)
            .padding(20.dp)
    ) {
        Text(
            text = title,
            style = RarimeTheme.typography.h4,
            color = RarimeTheme.colors.textPrimary,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 120.dp)
        )
        PasscodeField(
            modifier = Modifier
                .padding(top = 100.dp)
                .focusRequester(focusRequester),
            state = passcodeState,
            onFilled = onPasscodeFilled
        )
    }
}

@Preview
@Composable
private fun PasscodeScreenLayoutPreview() {
    PasscodeScreenLayout(
        title = "Enter Passcode",
        passcodeState = rememberAppTextFieldState(""),
        onPasscodeFilled = {}
    )
}