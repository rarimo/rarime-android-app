package com.rarilabs.rarime.modules.security

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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.rarilabs.rarime.R
import com.rarilabs.rarime.ui.base.ButtonSize
import com.rarilabs.rarime.ui.components.AppIcon
import com.rarilabs.rarime.ui.components.PrimaryButton
import com.rarilabs.rarime.ui.components.rememberAppTextFieldState
import com.rarilabs.rarime.ui.theme.RarimeTheme
import com.rarilabs.rarime.util.BiometricUtil
import com.rarilabs.rarime.util.Constants
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun LockScreen(
    lockViewModule: LockViewModule = hiltViewModel(), onPass: () -> Unit
) {
    val scope = rememberCoroutineScope()

    fun onPassHandler() {
        scope.launch {
            lockViewModule.unlockScreen()
            delay(300)
            onPass.invoke()
        }
    }

    /* STATE */
    val context = LocalContext.current
    val passcodeState = rememberAppTextFieldState("")
    val lockTimestamp = lockViewModule.lockTimestamp.collectAsState()
    var attemptsLeft by remember { mutableIntStateOf(Constants.MAX_PASSCODE_ATTEMPTS) }
    var lockedTimeLeft by remember { mutableLongStateOf(lockTimestamp.value - System.currentTimeMillis()) }
    var appLockedSubtitle by remember {
        mutableStateOf(
            context.getString(R.string.account_locked_time_msg, String.format("%02d:%02d", 0, 0))
        )
    }

    /* COMPUTED */
    var isAttemptsDisabled by remember { mutableStateOf(lockedTimeLeft > 0 || attemptsLeft <= 0) }
    LaunchedEffect(lockedTimeLeft, attemptsLeft) {
        isAttemptsDisabled = lockedTimeLeft > 0 || attemptsLeft <= 0
    }

    val isBiometricsAvailable = remember {
        BiometricUtil.isSupported(context)
    }

    /* METHODS */
    fun authenticateWithBiometrics() {
        BiometricUtil.authenticate(context = context,
            title = context.getString(R.string.biometric_authentication_title),
            subtitle = context.getString(R.string.biometric_authentication_subtitle),
            negativeButtonText = if (lockViewModule.isPasscodeEnabled) {
                context.getString(R.string.use_passcode_btn)
            } else {
                context.getString(R.string.cancel_btn)
            },
            onSuccess = { onPassHandler() },
            onError = {})
    }

    fun verifyPasscode() {
        if (passcodeState.text == lockViewModule.passcode.value) {
            onPassHandler()
        } else {
            attemptsLeft--

            passcodeState.updateText("")

            if (attemptsLeft <= 0) {
                lockViewModule.lockPasscode()
                passcodeState.updateErrorMessage("")
            } else {
                passcodeState.updateErrorMessage(
                    context.getString(R.string.attempts_left_msg, attemptsLeft.toString())
                )
            }
        }
    }

    /* EFFECTS */
    LaunchedEffect(true) {
        if (lockViewModule.isBiometricEnabled && isBiometricsAvailable) {
            if (
                lockTimestamp.value <= System.currentTimeMillis() &&
                !isAttemptsDisabled
            ) {
                authenticateWithBiometrics()
            }
        }
    }

    LaunchedEffect(lockTimestamp.value) {
        lockedTimeLeft = lockTimestamp.value - System.currentTimeMillis()
        while (lockedTimeLeft > 0) {
            delay(1000)
            lockedTimeLeft -= 1000
        }
    }

    LaunchedEffect(lockedTimeLeft) {
        if (lockedTimeLeft > 0) {
            val minutesLeft = (lockedTimeLeft / 60000) % 60
            val secondsLeft = (lockedTimeLeft / 1000) % 60
            appLockedSubtitle = context.getString(
                R.string.account_locked_time_msg,
                String.format("%02d:%02d", minutesLeft, secondsLeft)
            )
        } else {
            attemptsLeft = Constants.MAX_PASSCODE_ATTEMPTS
        }
    }

    if (lockViewModule.isPasscodeEnabled) {
        PasscodeScreenLayout(
            title = if (isAttemptsDisabled) stringResource(R.string.account_locked_msg) else stringResource(
                R.string.enter_passcode_title
            ),
            subtitle = if (isAttemptsDisabled) appLockedSubtitle else "",
            iconId = if (isAttemptsDisabled) R.drawable.ic_lock else R.drawable.ic_user,
            iconColors = if (isAttemptsDisabled) RarimeTheme.colors.baseBlack to RarimeTheme.colors.baseWhite
            else RarimeTheme.colors.primaryMain to RarimeTheme.colors.baseBlack,
            passcodeState = passcodeState,
            enabled = !isAttemptsDisabled,
            onPasscodeFilled = { verifyPasscode() }
        ) {
            if (lockViewModule.isBiometricEnabled && isBiometricsAvailable) {
                Button(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(64.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Transparent,
                        contentColor = RarimeTheme.colors.textPrimary
                    ),
                    onClick = { authenticateWithBiometrics() },
                    enabled = !isAttemptsDisabled
                ) {
                    AppIcon(
                        id = R.drawable.ic_fingerprint, size = 24.dp, tint = RarimeTheme.colors.textPrimary
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
                id = R.drawable.ic_fingerprint, size = 80.dp, tint = RarimeTheme.colors.textPrimary
            )
            Text(
                text = stringResource(R.string.biometric_lock_title),
                style = RarimeTheme.typography.h4,
                color = RarimeTheme.colors.textPrimary,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 32.dp)
            )
            if (isBiometricsAvailable) {
                PrimaryButton(text = stringResource(R.string.unlock_btn),
                    size = ButtonSize.Large,
                    modifier = Modifier.padding(top = 32.dp),
                    onClick = { authenticateWithBiometrics() })
            } else {
                Text(
                    text = stringResource(R.string.enable_biometrics_msg),
                    style = RarimeTheme.typography.body3,
                    color = RarimeTheme.colors.textSecondary,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp)
                        .padding(horizontal = 40.dp)
                )

            }
        }
    }
}

@Preview
@Composable
private fun LockScreenPreview() {
    LockScreen(
        onPass = {},
    )
}