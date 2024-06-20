package com.rarilabs.rarime.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
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
import androidx.compose.ui.platform.LocalConfiguration
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
        val Saver: Saver<AppSheetState, *> = listSaver(
            save = { listOf(it.showSheet) },
            restore = {
                AppSheetState(initialShowSheet = it[0])
            }
        )
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
    isHeaderEnabled: Boolean = true,
    content: @Composable (HideSheetFn) -> Unit
) {
    val modalState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val coroutineScope = rememberCoroutineScope()
    val configuration = LocalConfiguration.current

    fun hide(cb: () -> Unit) {
        coroutineScope.launch { modalState.hide() }.invokeOnCompletion {
            if (!modalState.isVisible) {
                state.hide()
                cb()
            }
        }
    }

    if (state.showSheet) {
        ModalBottomSheet(
            modifier = modifier,
            sheetState = modalState,
            dragHandle = null,
            containerColor = RarimeTheme.colors.backgroundPure,
            onDismissRequest = { state.hide() }
        ) {
            Box(modifier = Modifier.fillMaxWidth()) {
                if (isHeaderEnabled) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        horizontalArrangement = Arrangement.End
                    ) {
                        PrimaryTextButton(
                            leftIcon = R.drawable.ic_close,
                            onClick = { hide {} }
                        )
                    }
                }
                Box(
                    modifier = (if (fullScreen) Modifier.height(configuration.screenHeightDp.dp) else Modifier)
                        .fillMaxWidth()
                        .padding(bottom = 32.dp)
                ) {
                    content { cb -> hide(cb) }
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
                text = "Show bottom sheet",
                onClick = { sheetState.show() }
            )
        }
        AppBottomSheet(
            state = sheetState,
            fullScreen = true,
        ) {
            Box(modifier = Modifier.height(200.dp)) {
                Text("Bottom sheet content")
            }
        }
    }
}