package com.rarilabs.rarime.modules.home.v3.model

import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope

@OptIn(ExperimentalSharedTransitionApi::class)
sealed class BaseWidgetProps(
    open val layoutId: Int,
    open val animatedVisibilityScope: AnimatedContentScope,
    open val sharedTransitionScope: SharedTransitionScope
) {
    data class Collapsed(
        val onExpand: () -> Unit,
        override val layoutId: Int,
        override val animatedVisibilityScope: AnimatedContentScope,
        override val sharedTransitionScope: SharedTransitionScope
    ) : BaseWidgetProps(layoutId, animatedVisibilityScope, sharedTransitionScope)

    data class Expanded(
        val onCollapse: () -> Unit,
        override val layoutId: Int,
        override val animatedVisibilityScope: AnimatedContentScope,
        override val sharedTransitionScope: SharedTransitionScope
    ) : BaseWidgetProps(layoutId, animatedVisibilityScope, sharedTransitionScope)
}

