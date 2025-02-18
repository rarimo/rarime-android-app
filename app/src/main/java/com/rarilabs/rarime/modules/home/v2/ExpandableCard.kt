package com.rarilabs.rarime.modules.home.v2

import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun ExpandableCard(
    card: CardContent,
    sharedTransitionScope: SharedTransitionScope,
    animatedContentScope: AnimatedContentScope,
    modifier: Modifier = Modifier,
    onExpandChange: (Boolean) -> Unit
) {
//    var expanded by remember { mutableStateOf(false) }
//
//    val onClick = {
//        expanded = !expanded
//        onExpandChange(expanded)
//    }
//
//    AnimatedContent(
//        targetState = expanded,
//        transitionSpec = {
//            fadeIn() togetherWith fadeOut()
//        },
//        label = "card_transition"
//    ) { targetExpanded ->
//        val sharedKey = "card-shared-${card.properties.header}"
//
//        with(sharedTransitionScope) {
//            HomeCard(
//                modifier = modifier
//                    .sharedElement(
//                        state = rememberSharedContentState(key = sharedKey),
//                        animatedVisibilityScope = animatedContentScope
//                    )
//                    .clickable { onClick() },
//                cardProperties = card.properties,
//                footer = {},//if (targetExpanded) card.expandedFooter else card.footer,
//                onCardClick = onClick
//            )
//        }
//    }
}