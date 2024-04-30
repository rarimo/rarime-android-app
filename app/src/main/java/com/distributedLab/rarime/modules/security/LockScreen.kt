package com.distributedLab.rarime.modules.security

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.distributedLab.rarime.R
import com.distributedLab.rarime.ui.base.ButtonSize
import com.distributedLab.rarime.ui.components.AppAlertDialog
import com.distributedLab.rarime.ui.components.AppIcon
import com.distributedLab.rarime.ui.components.PrimaryButton
import com.distributedLab.rarime.ui.components.rememberAppTextFieldState
import com.distributedLab.rarime.ui.theme.RarimeTheme

@Composable
fun LockScreen(
    isPasscodeEnabled: Boolean = false,
    isBiometricEnabled: Boolean = false,
    passcode: String,
    onPass: () -> Unit,
) {
    var isAlertVisible by remember { mutableStateOf(false) }
    val passcodeState = rememberAppTextFieldState("")

    fun handleAlertDismiss() {
        isAlertVisible = false
        passcodeState.updateText("")
    }


    if (isAlertVisible) {
        AppAlertDialog(
            title = stringResource(R.string.invalid_passcode),
            text = stringResource(R.string.try_again_msg),
            confirmText = stringResource(R.string.try_again),
            onConfirm = { handleAlertDismiss() },
            onDismiss = { handleAlertDismiss() }
        )
    }

    if (isPasscodeEnabled) {
        PasscodeScreenLayout(
            title = stringResource(R.string.enter_passcode_title),
            passcodeState = passcodeState,
            onPasscodeFilled = {
                if (passcodeState.text == passcode) {
                    onPass()
                } else {
                    isAlertVisible = true
                }
            },
        ) {
            if (isBiometricEnabled) {
                Button(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(64.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Transparent,
                        contentColor = RarimeTheme.colors.textPrimary
                    ),
                    onClick = { /* TODO: Implement biometric authentication */ }
                ) {
                    AppIcon(
                        id = R.drawable.ic_fingerprint,
                        size = 24.dp
                    )
                }
            }
        }
    } else {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .background(RarimeTheme.colors.backgroundPrimary)
                .fillMaxSize()
                .padding(20.dp)
        ) {
            AppIcon(
                id = R.drawable.ic_fingerprint,
                size = 80.dp,
                tint = RarimeTheme.colors.textPrimary
            )
            Text(
                text = stringResource(R.string.unlock_with_fingerprint),
                style = RarimeTheme.typography.h4,
                color = RarimeTheme.colors.textPrimary,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 32.dp)
            )
            PrimaryButton(
                text = stringResource(R.string.unlock_btn),
                size = ButtonSize.Large,
                modifier = Modifier.padding(top = 32.dp),
                onClick = {
                    // TODO: Implement biometric authentication
                    onPass()
                }
            )
        }
    }
}

@Preview
@Composable
private fun LockScreenPreview() {
    LockScreen(
        isPasscodeEnabled = true,
        isBiometricEnabled = true,
        passcode = "1234",
        onPass = {}
    )
}