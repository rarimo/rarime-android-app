package com.rarilabs.rarime.modules.you

import android.annotation.SuppressLint
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.rarilabs.rarime.BuildConfig
import com.rarilabs.rarime.R
import com.rarilabs.rarime.api.registration.PassportAlreadyRegisteredByOtherPK
import com.rarilabs.rarime.data.enums.PassportIdentifier
import com.rarilabs.rarime.data.enums.PassportStatus
import com.rarilabs.rarime.data.enums.toLocalizedTitle
import com.rarilabs.rarime.data.enums.toLocalizedValue
import com.rarilabs.rarime.data.enums.toTitleStub
import com.rarilabs.rarime.data.enums.toValueStub
import com.rarilabs.rarime.manager.PassportProofState
import com.rarilabs.rarime.modules.passportScan.DownloadCircuitError
import com.rarilabs.rarime.modules.passportScan.models.EDocument
import com.rarilabs.rarime.modules.passportScan.models.PersonDetails
import com.rarilabs.rarime.ui.base.ButtonIconSize
import com.rarilabs.rarime.ui.components.AppAlertDialog
import com.rarilabs.rarime.ui.components.AppIcon
import com.rarilabs.rarime.ui.components.AppSheetState
import com.rarilabs.rarime.ui.components.PrimaryButton
import com.rarilabs.rarime.ui.components.SecondaryIconButton
import com.rarilabs.rarime.ui.components.UiLinearProgressBar
import com.rarilabs.rarime.ui.components.rememberAppSheetState
import com.rarilabs.rarime.ui.theme.RarimeTheme
import kotlinx.coroutines.delay
import kotlin.random.Random

val stageDurations = mapOf(
    PassportProofState.READING_DATA to 15_000,
    PassportProofState.APPLYING_ZERO_KNOWLEDGE to 15_000,
    PassportProofState.CREATING_CONFIDENTIAL_PROFILE to 15_000,
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
    stageDurations: Map<PassportProofState, Int>, stage: PassportProofState
): Float {
    return when (stage) {
        PassportProofState.APPLYING_ZERO_KNOWLEDGE -> getProgressForStage(
            stageDurations, PassportProofState.READING_DATA
        )

        PassportProofState.CREATING_CONFIDENTIAL_PROFILE -> getProgressForStage(
            stageDurations, PassportProofState.APPLYING_ZERO_KNOWLEDGE
        )

        PassportProofState.FINALIZING -> getProgressForStage(
            stageDurations, PassportProofState.CREATING_CONFIDENTIAL_PROFILE
        )

        else -> 0f
    }
}


suspend fun updateProgressWithTimedDelay(
    startProgress: Float,
    endProgress: Float,
    duration: Int,
    step: Int = 20,
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
        animationSpec = tween(
            easing = LinearEasing,
            durationMillis = 60000
        ),
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
                ) { progressValue ->
                    progress = progressValue
                }
            }

            PassportProofState.CREATING_CONFIDENTIAL_PROFILE -> {
                updateProgressWithTimedDelay(
                    getProgressForStage(
                        stageDurations, PassportProofState.APPLYING_ZERO_KNOWLEDGE
                    ), getProgressForStage(
                        stageDurations, PassportProofState.CREATING_CONFIDENTIAL_PROFILE
                    ), duration
                ) { progressValue ->
                    progress = progressValue
                }
            }

            PassportProofState.FINALIZING -> {
                updateProgressWithTimedDelay(
                    getProgressForStage(
                        stageDurations, PassportProofState.APPLYING_ZERO_KNOWLEDGE
                    ), getProgressForStage(
                        stageDurations, PassportProofState.FINALIZING
                    ), duration
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
                percentage = animatedProgress, trackColors = listOf(
                    RarimeTheme.colors.secondaryMain,
                    RarimeTheme.colors.secondaryMain,
                ), backgroundModifier = Modifier
                    .background(
                        RarimeTheme.colors.componentPrimary, CircleShape
                    )
                    .weight(1f)
            )
            Text(
                when (stage) {
                    PassportProofState.READING_DATA -> stringResource(R.string.downloading)
                    PassportProofState.APPLYING_ZERO_KNOWLEDGE -> stringResource(R.string.applying_zk)
                    PassportProofState.CREATING_CONFIDENTIAL_PROFILE -> stringResource(R.string.creating)
                    PassportProofState.FINALIZING -> stringResource(R.string.finishing)
                },
                style = RarimeTheme.typography.subtitle6,
                color = RarimeTheme.colors.textSecondary,
                textAlign = TextAlign.Right,
                modifier = Modifier.widthIn(min = 50.dp)
            )
        }
        Text(
            stringResource(R.string.please_don_t_close_application),
            style = RarimeTheme.typography.body5,
            color = RarimeTheme.colors.textSecondary
        )
    }
}


