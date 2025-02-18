package com.rarilabs.rarime.util

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.runtime.Composable

@Composable
@OptIn(ExperimentalSharedTransitionApi::class)
fun PrevireSharedAnimationProvider(content: @Composable (sharedTransitionLayout: SharedTransitionScope, animatedContentScope: AnimatedContentScope) -> Unit) {
    return SharedTransitionLayout {
        AnimatedContent(
            true,
            label = "basic_transition"
        ) { it ->
            if (it) {
                content(this@SharedTransitionLayout, this@AnimatedContent)
            }
        }
    }
}