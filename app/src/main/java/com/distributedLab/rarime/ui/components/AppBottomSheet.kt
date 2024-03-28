package com.distributedLab.rarime.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.distributedLab.rarime.R
import com.distributedLab.rarime.ui.theme.RarimeTheme
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppBottomSheet(
    modifier: Modifier = Modifier,
    state: AppSheetState = rememberAppSheetState(false),
    bottomBar: @Composable ((hide: () -> Unit) -> Unit)? = null,
    content: @Composable () -> Unit
) {
    val sheetState = rememberModalBottomSheetState()
    val coroutineScope = rememberCoroutineScope()

    val hide = {
        coroutineScope.launch { sheetState.hide() }.invokeOnCompletion {
            if (!sheetState.isVisible) {
                state.hide()
            }
        }
    }

    if (state.showSheet) {
        ModalBottomSheet(
            modifier = modifier,
            sheetState = sheetState,
            dragHandle = null,
            containerColor = RarimeTheme.colors.backgroundPure,
            onDismissRequest = { state.hide() }
        ) {
            Box(
                modifier = Modifier
                    .padding(bottom = 48.dp)
                    .fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalArrangement = Arrangement.End
                ) {
                    PrimaryTextButton(
                        leftIcon = R.drawable.ic_close,
                        onClick = { hide() }
                    )
                }
                Column {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp)
                            .padding(top = 32.dp)
                    ) {
                        content()
                    }
                    bottomBar?.let {
                        Box(modifier = Modifier.padding(top = 32.dp)) {
                            HorizontalDivider()
                            Box(
                                modifier = Modifier
                                    .padding(top = 16.dp)
                                    .padding(horizontal = 20.dp)
                            ) {
                                it.invoke { hide() }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Preview
@Composable
private fun AppBottomSheetPreview() {
    val sheetState = rememberAppSheetState(true)

    Surface(modifier = Modifier.fillMaxSize()) {
        Box {
            PrimaryButton(
                text = "Show bottom sheet",
                onClick = { sheetState.show() }
            )
        }
        AppBottomSheet(state = sheetState) {
            Box(modifier = Modifier.height(200.dp)) {
                Text("Bottom sheet content")
            }
        }
    }
}