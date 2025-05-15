package com.rarilabs.rarime.modules.passportScan.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.rarilabs.rarime.R
import com.rarilabs.rarime.ui.base.ButtonSize
import com.rarilabs.rarime.ui.components.ActionCard
import com.rarilabs.rarime.ui.components.AppBottomSheet
import com.rarilabs.rarime.ui.components.CircledBadge
import com.rarilabs.rarime.ui.components.GifViewer
import com.rarilabs.rarime.ui.components.PrimaryButton
import com.rarilabs.rarime.ui.components.rememberAppSheetState
import com.rarilabs.rarime.ui.theme.RarimeTheme

enum class SpecificPassportGuide {
    Other,
    USA
}

@Composable
fun ScanGuidesTrigger(
    modifier: Modifier = Modifier,
    type: SpecificPassportGuide = SpecificPassportGuide.Other
) {

    fun getMrzHintType(type: SpecificPassportGuide): Int {
        return when (type) {
            SpecificPassportGuide.USA -> R.raw.scan_mrz_usa
            SpecificPassportGuide.Other -> R.raw.scan_mrz_external
        }
    }

    fun getNFCHintType(type: SpecificPassportGuide): Int {
        return when (type) {
            SpecificPassportGuide.USA -> R.raw.read_nfc_usa
            SpecificPassportGuide.Other -> R.raw.read_nfc_external
        }
    }

    val guideSheetState = rememberAppSheetState(false)

    ActionCard(
        title = stringResource(id = R.string.scan_mrzstep_content_tutorial_title),
        description = stringResource(id = R.string.scan_mrzstep_content_tutorial_desc),
        leadingContent = {
            Box(
                modifier = Modifier.size(52.dp),
                contentAlignment = Alignment.Center,
            ) {
                Image(
                    modifier = Modifier.clip(RoundedCornerShape(8.dp)),
                    colorFilter = ColorFilter.tint(
                        RarimeTheme.colors.componentPrimary,
                        blendMode = BlendMode.Darken
                    ),
                    painter = painterResource(id = R.drawable.how_to_scan_preview),
                    contentDescription = ""
                )

                CircledBadge(
                    containerSize = 32,
                    contentSize = 16,
                    iconId = R.drawable.ic_caret_right,
                    containerColor = RarimeTheme.colors.baseBlack,
                    contentColor = RarimeTheme.colors.baseWhite,
                )
            }
        },
        onClick = { guideSheetState.show() }
    )

    AppBottomSheet(
        state = guideSheetState,
        fullScreen = true,
    ) { hide ->
        var step by remember { mutableStateOf(1) }

        AnimatedVisibility(
            visible = step.equals(1),
            enter = fadeIn() + slideInHorizontally(initialOffsetX = { it }),
            exit = fadeOut() + slideOutHorizontally(targetOffsetX = { -it })
        ) {
            ScanGuides(
                mediaId = R.raw.phone_case_warning,
                title = stringResource(id = R.string.scan_mrzstep_content_bottom_sheet_case_title),
                desc = stringResource(id = R.string.scan_mrzstep_content_bottom_sheet_case_description),
                btnText = stringResource(id = R.string.scan_mrzstep_content_bottom_sheet_case_btn),
                onPress = { step += 1 }
            )
        }
        AnimatedVisibility(
            visible = step.equals(2),
            enter = fadeIn() + slideInHorizontally(initialOffsetX = { it }),
            exit = fadeOut() + slideOutHorizontally(targetOffsetX = { -it })
        ) {
            ScanGuides(
                mediaId = getMrzHintType(type = type),
                title = stringResource(id = R.string.scan_mrzstep_content_bottom_sheet_mrz_title),
                desc = stringResource(id = R.string.scan_mrzstep_content_bottom_sheet_mrz_description),
                btnText = stringResource(id = R.string.scan_mrzstep_content_bottom_sheet_mrz_btn),
                onPress = { step += 1 }
            )
        }
        AnimatedVisibility(
            visible = step.equals(3),
            enter = fadeIn() + slideInHorizontally(initialOffsetX = { it }),
            exit = fadeOut() + slideOutHorizontally(targetOffsetX = { -it })
        ) {

            ScanGuides(
                mediaId = getNFCHintType(type),
                title = stringResource(id = R.string.scan_mrzstep_content_bottom_sheet_nfc_title),
                desc = stringResource(id = R.string.scan_mrzstep_content_bottom_sheet_nfc_description),
                btnText = stringResource(id = R.string.scan_mrzstep_content_bottom_sheet_nfc_btn),
                onPress = { hide {} }
            )
        }
    }
}

@Composable
fun ScanGuides(
    mediaId: Int, title: String, desc: String, btnText: String, onPress: () -> Unit
) {
    Column(
        modifier = Modifier
            .padding(24.dp)
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Spacer(modifier = Modifier.height(48.dp))
        GifViewer(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp)),
            gifId = mediaId
        )
        Spacer(modifier = Modifier.height(40.dp))
        Text(
            text = title,
            style = RarimeTheme.typography.h4,
            color = RarimeTheme.colors.textPrimary,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = desc,
            style = RarimeTheme.typography.body3,
            color = RarimeTheme.colors.textSecondary,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.weight(1f))
        Spacer(modifier = Modifier.height(16.dp))
        PrimaryButton(modifier = Modifier.fillMaxWidth(),
            size = ButtonSize.Large,
            text = btnText,
            onClick = { onPress() })

        Spacer(modifier = Modifier.windowInsetsPadding(WindowInsets.navigationBars))
    }
}

@Preview(showBackground = true)
@Composable
private fun ScanGuidesPreview() {
    ScanGuides(
        mediaId = R.raw.phone_case_warning,
        title = stringResource(id = R.string.scan_mrzstep_content_bottom_sheet_nfc_title),
        desc = stringResource(id = R.string.scan_mrzstep_content_bottom_sheet_nfc_description),
        btnText = stringResource(id = R.string.scan_mrzstep_content_bottom_sheet_nfc_btn),
        onPress = { }
    )
}