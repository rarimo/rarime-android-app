package com.rarilabs.rarime.modules.home.v2.details.votes

import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.absolutePadding
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.rarilabs.rarime.R
import com.rarilabs.rarime.modules.home.v2.details.BaseDetailsScreen
import com.rarilabs.rarime.modules.home.v2.details.DetailsProperties
import com.rarilabs.rarime.modules.main.LocalMainViewModel
import com.rarilabs.rarime.modules.main.ScreenInsets
import com.rarilabs.rarime.ui.base.ButtonSize
import com.rarilabs.rarime.ui.components.AppIcon
import com.rarilabs.rarime.ui.components.CardContainer
import com.rarilabs.rarime.ui.components.HorizontalDivider
import com.rarilabs.rarime.ui.components.TransparentButton
import com.rarilabs.rarime.ui.theme.RarimeTheme
import com.rarilabs.rarime.util.PrevireSharedAnimationProvider


@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun VotesScreen(
    modifier: Modifier = Modifier,
    id: Int,
    onBack: () -> Unit,
    sharedTransitionScope: SharedTransitionScope,
    animatedContentScope: AnimatedContentScope,
) {
    val mainViewModel = LocalMainViewModel.current
    val screenInsets by mainViewModel.screenInsets.collectAsState()

    val props = DetailsProperties(
        id = id,
        header = "Freedomtool",
        subTitle = "Voting",
        imageId = R.drawable.freedomtool_bg,
        backgroundGradient = Brush.linearGradient(
            colors = listOf(
                Color(0xFFD5FEC8),
                Color(0xFF80ED99)
            )
        )
    )

    BaseDetailsScreen(
        modifier = modifier
            .absolutePadding(
                top = (screenInsets.get(ScreenInsets.TOP)?.toFloat() ?: 0f).dp,
                bottom = (screenInsets.get(ScreenInsets.BOTTOM)?.toFloat() ?: 0f).dp,
            )
            .verticalScroll(rememberScrollState()),
        properties = props,
        sharedTransitionScope = sharedTransitionScope,
        animatedContentScope = animatedContentScope,
        onBack = onBack,
        footer = {
            Column (
                verticalArrangement = Arrangement.spacedBy(24.dp),
            ) {
                Text(
                    style = RarimeTheme.typography.body3,
                    color = RarimeTheme.colors.textSecondary,
                    text = "An identification and privacy solution that revolutionizes polling, surveying and election processes"
                )

                Spacer(modifier = Modifier.height(24.dp))

                Row (
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        modifier = Modifier
                            .width(56.dp)
                            .height(56.dp)
                            .background(RarimeTheme.colors.componentPrimary, RoundedCornerShape(20.dp)),
                        colors = IconButtonDefaults.iconButtonColors(
                            containerColor = Color.Transparent,
                            contentColor = RarimeTheme.colors.textPrimary,
                            disabledContainerColor = RarimeTheme.colors.componentDisabled,
                            disabledContentColor = RarimeTheme.colors.textDisabled
                        ),
                        onClick = {},
                    ) {
                        AppIcon(id = R.drawable.ic_plus)
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    TransparentButton(
                        modifier = Modifier.weight(1f).height(56.dp),
                        size = ButtonSize.Large,
                        text = "Scan a QR",
                        onClick = {}
                    )
                }

                HorizontalDivider(
                    modifier = Modifier
                        .background(RarimeTheme.colors.componentPrimary)
                        .fillMaxWidth()
                        .height(2.dp)
                )

                VoteResultsCard()
            }
        },
    )
}


@OptIn(ExperimentalSharedTransitionApi::class)
@Preview
@Composable
private fun VotesScreenPreview() {
    PrevireSharedAnimationProvider { state, anim ->
        VotesScreen(
            id = 0,
            sharedTransitionScope = state,
            animatedContentScope = anim,
            onBack = {}
        )

    }
}