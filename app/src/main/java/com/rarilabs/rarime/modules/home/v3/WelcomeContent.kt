package com.rarilabs.rarime.modules.home.v3

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.rarilabs.rarime.R
import com.rarilabs.rarime.ui.base.ButtonSize
import com.rarilabs.rarime.ui.components.CircledBadge
import com.rarilabs.rarime.ui.components.HorizontalDivider
import com.rarilabs.rarime.ui.components.HorizontalPageIndicator
import com.rarilabs.rarime.ui.components.PrimaryButton
import com.rarilabs.rarime.ui.theme.RarimeTheme
import kotlinx.coroutines.launch

data class WelcomeCardContent(
    val title: String,
    val imageId: Int,
    val description: String,
    val imageHeight: Dp,
    val accentColor: Color
)


@Composable
fun WelcomeBottomSheet(
    onClose: () -> Unit
) {

    val welcomeAccentColor1 = RarimeTheme.colors.welcomeAccent1
    val welcomeAccentColor2 = RarimeTheme.colors.welcomeAccent2
    val welcomeAccentColor3 = RarimeTheme.colors.welcomeAccent3
    val welcomeAccentColor4 = RarimeTheme.colors.welcomeAccent4

    val context = LocalContext.current

    val scope = rememberCoroutineScope()

    val cardContent = remember {
        listOf(
            WelcomeCardContent(
                title = context.getString(R.string.welcome_card1_title),
                imageId = R.drawable.welcome_cat,
                description = context.getString(R.string.welcome_card1_description),
                imageHeight = 268.dp,
                accentColor = welcomeAccentColor1

            ), WelcomeCardContent(
                title = context.getString(R.string.welcome_card2_title),
                imageId = R.drawable.welcome_lock,
                description = context.getString(R.string.welcome_card2_description),
                imageHeight = 206.dp,
                accentColor = welcomeAccentColor2


            ), WelcomeCardContent(
                title = context.getString(R.string.welcome_card3_title),
                imageId = R.drawable.welcome_identity_card,
                description = context.getString(R.string.welcome_card3_description),
                imageHeight = 224.dp,
                accentColor = welcomeAccentColor3


            ), WelcomeCardContent(
                title = context.getString(R.string.welcome_card4_title),
                imageId = R.drawable.welcome_cards,
                description = context.getString(R.string.welcome_card4_description),
                imageHeight = 191.dp,
                accentColor = welcomeAccentColor4

            )
        )
    }

    val pagerState = rememberPagerState { cardContent.size }


    Box {
        val animatedColor by animateColorAsState(
            targetValue = cardContent[pagerState.currentPage].accentColor,
            animationSpec = tween(durationMillis = 500)
        )
        Column(modifier = Modifier.matchParentSize()) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(225.dp)
                    .background(animatedColor)
            ) {}
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxSize()
                    .background(RarimeTheme.colors.backgroundSurface1)
            ) {}
        }

        Column {
            HorizontalPager(
                state = pagerState
            ) {
                BaseWelcomeContent(
                    modifier = Modifier.padding(start = 24.dp, top = 24.dp, end = 24.dp),
                    imageId = cardContent[it].imageId,
                    title = cardContent[it].title,
                    description = cardContent[it].description,
                    imageHeight = cardContent[it].imageHeight
                )
            }

            Spacer(Modifier.height(32.dp))


            HorizontalDivider()

            Spacer(Modifier.height(32.dp))

            WelcomeBottomBar(
                modifier = Modifier.padding(end = 24.dp, start = 24.dp),
                selectedPage = pagerState.currentPage,
                numberOfPages = pagerState.pageCount,
                onNext = {
                    scope.launch {
                        pagerState.animateScrollToPage(
                            pagerState.currentPage + 1, animationSpec = tween(
                                durationMillis = 500,
                            )
                        )
                    }
                },
                onExplore = onClose
            )
        }
    }

}


@Composable
fun BaseWelcomeContent(
    modifier: Modifier = Modifier, imageId: Int, title: String, description: String, imageHeight: Dp
) {

    Column(modifier) {
        Row(modifier = Modifier.fillMaxWidth()) {
            CircledBadge(
                iconId = R.drawable.ic_rarime,
                containerColor = RarimeTheme.colors.componentPrimary,
                contentSize = 24,
                containerSize = 40
            )
            Spacer(Modifier.weight(1f))
            Column(
                modifier = Modifier.height(268.dp),
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.Center
            ) {
                Image(
                    modifier = Modifier.height(imageHeight),
                    contentScale = ContentScale.Crop,
                    painter = painterResource(imageId),
                    contentDescription = ""
                )
            }

            Spacer(Modifier.weight(0.5f))
        }

        Column(modifier = Modifier.padding(top = 32.dp)) {
            Text(title, style = RarimeTheme.typography.h2, color = RarimeTheme.colors.textPrimary)
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                description,
                style = RarimeTheme.typography.body3,
                color = RarimeTheme.colors.textSecondary
            )


        }
    }
}


@Composable
fun WelcomeBottomBar(
    modifier: Modifier = Modifier,
    selectedPage: Int,
    numberOfPages: Int,
    onNext: () -> Unit,
    onExplore: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .then(modifier),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (numberOfPages - 1 == selectedPage) {
            PrimaryButton(
                modifier = Modifier.fillMaxWidth(),
                text = "Explore Apps",
                onClick = onExplore,
                size = ButtonSize.Large
            )
        } else {

            HorizontalPageIndicator(
                defaultRadius = 6.dp,
                selectedLength = 16.dp,
                space = 8.dp,
                selectedPage = selectedPage,
                numberOfPages = numberOfPages,
            )

            PrimaryButton(
                size = ButtonSize.Large,
                onClick = onNext,
                text = "Next",
                rightIcon = R.drawable.ic_arrow_right
            )

        }
    }
}


@Preview
@Composable
private fun WelcomeBottomSheetPreview() {
    WelcomeBottomSheet {}
}

@Preview
@Composable
private fun WelcomeBottomBarPreview() {
    var currentPage by remember { mutableIntStateOf(0) }
    val totalPages = remember { 5 }

    Surface {
        WelcomeBottomBar(selectedPage = currentPage, numberOfPages = totalPages, onNext = {
            currentPage += 1
        }, onExplore = {

        }

        )
    }

}