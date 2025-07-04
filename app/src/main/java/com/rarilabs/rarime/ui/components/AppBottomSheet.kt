package com.rarilabs.rarime.ui.components

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.rarilabs.rarime.ui.theme.RarimeTheme
import kotlinx.coroutines.launch

class AppSheetState(initialShowSheet: Boolean = false) {
    var showSheet by mutableStateOf(initialShowSheet)
        private set

    fun show() {
        showSheet = true
    }

    fun hide() {
        showSheet = false
    }

    companion object {
        val Saver: Saver<AppSheetState, *> =
            listSaver(save = { listOf(it.showSheet) }, restore = { list ->
                AppSheetState(initialShowSheet = list[0])
            })
    }
}

@Composable
fun rememberAppSheetState(showSheet: Boolean = false) =
    rememberSaveable(showSheet, saver = AppSheetState.Saver) {
        AppSheetState(showSheet)
    }

typealias HideSheetFn = (cb: () -> Unit) -> Unit

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppBottomSheet(
    modifier: Modifier = Modifier,
    state: AppSheetState = rememberAppSheetState(false),
    fullScreen: Boolean = false,
    shape: Shape = BottomSheetDefaults.ExpandedShape,
    isHeaderEnabled: Boolean = true,
    scrimColor: Color = Color.Black.copy(alpha = 0.5f),
    backgroundColor: Color = RarimeTheme.colors.backgroundPure,
    disablePullClose: Boolean = false,
    onClose: () -> Unit = {},
    content: @Composable (HideSheetFn) -> Unit,
) {
    val modalState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true,
        confirmValueChange = { newValue ->
            !(disablePullClose && newValue == SheetValue.Hidden)
        }
    )
    val coroutineScope = rememberCoroutineScope()

    fun hide(callback: () -> Unit = {}) {
        coroutineScope.launch {
            onClose()
            modalState.hide()
            state.hide()
            callback()
        }
    }

    BackHandler(enabled = state.showSheet) {
        if (!disablePullClose) {
            hide()
        }
    }

    if (state.showSheet) {
        ModalBottomSheet(
            modifier = modifier,
            sheetState = modalState,
            shape = shape,
            dragHandle = null,
            containerColor = backgroundColor,
            scrimColor = scrimColor,
            onDismissRequest = { hide() },
            windowInsets = WindowInsets.systemBars,
        ) {
            Column(
                modifier = if (fullScreen) Modifier.fillMaxSize() else Modifier.fillMaxWidth()
            ) {
                if (isHeaderEnabled) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp, vertical = 10.dp),
                        horizontalArrangement = Arrangement.End
                    ) {

                    }
                }
                content { afterHide ->
                    hide(afterHide)
                }
            }
        }
    }
}


@Preview(showSystemUi = true)
@Composable
private fun AppBottomSheetPreview() {
    val sheetState = rememberAppSheetState(true) // Open by default for preview

    Box {
        PrimaryButton(
            modifier = Modifier.padding(top = 48.dp),
            text = "Show bottom sheet",
            onClick = { sheetState.show() })
    }
    AppBottomSheet(
        state = sheetState,
        fullScreen = false,
    ) {
        Box(
            modifier = Modifier.background(Color.Red)
        ) {
            Text("Bottom sheet content")
        }
    }

}
