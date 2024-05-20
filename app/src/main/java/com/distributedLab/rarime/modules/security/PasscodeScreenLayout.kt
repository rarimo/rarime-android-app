package com.distributedLab.rarime.modules.security

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.distributedLab.rarime.R
import com.distributedLab.rarime.ui.components.AppTextFieldState
import com.distributedLab.rarime.ui.components.PasscodeField
import com.distributedLab.rarime.ui.components.PrimaryTextButton
import com.distributedLab.rarime.ui.components.rememberAppTextFieldState
import com.distributedLab.rarime.ui.theme.RarimeTheme

@Composable
fun PasscodeScreenLayout(
    title: String,
    passcodeState: AppTextFieldState,
    onPasscodeFilled: () -> Unit,
    enabled: Boolean = true,
    onClose: (() -> Unit)? = null,
    action: @Composable () -> Unit = {}
) {
    Box(
        contentAlignment = Alignment.TopEnd,
        modifier = Modifier
            .background(RarimeTheme.colors.backgroundPrimary)
            .fillMaxSize()
            .padding(20.dp)
    ) {
        onClose?.let {
            PrimaryTextButton(
                leftIcon = R.drawable.ic_close,
                onClick = onClose
            )
        }
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxSize()
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
                modifier = Modifier.padding(top = 100.dp),
                state = passcodeState,
                enabled = enabled,
                action = action,
                onFilled = onPasscodeFilled
            )
        }
    }
}

@Preview
@Composable
private fun PasscodeScreenLayoutPreview() {
    PasscodeScreenLayout(
        title = "Enter Passcode",
        passcodeState = rememberAppTextFieldState(""),
        onPasscodeFilled = {},
        onClose = {}
    )
}