package com.rarilabs.rarime.modules.main

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.absolutePadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun ScreenInsetsContainer(
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit = {}
) {
    val mainViewModel = LocalMainViewModel.current
    val screenInsets by mainViewModel.screenInsets.collectAsState()

    Box(
        modifier = modifier
            .fillMaxSize()
            .absolutePadding(
                top = (screenInsets
                    .get(ScreenInsets.TOP)
                    ?.toFloat() ?: 0f).dp,
                bottom = (screenInsets
                    .get(ScreenInsets.BOTTOM)
                    ?.toFloat() ?: 0f).dp
            )
    ) {
        content()
    }
}
