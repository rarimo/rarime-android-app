package com.distributedLab.rarime.modules.security

import android.util.Log
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
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
import com.distributedLab.rarime.util.BiometricUtil
import com.distributedLab.rarime.util.Constants
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.seconds

@Composable
fun LockScreen(
    isPasscodeEnabled: Boolean = false,
    isBiometricEnabled: Boolean = false,
    passcode: String,
    lockTimestamp: Long,
    onPass: () -> Unit,
    onLock: () -> Unit
) {
    val context = LocalContext.current

    var isAlertVisible by remember { mutableStateOf(false) }
    val passcodeState = rememberAppTextFieldState("")

    var attemptsLeft by remember { mutableIntStateOf(Constants.MAX_PASSCODE_ATTEMPTS) }
    var lockedTimeLeft by remember { mutableLongStateOf(lockTimestamp - System.currentTimeMillis()) }

    val isBiometricsAvailable = remember {
        BiometricUtil.isSupported(context)
    }

    fun authenticateWithBiometrics() {
        BiometricUtil.authenticate(
            context = context,
            title = "Biometric authentication",
            subtitle = "Unlock with your fingerprint",
            negativeButtonText = if (isPasscodeEnabled) "Use passcode" else "Cancel",
            onSuccess = onPass,
            onError = {}
        )
    }

    LaunchedEffect(true) {
        if (isBiometricEnabled && isBiometricsAvailable) {
            authenticateWithBiometrics()
        }
    }

    LaunchedEffect(lockTimestamp) {
        lockedTimeLeft = lockTimestamp - System.currentTimeMillis()
        while (lockedTimeLeft > 0) {
            delay(1000)
            lockedTimeLeft -= 1000
        }
    }

    LaunchedEffect(lockedTimeLeft) {
        if (lockedTimeLeft > 0) {
            val minutesLeft = (lockedTimeLeft / 60000) % 60
            val secondsLeft = (lockedTimeLeft / 1000) % 60
            passcodeState.updateErrorMessage(
                context.getString(
                    R.string.account_locked_time_msg,
                    String.format("%02d:%02d", minutesLeft, secondsLeft)
                )
            )
        } else {
            attemptsLeft = Constants.MAX_PASSCODE_ATTEMPTS
            passcodeState.updateErrorMessage("")
        }
    }

    fun verifyPasscode() {
        if (passcodeState.text == passcode) {
            onPass()
        } else {
            attemptsLeft--
            if (attemptsLeft == 0) {
                onLock()
            }

            isAlertVisible = true
        }
    }

    fun handleAlertDismiss() {
        isAlertVisible = false
        passcodeState.updateText("")
    }

    if (isAlertVisible) {
        AppAlertDialog(
            title = stringResource(R.string.invalid_passcode),
            text = if (attemptsLeft > 0) {
                stringResource(R.string.attempts_left_msg, attemptsLeft)
            } else {
                stringResource(R.string.account_locked_msg)
            },
            confirmText = stringResource(R.string.try_again),
            onConfirm = { handleAlertDismiss() },
            onDismiss = { handleAlertDismiss() }
        )
    }

    if (isPasscodeEnabled) {
        PasscodeScreenLayout(
            title = stringResource(R.string.enter_passcode_title),
            passcodeState = passcodeState,
            enabled = lockedTimeLeft <= 0,
            onPasscodeFilled = { verifyPasscode() }
        ) {
            if (isBiometricEnabled && isBiometricsAvailable) {
                Button(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(64.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Transparent,
                        contentColor = RarimeTheme.colors.textPrimary
                    ),
                    onClick = { authenticateWithBiometrics() }
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
            if (isBiometricsAvailable) {
                PrimaryButton(
                    text = stringResource(R.string.unlock_btn),
                    size = ButtonSize.Large,
                    modifier = Modifier.padding(top = 32.dp),
                    onClick = { authenticateWithBiometrics() }
                )
            } else {
                Text(
                    text = "Enable biometrics in settings to unlock with fingerprint",
                    style = RarimeTheme.typography.body1,
                    color = RarimeTheme.colors.textSecondary,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 32.dp)
                )

            }
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
        lockTimestamp = System.currentTimeMillis() + 5.seconds.inWholeMilliseconds,
        onPass = {},
        onLock = {}
    )
}