package com.rarilabs.rarime.modules.manageWidgets

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.rarilabs.rarime.R
import com.rarilabs.rarime.modules.main.ScreenInsets
import com.rarilabs.rarime.ui.base.ButtonSize
import com.rarilabs.rarime.ui.components.PrimaryButton

@Composable
fun ManageWidgetsButton(
    modifier: Modifier = Modifier,
    innerPaddings: Map<ScreenInsets, Number>,
    onClick: () -> Unit
) {
    Box(modifier = modifier.fillMaxSize()) {
        PrimaryButton(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(
                    bottom = (innerPaddings[ScreenInsets.BOTTOM]!!.toInt() + 10).dp,
                    start = 73.dp,
                    end = 73.dp,
                    top = 56.dp
                )
                .fillMaxWidth(),
            onClick = onClick,
            size = ButtonSize.Large,
            text = stringResource(R.string.manage_widgets_btn_label),
            leftIcon = R.drawable.ic_filter_3_line

        )
    }
}