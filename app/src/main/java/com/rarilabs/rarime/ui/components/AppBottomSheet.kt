package com.rarilabs.rarime.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Surface
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
import com.rarilabs.rarime.R
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
    scrimColor: Color = Color.Black.copy(alpha = 0.5f), // Dims the background
    isWindowInsetsEnabled: Boolean = true,
    // When `disableScrollClose` true, prevents sheet from closing on swipe/scroll
    // without pulling in experimental APIs
    disableScrollClose: Boolean = false,
    content: @Composable (HideSheetFn) -> Unit,
) {
    val modalState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true,
        confirmValueChange = { newValue ->
            if (disableScrollClose) newValue == SheetValue.Hidden else true
        })
    val coroutineScope = rememberCoroutineScope()

    // Remove the windowInsetsPadding from the sheet container so the scrim covers the full screen.
    // Instead, we'll apply insets on the inner content.
    fun hide(callback: () -> Unit = {}) {
        coroutineScope.launch { modalState.hide() }.invokeOnCompletion {
            if (!modalState.isVisible) {
                state.hide()
                callback()
            }
        }
    }

    if (state.showSheet) {
        ModalBottomSheet(
            modifier = modifier.fillMaxWidth(), // Let the sheet fill the width of the screen.
            sheetState = modalState,
            shape = shape,
            dragHandle = null,
            containerColor = RarimeTheme.colors.backgroundPure,
            onDismissRequest = { hide() },
            scrimColor = scrimColor,
            windowInsets = if (isWindowInsetsEnabled) {
                BottomSheetDefaults.windowInsets
            } else {
                val topPaddingDp =
                    BottomSheetDefaults.windowInsets.asPaddingValues().calculateTopPadding()
                WindowInsets(0.dp, topPaddingDp, 0.dp, 0.dp)

            }
        ) {
            // Wrap the sheet content with a container that applies window insets (for content padding),
            // while the ModalBottomSheet itself still occupies the full screen so the scrim covers all edges.
            Box(modifier = if (fullScreen) Modifier.fillMaxSize() else Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .windowInsetsPadding(WindowInsets.systemBars) // Apply system insets only to inner content
                        .padding(bottom = 24.dp)
                ) {
                    if (isHeaderEnabled) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 20.dp, vertical = 10.dp),
                            horizontalArrangement = Arrangement.End
                        ) {
                            PrimaryTextButton(
                                leftIcon = R.drawable.ic_close, onClick = { hide() })
                        }
                    }
                    content { callback -> hide(callback) }
                }
            }
        }
    }
}

@Preview
@Composable
private fun AppBottomSheetPreview() {
    val sheetState = rememberAppSheetState(false)

    Surface(modifier = Modifier.fillMaxSize()) {
        Box {
            PrimaryButton(
                text = "Show bottom sheet", onClick = { sheetState.show() })
        }
        AppBottomSheet(
            state = sheetState,
            fullScreen = true,
            isWindowInsetsEnabled = false,

            ) {
            Box(modifier = Modifier.height(200.dp)) {
                Text("Bottom sheet content")
            }
        }
    }
}