package com.rarilabs.rarime.modules.you

import android.annotation.SuppressLint
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.rarilabs.rarime.R
import com.rarilabs.rarime.modules.passportScan.proof.PassportProofState
import com.rarilabs.rarime.ui.components.AppIcon
import com.rarilabs.rarime.ui.components.PrimaryButton
import com.rarilabs.rarime.ui.components.UiLinearProgressBar
import com.rarilabs.rarime.ui.theme.RarimeTheme
import com.rarilabs.rarime.util.RandomUtil.Companion.updateProgressWithRandomDelay

enum class IdentityCardBottomBarState {
    LOADING,
    ERROR,
    INFO
}

val progressDurations = mapOf(
    PassportProofState.READING_DATA to 0,
    PassportProofState.APPLYING_ZERO_KNOWLEDGE to 18,
    PassportProofState.CREATING_CONFIDENTIAL_PROFILE to 8,
    PassportProofState.FINALIZING to 2
)

@SuppressLint("AutoboxingStateCreation")
@Composable
fun IdentityCardBottomBarContentLoading(
    modifier: Modifier = Modifier,
    status: PassportProofState
) {
    var progress by remember { mutableFloatStateOf(0f) }
    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = tween(easing = LinearEasing),
    )

    LaunchedEffect(status) {
        val durationInSeconds = progressDurations[status] ?: 0

        updateProgressWithRandomDelay(durationInSeconds = durationInSeconds) { progressValue ->
            progress = when (status) {
                PassportProofState.READING_DATA -> progressValue
                PassportProofState.APPLYING_ZERO_KNOWLEDGE -> (25 + progressValue) / 100f
                PassportProofState.CREATING_CONFIDENTIAL_PROFILE -> (50 + progressValue) / 100f
                PassportProofState.FINALIZING -> (75 + progressValue) / 100f
            }
        }
    }

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(2.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            UiLinearProgressBar(
                percentage = animatedProgress,
                trackColors = listOf(
                    RarimeTheme.colors.secondaryMain,
                    RarimeTheme.colors.secondaryMain,
                ),
                backgroundModifier = Modifier
                    .background(RarimeTheme.colors.componentPrimary, CircleShape)
                    .weight(1f)
            )
            Text(
                "Finishing...",
                style = RarimeTheme.typography.subtitle6,
                color = RarimeTheme.colors.textSecondary
            )
        }
        Text(
            "Please don’t close application",
            style = RarimeTheme.typography.body5,
            color = RarimeTheme.colors.textSecondary
        )
    }
}

@Composable
fun IdentityCardBottomBarContentError(modifier: Modifier = Modifier) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(14.dp),
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier.fillMaxWidth()
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            AppIcon(
                id = R.drawable.ic_information_line,
                size = 20.dp,
                description = "Error",
                tint = RarimeTheme.colors.errorDark,
            )
            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(
                    "Unknown error",
                    style = RarimeTheme.typography.subtitle6,
                    color = RarimeTheme.colors.errorDark
                )
                Text(
                    "Please try again",
                    style = RarimeTheme.typography.body5,
                    color = RarimeTheme.colors.textSecondary
                )
            }
        }
        Spacer(modifier = Modifier.weight(1f))
        PrimaryButton(
            modifier = Modifier.clip(RoundedCornerShape(20.dp)),
            text = "Retry",
            leftIcon = R.drawable.ic_restart_line,
            onClick = { TODO() }
        )
    }
}

@Composable
fun IdentityCardBottomBarContentInfo(modifier: Modifier = Modifier) {
    Column(modifier) {
        Text("#")
        Spacer(modifier = Modifier.height(4.dp))
        Text("13B294029491")
    }

    Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
        AppIcon(
            id = R.drawable.ic_eye_slash,
            modifier = Modifier
                .padding(8.dp)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = { TODO() })
        )

        AppIcon(
            id = R.drawable.ic_dots_three_outline,
            modifier = Modifier
                .padding(8.dp)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = { })
        )
    }
}

@Composable
fun IdentityCardBottomBar(
    modifier: Modifier = Modifier,
    state: IdentityCardBottomBarState = IdentityCardBottomBarState.LOADING,
) {
    Card(shape = RoundedCornerShape(16.dp)) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(color = RarimeTheme.colors.backgroundPrimary)
                .padding(horizontal = 16.dp, vertical = 9.dp)
                .then(modifier),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            when (state) {
                IdentityCardBottomBarState.LOADING -> IdentityCardBottomBarContentLoading(
                    status = PassportProofState.APPLYING_ZERO_KNOWLEDGE
                )
                IdentityCardBottomBarState.ERROR -> IdentityCardBottomBarContentError()
                IdentityCardBottomBarState.INFO -> IdentityCardBottomBarContentInfo()
            }
        }
    }
}

@Preview
@Composable
private fun IdentityCardBottomBarPreview() {
    Surface {
        IdentityCardBottomBarContentLoading(status = PassportProofState.APPLYING_ZERO_KNOWLEDGE)
    }
}
