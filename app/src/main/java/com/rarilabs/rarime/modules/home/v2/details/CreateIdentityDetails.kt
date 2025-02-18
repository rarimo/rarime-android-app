package com.rarilabs.rarime.modules.home.v2.details

import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import com.rarilabs.rarime.R
import com.rarilabs.rarime.ui.components.PrimaryButton
import com.rarilabs.rarime.util.PrevireSharedAnimationProvider


@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun CreateIdentityDetails(
    modifier: Modifier = Modifier,
    id: Int,
    onBack: () -> Unit,
    sharedTransitionScope: SharedTransitionScope,
    animatedContentScope: AnimatedContentScope,
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
        sharedTransitionScope = sharedTransitionScope,
        animatedContentScope = animatedContentScope,
        onBack = onBack,
        footer = {
            Column {
                Text("This app is where you privately store your digital identities, enabling you to go incognito across the web.")
                PrimaryButton(text = "Lets start", onClick = {})
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
            onBack = {})

    }
}