data class ErrorIdentityBottomBardData(
    val header: String,
    val hint: String,
    val buttonText: String,
    val onButtonClick: (() -> Unit)?,
    val infoHeader: String,
    val infoDescription: String,
    val infoConfirmCLick: () -> Unit,
)


@Composable
fun IdentityCardBottomBarContentError(
    reason: Exception,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
    revokeSheetState: AppSheetState
) {

    var infoModalState by remember { mutableStateOf(false) }

    val errorData = remember {
        when (reason) {
            is DownloadCircuitError -> {
                ErrorIdentityBottomBardData(
                    header = "Connection Error",
                    hint = "Try again",
                    buttonText = "Retry",
                    onButtonClick = { onRetry.invoke() },
                    infoHeader = "Connection Error",
                    infoDescription = "Check your internet connection and try again.",
                    infoConfirmCLick = { onRetry.invoke(); infoModalState = false })
            }

            is PassportAlreadyRegisteredByOtherPK -> {
                ErrorIdentityBottomBardData(
                    header = "Already Registered",
                    hint = "Try to find previous pk".plus(
                        if (BuildConfig.isTestnet) {
                            "TESTER REVOKE"
                        } else {
                            ""
                        }
                    ),
                    buttonText = "",
                    onButtonClick = null,
                    infoHeader = "Passport already in use",
                    infoDescription = if (!BuildConfig.isTestnet) "This passport is already registered with another identity. Please try to find the private key that was previously used with this passport." else "PresConfirmTo Revoke",
                    infoConfirmCLick = {
                        infoModalState = false; if (BuildConfig.isTestnet) revokeSheetState.show()
                    })
            }

            else -> {
                ErrorIdentityBottomBardData(
                    header = "Unknown error",
                    hint = "Try again later",
                    buttonText = "Retry",
                    onButtonClick = { onRetry.invoke() },
                    infoHeader = "Something went wrong",
                    infoDescription = "An unexpected error occurred. Please try again later.",
                    infoConfirmCLick = { infoModalState = false })
            }
        }
    }

    Row(
        horizontalArrangement = Arrangement.spacedBy(14.dp),
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AppIcon(
                modifier = Modifier
                    .clickable {
                        infoModalState = true
                    }
                    .clip(CircleShape),

                id = R.drawable.ic_information_line,
                size = 20.dp,
                description = stringResource(R.string.error),
                tint = RarimeTheme.colors.errorDark,
            )
            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(
                    errorData.header,
                    style = RarimeTheme.typography.subtitle6,
                    color = RarimeTheme.colors.errorDark
                )
                Text(
                    errorData.hint,
                    style = RarimeTheme.typography.body5,
                    color = RarimeTheme.colors.textSecondary
                )
            }
        }
        Spacer(modifier = Modifier.weight(1f))

        if (errorData.onButtonClick != null) {
            PrimaryButton(
                modifier = Modifier.clip(RoundedCornerShape(20.dp)),
                text = errorData.buttonText,
                leftIcon = R.drawable.ic_restart_line,
                onClick = errorData.onButtonClick,
            )
        }

    }

    if (infoModalState) {
        AppAlertDialog(title = errorData.infoHeader, text = errorData.infoDescription, onConfirm = {
            errorData.infoConfirmCLick.invoke()
        }, onDismiss = {
            infoModalState = false
        })
    }

}

