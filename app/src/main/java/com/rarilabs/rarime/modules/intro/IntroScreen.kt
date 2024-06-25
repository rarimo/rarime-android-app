package com.rarilabs.rarime.modules.intro

import androidx.annotation.RawRes
import androidx.annotation.StringRes
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.rarilabs.rarime.R
import com.rarilabs.rarime.ui.components.AppAnimation
import com.rarilabs.rarime.ui.components.HorizontalDivider
import com.rarilabs.rarime.ui.components.PrimaryButton
import com.rarilabs.rarime.ui.components.SecondaryTextButton
import com.rarilabs.rarime.ui.components.StepIndicator
import com.rarilabs.rarime.ui.theme.RarimeTheme
import com.rarilabs.rarime.util.Screen
import kotlinx.coroutines.launch

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

private val introSteps = listOf(
    IntroStep.Welcome,
    IntroStep.Identity,
    IntroStep.Privacy,
    IntroStep.Rewards
)

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun IntroScreen(navigateTo: (route: String) -> Unit) {
    val stepState = rememberPagerState(pageCount = { introSteps.size })
    val coroutineScope = rememberCoroutineScope()

    val isLastStep = stepState.currentPage == introSteps.size - 1

    Surface(color = RarimeTheme.colors.backgroundPrimary) {
        Column(
            verticalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxHeight()
                .verticalScroll(rememberScrollState())
        ) {
            Column {
                SecondaryTextButton(
                    text = stringResource(R.string.skip_btn),
                    modifier = Modifier
                        .padding(top = 20.dp, bottom = 10.dp, end = 24.dp)
                        .align(Alignment.End)
                        .alpha(if (isLastStep) 0f else 1f),
                    onClick = {
                        coroutineScope.launch {
                            stepState.animateScrollToPage(introSteps.size - 1)
                        }
                    },
                )
                HorizontalPager(
                    state = stepState,
                    verticalAlignment = Alignment.Top,
                ) { page ->
                    StepView(introSteps[page])
                }
            }
            Column(
                modifier = Modifier
                    .padding(horizontal = 24.dp)
                    .padding(top = 24.dp, bottom = 24.dp),
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                HorizontalDivider()
                if (isLastStep) {
                    PrimaryButton(
                        text = stringResource(R.string.create_account_btn),
                        modifier = Modifier.fillMaxWidth(),
                        onClick = { navigateTo(Screen.Register.NewIdentity.route) }
                    )
                } else {
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        StepIndicator(
                            itemsCount = introSteps.size,
                            selectedIndex = stepState.currentPage
                        )
                        PrimaryButton(
                            text = stringResource(R.string.next_btn),
                            rightIcon = R.drawable.ic_arrow_right,
                            onClick = {
                                coroutineScope.launch {
                                    stepState.animateScrollToPage(stepState.currentPage + 1)
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun StepView(step: IntroStep) {
    Column(verticalArrangement = Arrangement.spacedBy(24.dp)) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(390.dp)
        ) {
            AppAnimation(
                id = step.animation,
                modifier = Modifier
                    .align(Alignment.Center)
                    .size(step.animationWidth.dp)
            )
        }
        Spacer(modifier = Modifier.height(40.dp))
        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.padding(horizontal = 24.dp)
        ) {

            Text(
                text = stringResource(step.title),
                style = RarimeTheme.typography.h4,
                color = RarimeTheme.colors.textPrimary
            )
            Text(
                text = stringResource(step.text),
                style = RarimeTheme.typography.body2,
                color = RarimeTheme.colors.textSecondary
            )
        }
    }
}

@Preview
@Composable
private fun IntroScreenPreview() {
    IntroScreen(navigateTo = {})
}