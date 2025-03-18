package com.rarilabs.rarime.modules.home.v2.details

import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.rarilabs.rarime.R
import com.rarilabs.rarime.modules.main.ScreenInsets
import com.rarilabs.rarime.ui.base.ButtonSize
import com.rarilabs.rarime.ui.components.TransparentButton
import com.rarilabs.rarime.ui.theme.RarimeTheme
import com.rarilabs.rarime.util.PrevireSharedAnimationProvider
import com.rarilabs.rarime.util.Screen


@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun CreateIdentityDetails(
    modifier: Modifier = Modifier,
    id: Int,
    onBack: () -> Unit,
    innerPaddings: Map<ScreenInsets, Number>,
    sharedTransitionScope: SharedTransitionScope,
    animatedContentScope: AnimatedContentScope,
    navigate: (String) -> Unit = {}
) {

    val props = remember {
        DetailsProperties(
            id = id,
            header = "Your Device",
            subTitle = "Your Identity",
            imageId = R.drawable.drawable_hand_phone,
            backgroundGradient = Brush.linearGradient(
                colors = listOf(
                    Color(0xFF9AFE8A), Color(0xFF8AFECC)
                )
            )
        )
    }

    BaseDetailsScreen(
        properties = props,
        innerPaddings = innerPaddings,
        sharedTransitionScope = sharedTransitionScope,
        animatedContentScope = animatedContentScope,
        onBack = onBack,
        footer = {
            Column {
                Text(
                    style = RarimeTheme.typography.body3,
                    color = RarimeTheme.colors.textSecondary,
                    text = "This app is where you privately store your digital identities, enabling you to go incognito across the web."
                )

                Spacer(modifier = Modifier.height(24.dp))
                TransparentButton(
                    size = ButtonSize.Large,
                    modifier = Modifier.fillMaxWidth(),
                    text = "Let’s Start",
                    onClick = {
                        navigate(Screen.ScanPassport.ScanPassportPoints.route)
                    }
                )

                Spacer(modifier = Modifier.height(16.dp))
            }
        },
    )
}


@OptIn(ExperimentalSharedTransitionApi::class)
@Preview
@Composable
private fun CreateIdentityDetailsPreview() {

    PrevireSharedAnimationProvider { state, anim ->
        CreateIdentityDetails(
            id = 0,
            sharedTransitionScope = state,
            animatedContentScope = anim,
            innerPaddings = mapOf(ScreenInsets.TOP to 23, ScreenInsets.BOTTOM to 12),
            onBack = {})

    }
}