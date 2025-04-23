package com.rarilabs.rarime.modules.home.v2.details

import android.Manifest
import android.graphics.Bitmap
import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.rarilabs.rarime.R
import com.rarilabs.rarime.modules.digitalLikeness.DigitalLikenessCamera
import com.rarilabs.rarime.modules.digitalLikeness.DigitalLikenessProcessing
import com.rarilabs.rarime.modules.main.ScreenInsets
import com.rarilabs.rarime.ui.base.ButtonSize
import com.rarilabs.rarime.ui.components.AppBottomSheet
import com.rarilabs.rarime.ui.components.HorizontalDivider
import com.rarilabs.rarime.ui.components.PrimaryButton
import com.rarilabs.rarime.ui.components.TransparentButton
import com.rarilabs.rarime.ui.components.rememberAppSheetState
import com.rarilabs.rarime.ui.theme.RarimeTheme
import com.rarilabs.rarime.util.PrevireSharedAnimationProvider

const val ALREADY_SET = "49,421"

@OptIn(ExperimentalSharedTransitionApi::class, ExperimentalPermissionsApi::class)
@Composable
fun DigitalLikeness(
    modifier: Modifier = Modifier,
    id: Int,
    onBack: () -> Unit,
    innerPaddings: Map<ScreenInsets, Number>,
    sharedTransitionScope: SharedTransitionScope,
    animatedContentScope: AnimatedContentScope,
    navigate: (String) -> Unit = {}
) {
    val appSheetState = rememberAppSheetState()
    val cameraPermissionState = rememberPermissionState(Manifest.permission.CAMERA)
    var showOnGrant by remember { mutableStateOf(false) }

    val backgroundGradient = RarimeTheme.colors.gradient7
    val ruleSheetState = rememberAppSheetState(false)

    LaunchedEffect(cameraPermissionState.status.isGranted, showOnGrant) {
        if (!cameraPermissionState.status.isGranted) {
            cameraPermissionState.launchPermissionRequest()
        }

        if (cameraPermissionState.status.isGranted && showOnGrant) {
            appSheetState.show()
            showOnGrant = false
        }
    }

    if (cameraPermissionState.status.isGranted) {
        AppBottomSheet(
            state = appSheetState,
            fullScreen = false,
            isHeaderEnabled = false,
            isWindowInsetsEnabled = false,
        ) {

            var selectedBitmap: Bitmap? by remember { mutableStateOf(null) }

            if (selectedBitmap == null) {
                DigitalLikenessCamera {
                    selectedBitmap = it
                }
            } else {
                DigitalLikenessProcessing(
                    modifier = Modifier.padding(vertical = 16.dp),
                    onNext = {})
            }
        }
    }

    AppBottomSheet(state = ruleSheetState) {
        // TODO: Add content
    }


    val props =
        DetailsProperties(
            id = id,
            header = stringResource(R.string.digital_likeness),
            subTitle = stringResource(R.string.set_a_rule),
            caption = stringResource(R.string.first_human_ai_contract),
            imageId = R.drawable.drawable_digital_likeness,
            backgroundGradient = backgroundGradient,
            imageModifier = Modifier
                .padding(horizontal = 52.dp)
                .padding(bottom = 40.dp)
        )

    BaseDetailsScreen(
        properties = props,
        innerPaddings = innerPaddings,
        sharedTransitionScope = sharedTransitionScope,
        animatedContentScope = animatedContentScope,
        onBack = onBack,
        body = {
            Column {
                Text(
                    style = RarimeTheme.typography.body3,
                    color = RarimeTheme.colors.baseBlack.copy(alpha = 0.5f),
                    text = stringResource(R.string.digital_likeness_description_1)
                )
                Spacer(modifier = Modifier.height(24.dp))
                Text(
                    style = RarimeTheme.typography.body3,
                    color = RarimeTheme.colors.baseBlack.copy(alpha = 0.5f),
                    text = stringResource(R.string.digital_likeness_description_2)
                )
                Spacer(modifier = Modifier.height(24.dp))
                Text(
                    style = RarimeTheme.typography.body3,
                    color = RarimeTheme.colors.baseBlack.copy(alpha = 0.5f),
                    text = stringResource(R.string.digital_likeness_description_3)
                )
            }
        },
        footer = {
            HorizontalDivider()
            Spacer(modifier = Modifier.height(24.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(ALREADY_SET, style = RarimeTheme.typography.h4)
                    Text(
                        stringResource(R.string.digital_likeness_registered_lbl),
                        style = RarimeTheme.typography.body4,
                        color = RarimeTheme.colors.textSecondary
                    )
                }
                PrimaryButton(
                    onClick = { ruleSheetState.show() },
                    text = "Set a rule",
                    size = ButtonSize.Large
                )

                TransparentButton(
                    size = ButtonSize.Large,
                    modifier = Modifier.fillMaxWidth(),
                    text = "Let’s Start",
                    onClick = {
                        if (cameraPermissionState.status.isGranted) {
                            // already have it → just open
                            appSheetState.show()
                        } else {
                            // request, and mark that we want to show afterwards
                            showOnGrant = true
                            cameraPermissionState.launchPermissionRequest()
                        }
                    }
                )
            }
        }
    )
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Preview
@Composable
private fun CreateIdentityDetailsPreview() {

    PrevireSharedAnimationProvider { state, anim ->
        DigitalLikeness(
            id = 0,
            sharedTransitionScope = state,
            animatedContentScope = anim,
            innerPaddings = mapOf(ScreenInsets.TOP to 23, ScreenInsets.BOTTOM to 12),
            onBack = {}
        )
    }
}
