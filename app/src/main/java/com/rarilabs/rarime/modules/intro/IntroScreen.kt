package com.rarilabs.rarime.modules.intro

import androidx.annotation.RawRes
import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.rarilabs.rarime.R
import com.rarilabs.rarime.ui.base.ButtonSize
import com.rarilabs.rarime.ui.components.AppAnimation
import com.rarilabs.rarime.ui.components.AppBottomSheet
import com.rarilabs.rarime.ui.components.AppLogo
import com.rarilabs.rarime.ui.components.PrimaryButton
import com.rarilabs.rarime.ui.components.rememberAppSheetState
import com.rarilabs.rarime.ui.theme.RarimeTheme
import com.rarilabs.rarime.util.Screen

private enum class IntroStep(
    @StringRes val title: Int,
    @StringRes val text: Int,
    @RawRes val animation: Int,
    val animationWidth: Int,
) {
    Welcome(
        title = R.string.intro_step_1_title,
        text = R.string.intro_step_1_text,
        animation = R.raw.anim_intro_welcome,
        animationWidth = 390
    ),
    Identity(
        title = R.string.intro_step_2_title,
        text = R.string.intro_step_2_text,
        animation = R.raw.anim_intro_incognito,
        animationWidth = 342
    ),
    Privacy(
        title = R.string.intro_step_3_title,
        text = R.string.intro_step_3_text,
        animation = R.raw.anim_intro_proofs,
        animationWidth = 320
    ),
    Rewards(
        title = R.string.intro_step_4_title,
        text = R.string.intro_step_4_text,
        animation = R.raw.anim_intro_rewards,
        animationWidth = 220
    )
}

@Composable
fun IntroScreen(
    navigate: (String) -> Unit
) {
    val introSteps = rememberSaveable {
        listOf(
            IntroStep.Welcome,
            IntroStep.Identity,
            IntroStep.Privacy,
            IntroStep.Rewards
        )
    }
    val stepState = rememberPagerState(pageCount = { 1 })

    Column(
        verticalArrangement = Arrangement.spacedBy(48.dp),
        modifier = Modifier
            .fillMaxHeight()
            .background(RarimeTheme.colors.backgroundPrimary)
            .padding(bottom = 20.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Column(modifier = Modifier.weight(1f)) {
            HorizontalPager(
                state = stepState,
                verticalAlignment = Alignment.Top,
            ) { page ->
                StepView(introSteps[page])
            }
        }
        Column(
            modifier = Modifier.padding(horizontal = 24.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            AuthorizationMethodsList(
                listOf(
                    AuthorizationMethod(
                        title = stringResource(id = R.string.create_identity_selector_option_1),
                        icon = R.drawable.ic_plus,
                        onSelect = {
                            navigate(Screen.Register.NewIdentity.route)
                        }
                    ),
                    AuthorizationMethod(
                        title = stringResource(id = R.string.create_identity_selector_option_2),
                        icon = R.drawable.ic_share_1,
                        onSelect = {
                            navigate(Screen.Register.ImportIdentity.route)
                        }
                    ),
                )
            )
        }
    }
}

@Composable
private fun StepView(step: IntroStep) {
    Column(verticalArrangement = Arrangement.spacedBy(24.dp)) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            contentAlignment = Alignment.Center
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(30.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .padding(horizontal = 24.dp)
                    .fillMaxWidth()
            ) {
                AppLogo()
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text(
                        text = stringResource(step.text),
                        style = RarimeTheme.typography.subtitle4,
                        color = RarimeTheme.colors.textSecondary,
                        textAlign = TextAlign.Center
                    )
                    Text(
                        text = stringResource(step.title),
                        style = RarimeTheme.typography.h1,
                        color = RarimeTheme.colors.textPrimary,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}

@Preview
@Composable
private fun IntroScreenPreview() {
    IntroScreen(navigate = {})
}