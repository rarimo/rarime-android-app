package com.distributedLab.rarime.ui.components.enter_program

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import com.distributedLab.rarime.R
import com.distributedLab.rarime.ui.components.AppBottomSheet
import com.distributedLab.rarime.ui.components.AppSheetState
import com.distributedLab.rarime.ui.components.HideSheetFn
import com.distributedLab.rarime.ui.components.PrimaryButton
import com.distributedLab.rarime.ui.components.PrimaryTextButton
import com.distributedLab.rarime.ui.components.enter_program.components.AboutProgram
import com.distributedLab.rarime.ui.components.enter_program.components.Invitation
import com.distributedLab.rarime.ui.components.enter_program.components.PolicyConfirmation
import com.distributedLab.rarime.ui.components.rememberAppSheetState
import com.distributedLab.rarime.ui.theme.RarimeTheme
import com.distributedLab.rarime.util.Screen

enum class UNSPECIFIED_PASSPORT_STEPS(val value: Int) {
    INVITATION(1),
    POLICY_CONFIRMATION(3),

    ABOUT_PROGRAM(5),
}

@Composable
fun EnterProgramFlow(
    navigate: () -> Unit,
    sheetState: AppSheetState,
    hide: HideSheetFn,
) {
    var currStep by remember {
        mutableStateOf(UNSPECIFIED_PASSPORT_STEPS.INVITATION)
    }

    AnimatedVisibility(
        visible = currStep.equals(UNSPECIFIED_PASSPORT_STEPS.INVITATION),
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

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Invitation(
                    onNext = { currStep = UNSPECIFIED_PASSPORT_STEPS.POLICY_CONFIRMATION },
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
                    hide({
                        navigate()
                    })
                }
            )
        }
    }

    AnimatedVisibility(
        visible = currStep.equals(UNSPECIFIED_PASSPORT_STEPS.ABOUT_PROGRAM),
        enter = fadeIn() + slideInHorizontally(initialOffsetX = { it }),
        exit = fadeOut() + slideOutHorizontally(targetOffsetX = { -it })
    ) {
        Column {
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
            navigate = {},
            sheetState = nonSpecificAppSheetState,
            hide = hide
        )
    }
}