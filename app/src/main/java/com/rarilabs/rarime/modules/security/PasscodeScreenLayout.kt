package com.rarilabs.rarime.modules.security

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.absoluteOffset
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.rarilabs.rarime.R
import com.rarilabs.rarime.ui.components.AppIcon
import com.rarilabs.rarime.ui.components.AppTextFieldState
import com.rarilabs.rarime.ui.components.PasscodeField
import com.rarilabs.rarime.ui.components.PrimaryTextButton
import com.rarilabs.rarime.ui.components.rememberAppTextFieldState
import com.rarilabs.rarime.ui.theme.RarimeTheme

@Composable
fun PasscodeScreenLayout(
    title: String,
    subtitle: String = "",
    passcodeState: AppTextFieldState,
    onPasscodeFilled: () -> Unit,
    enabled: Boolean = true,
    onClose: (() -> Unit)? = null,
    iconId: Int = R.drawable.ic_user,
    iconColors: Pair<Color, Color> = RarimeTheme.colors.componentPrimary to RarimeTheme.colors.textPrimary,
    action: @Composable () -> Unit = {}
) {
    Box(
        contentAlignment = Alignment.BottomEnd,
        modifier = Modifier
            .background(RarimeTheme.colors.backgroundPrimary)
            .fillMaxSize()
    ) {
        onClose?.let {
            PrimaryTextButton(
                leftIcon = R.drawable.ic_close,
                onClick = onClose
            )
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
                    .fillMaxWidth()
                    .fillMaxHeight(0.9f)
                    .background(RarimeTheme.colors.backgroundPure)
                    .padding(top = 48.dp)
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxSize()
                ) {
                    Text(
                        text = title,
                        style = RarimeTheme.typography.h2,
                        color = RarimeTheme.colors.textPrimary,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 12.dp)
                    )
                    Text(
                        text = subtitle,
                        style = RarimeTheme.typography.body3,
                        color = RarimeTheme.colors.textSecondary,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth(0.75f)
                            .padding(bottom = 28.dp)
                    )
                    PasscodeField(
                        modifier = Modifier.weight(1f),
                        state = passcodeState,
                        enabled = enabled,
                        action = action,
                        onFilled = onPasscodeFilled
                    )
                }
            }
        }
    }
}

@Preview
@Composable
private fun PasscodeScreenLayoutPreview() {
    PasscodeScreenLayout(
        title = "Enter Passcode",
        passcodeState = rememberAppTextFieldState("", ""),
        onPasscodeFilled = {},
        onClose = {}
    )
}

@Preview
@Composable
private fun PasscodeScreenLayoutPreview2() {
    PasscodeScreenLayout(
        title = "Enter Passcode",
        subtitle = "Lorem ipsum dolor sit amet consectetur adipiscing elit",
        passcodeState = rememberAppTextFieldState("", "Asdf"),
        onPasscodeFilled = {},
        onClose = {}
    )
}