package com.distributedLab.rarime.modules.security

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.distributedLab.rarime.R
import com.distributedLab.rarime.ui.components.AppIcon
import com.distributedLab.rarime.ui.components.rememberAppTextFieldState
import com.distributedLab.rarime.ui.theme.RarimeTheme

const val MAX_PASSCODE_ATTEMPTS = 3

@Composable
fun LockScreen(
    isBiometricEnabled: Boolean = false,
    passcode: String,
    onPass: () -> Unit,
) {
    var attemptsLeft by remember { mutableStateOf(MAX_PASSCODE_ATTEMPTS) }
    val passcodeState = rememberAppTextFieldState("")

    val isLocked = attemptsLeft == 0

    fun verifyPasscode() {
        if (isLocked) return

        if (passcodeState.text == passcode) {
            onPass()
        } else {
            attemptsLeft--
            passcodeState.updateText("")
            passcodeState.updateErrorMessage(
                if (attemptsLeft == 0) {
                    "Account locked. Please try again later."
                } else {
                    "Invalid passcode. Attempts left: $attemptsLeft."
                }
            )
        }
    }

    PasscodeScreenLayout(
        title = stringResource(R.string.enter_passcode_title),
        passcodeState = passcodeState,
        enabled = !isLocked,
        onPasscodeFilled = { verifyPasscode() },
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
                onClick = {}
            ) {
                AppIcon(
                    id = R.drawable.ic_fingerprint,
                    size = 24.dp
                )
            }
        }
    }
}

@Preview
@Composable
private fun LockScreenPreview() {
    LockScreen(
        passcode = "1234",
        isBiometricEnabled = true,
        onPass = {}
    )
}