@Composable
fun IdentityCardBottomBarContentInfo(
    modifier: Modifier = Modifier,
    eDocument: EDocument,
    identifier: PassportIdentifier,
    onIncognitoChange: (Boolean) -> Unit,
    isIncognito: Boolean,
    settingsSheetState: AppSheetState
) {
    Column(modifier) {
        Text(
            text = if (isIncognito) identifier.toTitleStub() else identifier.toLocalizedTitle(),
            style = RarimeTheme.typography.subtitle6,
            color = RarimeTheme.colors.textSecondary
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = if (isIncognito) identifier.toValueStub() else identifier.toLocalizedValue(
                eDocument
            ),
            style = RarimeTheme.typography.subtitle5,
            color = RarimeTheme.colors.textPrimary
        )
    }

    Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
        SecondaryIconButton(
            size = ButtonIconSize.Medium,
            icon = if (isIncognito) R.drawable.ic_eye_slash else R.drawable.ic_eye,
            onClick = { onIncognitoChange(!isIncognito) })


        SecondaryIconButton(
            size = ButtonIconSize.Medium,
            icon = R.drawable.ic_dots_three_outline,
            onClick = { settingsSheetState.show() })
    }
}

@Composable
fun IdentityCardBottomBar(
    modifier: Modifier = Modifier,
    eDocument: EDocument,
    identifier: PassportIdentifier,
    registrationStatus: IdentityCardBottomBarUiState,
    retryRegistration: () -> Unit,
    onIncognitoChange: (Boolean) -> Unit,
    isIncognito: Boolean,
    settingsSheetState: AppSheetState,
    revokeSheetState: AppSheetState
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
            if (registrationStatus.proofError !== null) {
                IdentityCardBottomBarContentError(
                    reason = registrationStatus.proofError,
                    onRetry = { retryRegistration() },
                    revokeSheetState = revokeSheetState
                )
            } else if (registrationStatus.passportStatus == PassportStatus.UNREGISTERED) {
                IdentityCardBottomBarContentLoading(stage = registrationStatus.loadingState)
            } else {
                IdentityCardBottomBarContentInfo(
                    eDocument = eDocument,
                    identifier = identifier,
                    onIncognitoChange = onIncognitoChange,
                    isIncognito = isIncognito,
                    settingsSheetState = settingsSheetState
                )
            }
        }
    }
}


@Preview
@Composable
private fun IdentityCardBottomBarPreview() {

    val registrationStatus = IdentityCardBottomBarUiState()
    Surface {
        IdentityCardBottomBar(
            eDocument = EDocument(
                personDetails = PersonDetails(
                    name = "John",
                    surname = "Doe",
                    birthDate = "01.01.1996",
                    expiryDate = "01.01.2025",
                    nationality = "USA",
                    serialNumber = "123456789",
                    faceImageInfo = null
                )
            ),
            registrationStatus = registrationStatus,
            onIncognitoChange = {},
            retryRegistration = {},
            isIncognito = false,
            settingsSheetState = rememberAppSheetState(),
            identifier = PassportIdentifier.BIRTH_DATE,
            revokeSheetState = rememberAppSheetState()
        )
    }
}


@Preview
@Composable
private fun IdentityCardInfoBottomBarPreview() {
    val registrationStatus = IdentityCardBottomBarUiState(proofError = Exception())
    Surface {
        IdentityCardBottomBar(
            eDocument = EDocument(
                personDetails = PersonDetails(
                    name = "John",
                    surname = "Doe",
                    birthDate = "01.01.1996",
                    expiryDate = "01.01.2025",
                    nationality = "USA",
                    serialNumber = "123456789",
                    faceImageInfo = null
                )
            ),
            registrationStatus = registrationStatus,
            onIncognitoChange = {},
            retryRegistration = {},
            isIncognito = false,
            settingsSheetState = rememberAppSheetState(),
            identifier = PassportIdentifier.BIRTH_DATE,
            revokeSheetState = rememberAppSheetState()

        )
    }
}


@Preview
@Composable
private fun IdentityCardLoadingBottomBarPreview() {
    val registrationStatus = IdentityCardBottomBarUiState(
        proofError = null, passportStatus = PassportStatus.UNREGISTERED
    )
    Surface {
        IdentityCardBottomBar(
            eDocument = EDocument(
                personDetails = PersonDetails(
                    name = "John",
                    surname = "Doe",
                    birthDate = "01.01.1996",
                    expiryDate = "01.01.2025",
                    nationality = "USA",
                    serialNumber = "123456789",
                    faceImageInfo = null
                )
            ),
            registrationStatus = registrationStatus,
            onIncognitoChange = {},
            retryRegistration = {},
            isIncognito = false,
            settingsSheetState = rememberAppSheetState(),
            identifier = PassportIdentifier.BIRTH_DATE,
            revokeSheetState = rememberAppSheetState()
        )
    }
}