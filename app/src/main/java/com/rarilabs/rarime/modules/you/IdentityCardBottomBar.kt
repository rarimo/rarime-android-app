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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.rarilabs.rarime.R
import com.rarilabs.rarime.manager.PassportProofState
import com.rarilabs.rarime.ui.components.AppIcon
import com.rarilabs.rarime.ui.components.PrimaryButton
import com.rarilabs.rarime.ui.components.UiLinearProgressBar
import com.rarilabs.rarime.ui.theme.RarimeTheme
import kotlinx.coroutines.delay
import kotlin.random.Random

enum class IdentityCardBottomBarState(string: String) {
    LOADING(""), ERROR(""), INFO("")
}

val stageDurations = mapOf(
    PassportProofState.READING_DATA to 2_000,
    PassportProofState.APPLYING_ZERO_KNOWLEDGE to 18_000,
    PassportProofState.CREATING_CONFIDENTIAL_PROFILE to 8_000,
    PassportProofState.FINALIZING to 2_000
)

fun getStagePercentage(
    stageDurations: Map<PassportProofState, Int>, targetStage: PassportProofState
): Float {
    val totalDuration = stageDurations.values.sum()
    val stageDuration = stageDurations[targetStage] ?: 0
    return if (totalDuration > 0) stageDuration.toFloat() / totalDuration else 0f
}

fun getProgressForStage(
    stageDurations: Map<PassportProofState, Int>, targetStage: PassportProofState
): Float {
    var totalProgress = 0f

    for ((stage, _) in stageDurations) {
        val stagePercentage = getStagePercentage(stageDurations, stage)

        totalProgress += stagePercentage

        if (stage == targetStage) {
            return totalProgress
        }
    }

    return totalProgress
}

fun getInitialStageProgress(
    stageDurations: Map<PassportProofState, Int>,
    stage: PassportProofState
): Float {
    return when (stage) {
        PassportProofState.APPLYING_ZERO_KNOWLEDGE -> getProgressForStage(
            stageDurations,
            PassportProofState.READING_DATA
        )

        PassportProofState.CREATING_CONFIDENTIAL_PROFILE -> getProgressForStage(
            stageDurations,
            PassportProofState.APPLYING_ZERO_KNOWLEDGE
        )

        PassportProofState.FINALIZING -> getProgressForStage(
            stageDurations,
            PassportProofState.CREATING_CONFIDENTIAL_PROFILE
        )

        else -> 0f
    }
}


suspend fun updateProgressWithTimedDelay(
    startProgress: Float,
    endProgress: Float,
    duration: Int,
    step: Int = 10,
    updateProgress: (Float) -> Unit,
) {
    val progressDuration = duration.toFloat()
    val stepDuration = progressDuration / step

    var currentProgress = startProgress

    for (i in 1..step) {
        val stepProgress = (endProgress - startProgress) / step

        val randomFactor = 1 + Random.nextFloat() * 0.2f
        currentProgress += stepProgress * randomFactor

        currentProgress = currentProgress.coerceIn(startProgress, endProgress)

        val randomDelayFactor = 1 + Random.nextFloat() * 0.4f
        val randomStepDuration = stepDuration * randomDelayFactor

        updateProgress(currentProgress)

        delay(randomStepDuration.toLong())
    }
}

@SuppressLint("AutoboxingStateCreation")
@Composable
fun IdentityCardBottomBarContentLoading(
    modifier: Modifier = Modifier,
    stage: PassportProofState,
) {
    var progress by rememberSaveable {
        mutableFloatStateOf(
            getInitialStageProgress(stageDurations, stage)
        )
    }
    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = tween(easing = LinearEasing),
    )

    LaunchedEffect(stage) {
        val duration = stageDurations[stage] ?: 0

        when (stage) {
            PassportProofState.READING_DATA -> {
                updateProgressWithTimedDelay(
                    0f,
                    getProgressForStage(
                        stageDurations, PassportProofState.READING_DATA
                    ),
                    duration,
                ) { progressValue ->
                    progress = progressValue
                }
            }

            PassportProofState.APPLYING_ZERO_KNOWLEDGE -> {
                updateProgressWithTimedDelay(
                    getProgressForStage(
                        stageDurations, PassportProofState.READING_DATA
                    ),
                    getProgressForStage(
                        stageDurations, PassportProofState.APPLYING_ZERO_KNOWLEDGE
                    ),
                    duration,
                    step = 20
                ) { progressValue ->
                    progress = progressValue
                }
            }

            PassportProofState.CREATING_CONFIDENTIAL_PROFILE -> {
                updateProgressWithTimedDelay(
                    getProgressForStage(
                        stageDurations, PassportProofState.APPLYING_ZERO_KNOWLEDGE
                    ),
                    getProgressForStage(
                        stageDurations, PassportProofState.CREATING_CONFIDENTIAL_PROFILE
                    ),
                    duration
                ) { progressValue ->
                    progress = progressValue
                }
            }

            PassportProofState.FINALIZING -> {
                updateProgressWithTimedDelay(
                    getProgressForStage(
                        stageDurations, PassportProofState.APPLYING_ZERO_KNOWLEDGE
                    ),
                    getProgressForStage(
                        stageDurations, PassportProofState.FINALIZING
                    ),
                    duration
                ) { progressValue ->
                    progress = progressValue
                }
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
                    .background(
                        RarimeTheme.colors.componentPrimary, CircleShape
                    )
                    .weight(1f)
            )
            Text(
                when (stage) {
                    PassportProofState.READING_DATA -> "Downloading"
                    PassportProofState.APPLYING_ZERO_KNOWLEDGE -> "Applying ZK"
                    PassportProofState.CREATING_CONFIDENTIAL_PROFILE -> "Creating"
                    PassportProofState.FINALIZING -> "Finishing"
                },
                style = RarimeTheme.typography.subtitle6,
                color = RarimeTheme.colors.textSecondary,
                textAlign = TextAlign.Right,
                modifier = Modifier.widthIn(min = 50.dp)
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
            onClick = { TODO() })
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
            id = R.drawable.ic_eye_slash, modifier = Modifier
                .padding(8.dp)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = { }
                )
        )


        AppIcon(
            id = R.drawable.ic_dots_three_outline, modifier = Modifier
                .padding(8.dp)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = { }
                )
        )
    }
}

@Composable
fun IdentityCardBottomBar(
    modifier: Modifier = Modifier,
    // TODO: Replace state with ViewModel
    state: IdentityCardBottomBarState = IdentityCardBottomBarState.INFO,
    viewModel: IdentityCardBottomBarViewModel = viewModel()
) {
    val bottomBarUiState by viewModel.uiState.collectAsState()

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
                    stage = bottomBarUiState.state,
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
        IdentityCardBottomBarContentLoading(
            stage = PassportProofState.APPLYING_ZERO_KNOWLEDGE,
        )
    }
}