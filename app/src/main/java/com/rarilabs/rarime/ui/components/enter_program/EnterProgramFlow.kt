package com.rarilabs.rarime.ui.components.enter_program

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.absolutePadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.rarilabs.rarime.R
import com.rarilabs.rarime.data.enums.PassportStatus
import com.rarilabs.rarime.ui.components.AppBottomSheet
import com.rarilabs.rarime.ui.components.AppSheetState
import com.rarilabs.rarime.ui.components.HideSheetFn
import com.rarilabs.rarime.ui.components.PrimaryButton
import com.rarilabs.rarime.ui.components.PrimaryTextButton
import com.rarilabs.rarime.ui.components.enter_program.components.AboutProgram
import com.rarilabs.rarime.ui.components.enter_program.components.Invitation
import com.rarilabs.rarime.ui.components.enter_program.components.PolicyConfirmation
import com.rarilabs.rarime.ui.components.rememberAppSheetState
import com.rarilabs.rarime.ui.theme.RarimeTheme

enum class UNSPECIFIED_PASSPORT_STEPS(val value: Int) {
    INVITATION(1),
    ONLY_INVITATION(2),
    POLICY_CONFIRMATION(3),
    ABOUT_PROGRAM(5),
}

@Composable
fun EnterProgramFlow(
    onFinish: () -> Unit,
    sheetState: AppSheetState,
    hide: HideSheetFn,
    passportStatus: PassportStatus = PassportStatus.UNSCANNED,
    initialStep: UNSPECIFIED_PASSPORT_STEPS = UNSPECIFIED_PASSPORT_STEPS.INVITATION
) {
    var currStep by remember {
        mutableStateOf(initialStep)
    }

    AnimatedVisibility(
        visible = currStep.equals(UNSPECIFIED_PASSPORT_STEPS.INVITATION),
        enter = fadeIn() + slideInHorizontally(initialOffsetX = { it }),
        exit = fadeOut() + slideOutHorizontally(targetOffsetX = { -it })
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .absolutePadding(top = 12.dp, left = 24.dp, right = 24.dp),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                PrimaryTextButton(
                    leftIcon = R.drawable.ic_close,
                    onClick = { sheetState.hide() }
                )
            }

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Invitation(
                    onNext = {
                        when(passportStatus) {
                            PassportStatus.WAITLIST -> {
                                onFinish()
                            }
                            else -> {
                                onFinish()
                            }
                        }
                    },
                    updateStep = { currStep = it }
                )
            }
        }
    }

    AnimatedVisibility(
        visible = currStep.equals(UNSPECIFIED_PASSPORT_STEPS.ONLY_INVITATION),
        enter = fadeIn() + slideInHorizontally(initialOffsetX = { it }),
        exit = fadeOut() + slideOutHorizontally(targetOffsetX = { -it })
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .absolutePadding(top = 12.dp, left = 24.dp, right = 24.dp),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                PrimaryTextButton(
                    leftIcon = R.drawable.ic_close,
                    onClick = { sheetState.hide() }
                )
            }

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Invitation(
                    onNext = {
                        onFinish()
                    },
                    updateStep = { currStep = it }
                )
            }
        }
    }

    AnimatedVisibility(
        visible = currStep.equals(UNSPECIFIED_PASSPORT_STEPS.POLICY_CONFIRMATION),
        enter = fadeIn() + slideInHorizontally(initialOffsetX = { it }),
        exit = fadeOut() + slideOutHorizontally(targetOffsetX = { -it })
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                PrimaryTextButton(
                    leftIcon = R.drawable.ic_close,
                    onClick = { sheetState.hide() }
                )
            }
            PolicyConfirmation(
                onNext = {
                    hide {
                        onFinish()
                    }
                }
            )
        }
    }

    AnimatedVisibility(
        visible = currStep.equals(UNSPECIFIED_PASSPORT_STEPS.ABOUT_PROGRAM),
        enter = fadeIn() + slideInHorizontally(initialOffsetX = { it }),
        exit = fadeOut() + slideOutHorizontally(targetOffsetX = { -it })
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                PrimaryTextButton(
                    leftIcon = R.drawable.ic_arrow_left,
                    onClick = { currStep = UNSPECIFIED_PASSPORT_STEPS.INVITATION }
                )

                Text(
                    text = "About the program",
                    style = RarimeTheme.typography.subtitle4,
                    color = RarimeTheme.colors.textPrimary,
                )

                PrimaryTextButton(
                    leftIcon = R.drawable.ic_close,
                    onClick = { sheetState.hide() }
                )
            }
            AboutProgram(
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun EnterProgramFlowPreview() {
    val nonSpecificAppSheetState = rememberAppSheetState()

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        PrimaryButton(onClick = { nonSpecificAppSheetState.show() }) {
            Text("Show bottom sheet")
        }
    }

    AppBottomSheet(
        state = nonSpecificAppSheetState,
        fullScreen = true,
        isHeaderEnabled = false,
    ) { hide ->
        EnterProgramFlow(
            onFinish = {},
            sheetState = nonSpecificAppSheetState,
            hide = hide
        )
    }
}