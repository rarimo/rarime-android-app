package com.distributedLab.rarime.modules.security

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.distributedLab.rarime.R
import com.distributedLab.rarime.ui.base.ButtonSize
import com.distributedLab.rarime.ui.components.AppIcon
import com.distributedLab.rarime.ui.components.PrimaryButton
import com.distributedLab.rarime.ui.components.rememberAppTextFieldState
import com.distributedLab.rarime.ui.theme.RarimeTheme
import com.distributedLab.rarime.util.BiometricUtil
import com.distributedLab.rarime.util.Constants
import kotlinx.coroutines.delay

@Composable
fun LockScreen(
    lockViewModule: LockViewModule = hiltViewModel(), onPass: () -> Unit
) {
    fun onPassHandler() {
        lockViewModule.unlockScreen()
        onPass.invoke()
    }

    val context = LocalContext.current

    var attemptsLeft by remember { mutableIntStateOf(Constants.MAX_PASSCODE_ATTEMPTS) }
    var lockedTimeLeft by remember { mutableLongStateOf(lockViewModule.lockTimestamp - System.currentTimeMillis()) }

    val passcodeState = rememberAppTextFieldState("")
    var appLockedSubtitle by remember {
        mutableStateOf(
            context.getString(R.string.account_locked_time_msg, String.format("%02d:%02d", 0, 0))
        )
    }

    val isBiometricsAvailable = remember {
        BiometricUtil.isSupported(context)
    }

    fun authenticateWithBiometrics() {
        BiometricUtil.authenticate(context = context,
            title = context.getString(R.string.biometric_authentication_title),
            subtitle = context.getString(R.string.biometric_authentication_subtitle),
            negativeButtonText = if (lockViewModule.isPasscodeEnabled) {
                context.getString(R.string.use_passcode_btn)
            } else {
                context.getString(R.string.cancel_btn)
            },
            onSuccess = onPass,
            onError = {})
    }

    LaunchedEffect(true) {
        if (lockViewModule.isBiometricEnabled && isBiometricsAvailable) {
            authenticateWithBiometrics()
        }
    }

    LaunchedEffect(lockViewModule.lockTimestamp) {
        lockedTimeLeft = lockViewModule.lockTimestamp - System.currentTimeMillis()
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
            passcodeState.updateErrorMessage("")
        }
    }

    fun verifyPasscode() {
        if (passcodeState.text == lockViewModule.passcode.value) {
            onPass()
        } else {
            attemptsLeft--

            if (attemptsLeft == 0) {
                lockViewModule.lockPasscode()
            }

            passcodeState.updateText("")
            passcodeState.updateErrorMessage(
                context.getString(R.string.attempts_left_msg, attemptsLeft.toString())
            )
        }
    }

    if (lockViewModule.isPasscodeEnabled) {
        PasscodeScreenLayout(
            title = run {
                if (lockedTimeLeft > 0)
                    stringResource(R.string.account_locked_msg)
                else
                    stringResource(R.string.enter_passcode_title)
            },
            subtitle = run {
                if (lockedTimeLeft > 0)
                    appLockedSubtitle
                else
                    ""
            },
            iconComponent = {
                if (lockedTimeLeft > 0) {
                    Box(
                        modifier = Modifier
                            .clip(CircleShape)
                            .fillMaxSize()
                            .background(RarimeTheme.colors.baseBlack),
                        contentAlignment = Alignment.Center,
                    ) {
                        AppIcon(
                            id = R.drawable.ic_lock,
                            size = 28.dp,
                            tint = RarimeTheme.colors.baseWhite
                        )
                    }
                } else {
                    null
                }
            },
            passcodeState = passcodeState,
            enabled = lockedTimeLeft <= 0,
            onPasscodeFilled = { verifyPasscode() }
        ) {
            if (lockViewModule.isBiometricEnabled && isBiometricsAvailable) {
                Button(modifier = Modifier
                    .fillMaxWidth()
                    .height(64.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Transparent,
                        contentColor = RarimeTheme.colors.textPrimary
                    ),
                    onClick = { authenticateWithBiometrics() }) {
                    AppIcon(
                        id = R.drawable.ic_fingerprint, size = 24.dp
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