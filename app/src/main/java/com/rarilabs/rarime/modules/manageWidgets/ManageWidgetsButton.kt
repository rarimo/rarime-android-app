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
import com.rarilabs.rarime.ui.components.AppBottomSheet
import com.rarilabs.rarime.ui.components.PrimaryButton
import com.rarilabs.rarime.ui.components.rememberAppSheetState
import com.rarilabs.rarime.ui.theme.RarimeTheme

@Composable
fun ManageWidgetsButton(
    modifier: Modifier = Modifier,
    innerPaddings: Map<ScreenInsets, Number>,
) {
    val sheetManageWidgets = rememberAppSheetState()
    AppBottomSheet(
        state = sheetManageWidgets,
        backgroundColor = RarimeTheme.colors.backgroundPrimary,
        isHeaderEnabled = false,

        ) {
        ManageWidgetsBottomSheet(onClose = { sheetManageWidgets.hide() }, onRemove = {}, onAdd = {})//Todo implement logic
    }
    Box(modifier = Modifier.fillMaxSize()) {
        PrimaryButton(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(
                    bottom = (innerPaddings[ScreenInsets.BOTTOM]!!.toInt() + 48).dp,
                    start = 73.dp,
                    end = 73.dp,
                    top = 48.dp
                )
                .fillMaxWidth(),
            onClick = { sheetManageWidgets.show() },
            text = stringResource(R.string.manage_widgets_btn_label)
        )
    }
}