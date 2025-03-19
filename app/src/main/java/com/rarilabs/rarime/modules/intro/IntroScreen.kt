package com.rarilabs.rarime.modules.intro

import androidx.annotation.RawRes
import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.rarilabs.rarime.R
import com.rarilabs.rarime.ui.base.ButtonSize
import com.rarilabs.rarime.ui.components.AppAnimation
import com.rarilabs.rarime.ui.components.AppBottomSheet
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
    val introSteps = remember {
        listOf(
            IntroStep.Welcome,
            IntroStep.Identity,
            IntroStep.Privacy,
            IntroStep.Rewards
        )
    }
    val stepState = rememberPagerState(pageCount = { 1 })
    val sheetState = rememberAppSheetState()

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
            PrimaryButton(
                text = stringResource(R.string.create_account_btn),
                size = ButtonSize.Large,
                modifier = Modifier.fillMaxWidth(),
                onClick = { sheetState.show() }
            )
        }

        AppBottomSheet(state = sheetState) {
            CreateIdentityVariantsSelector(
                listOf(
                    IdentityVariant(
                        title = stringResource(id = R.string.create_identity_selector_option_1),
                        subtitle = stringResource(id = R.string.create_identity_selector_option_1_subtitle),
                        icon = R.drawable.ic_user_plus,
                        onSelect = {
                            navigate(Screen.Register.NewIdentity.route)
                        }
                    ),
                    IdentityVariant(
                        title = stringResource(id = R.string.create_identity_selector_option_2),
                        subtitle = stringResource(id = R.string.create_identity_selector_option_2_subtitle),
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
                .weight(1f)
        ) {
            AppAnimation(
                id = step.animation,
                modifier = Modifier
                    .align(Alignment.Center)
                    .size(step.animationWidth.dp)
            )
        }
        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.padding(horizontal = 24.dp)
        ) {

            Text(
                text = stringResource(step.title),
                style = RarimeTheme.typography.h2,
                color = RarimeTheme.colors.textPrimary
            )
            Text(
                text = stringResource(step.text),
                style = RarimeTheme.typography.body3,
                color = RarimeTheme.colors.textSecondary
            )
        }
    }
}

@Preview
@Composable
private fun IntroScreenPreview() {
    IntroScreen(navigate = {})
}