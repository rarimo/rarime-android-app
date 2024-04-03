package com.distributedLab.rarime.modules.intro

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.distributedLab.rarime.R
import com.distributedLab.rarime.modules.main.Screen
import com.distributedLab.rarime.ui.components.HorizontalDivider
import com.distributedLab.rarime.ui.components.PrimaryButton
import com.distributedLab.rarime.ui.components.SecondaryTextButton
import com.distributedLab.rarime.ui.components.rememberAppSheetState
import com.distributedLab.rarime.ui.theme.RarimeTheme
import kotlinx.coroutines.launch

private enum class IntroStep(
    @StringRes val title: Int,
    @StringRes val text: Int,
    @DrawableRes val image: Int
) {
    Welcome(
        title = R.string.intro_step_1_title,
        text = R.string.intro_step_1_text,
        image = R.drawable.intro_app
    ),
    Identity(
        title = R.string.intro_step_2_title,
        text = R.string.intro_step_2_text,
        image = R.drawable.intro_identity
    ),
    Privacy(
        title = R.string.intro_step_3_title,
        text = R.string.intro_step_3_text,
        image = R.drawable.intro_privacy
    ),
    Rewards(
        title = R.string.intro_step_4_title,
        text = R.string.intro_step_4_text,
        image = R.drawable.intro_gifts
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
    val sheetState = rememberAppSheetState()
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
                        text = stringResource(R.string.get_started_btn),
                        modifier = Modifier.fillMaxWidth(),
                        onClick = { sheetState.show() }
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

        GetStartedSheet(
            sheetState = sheetState,
            onCreateClick = {
                navigateTo(Screen.Register.NewPhrase.route)
            },
            onImportClick = {
                navigateTo(Screen.Register.ImportPhrase.route)
            }
        )
    }
}

@Composable
private fun StepView(step: IntroStep) {
    Column(verticalArrangement = Arrangement.spacedBy(70.dp)) {
        Image(
            painter = painterResource(id = step.image),
            contentDescription = null,
            contentScale = ContentScale.FillWidth,
            modifier = Modifier.fillMaxWidth()
        )
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

@Composable
private fun StepIndicator(itemsCount: Int, selectedIndex: Int) {
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        repeat(itemsCount) { index ->
            Box(
                modifier = Modifier
                    .width(if (index == selectedIndex) 16.dp else 8.dp)
                    .height(8.dp)
                    .background(
                        color = if (index == selectedIndex) RarimeTheme.colors.primaryMain else RarimeTheme.colors.componentPrimary,
                        shape = CircleShape
                    )
            ) {}
        }
    }
}

@Preview
@Composable
private fun IntroScreenPreview() {
    IntroScreen(navigateTo = {})
}