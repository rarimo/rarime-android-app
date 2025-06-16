package com.rarilabs.rarime.api.ext_integrator.ext_int_action_preview.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.rarilabs.rarime.R
import com.rarilabs.rarime.ui.base.ButtonSize
import com.rarilabs.rarime.ui.components.AppBottomSheet
import com.rarilabs.rarime.ui.components.AppIcon
import com.rarilabs.rarime.ui.components.HorizontalDivider
import com.rarilabs.rarime.ui.components.PrimaryButton
import com.rarilabs.rarime.ui.components.SecondaryButton
import com.rarilabs.rarime.ui.components.rememberAppSheetState
import com.rarilabs.rarime.ui.theme.RarimeTheme
import com.rarilabs.rarime.util.ErrorHandler
import kotlinx.coroutines.launch

data class HandlerPreviewerLayoutTexts(
    val title: String
)

@Composable
fun HandlerPreviewerLayout(
    onAcceptHandler: suspend () -> Unit = {},
    loadPreviewFields: suspend () -> Map<String, String> = { mapOf() },

    texts: HandlerPreviewerLayoutTexts,

    onSuccess: () -> Unit = {},
    onFail: (e: Exception) -> Unit = {},
    onCancel: () -> Unit = {}
) {
    val scope = rememberCoroutineScope()

    var previewFields by remember { mutableStateOf<Map<String, String>>(mapOf()) }

    var isSubmitting by remember { mutableStateOf(false) }
    var isLoaded by remember { mutableStateOf(false) }

    val sheetState = rememberAppSheetState(true)

    LaunchedEffect(Unit) {
        sheetState.show()
    }

    LaunchedEffect(sheetState.showSheet) {
        if (!sheetState.showSheet) {
            sheetState.hide()
            onCancel.invoke()
        }
    }

    fun handleAccept() {
        scope.launch {
            isSubmitting = true

            try {
                onAcceptHandler()
                sheetState.hide()
                onSuccess()

                return@launch
            } catch (e: Exception) {
                ErrorHandler.logError("ExtIntActionPreview", "handleAccept", e)
                sheetState.hide()
                onFail(e)
            }
            isSubmitting = false
        }
    }

    LaunchedEffect(Unit) {
        scope.launch {
            isLoaded = false

            try {
                previewFields = loadPreviewFields()
                previewFields
            } catch (e: Exception) {
                ErrorHandler.logError("ExtIntActionPreview", "loadPreviewFields", e)
                sheetState.hide()
                onFail(e)
            }

            isLoaded = true
        }
    }
    AppBottomSheet(
        state = sheetState,
        isHeaderEnabled = false,
    ) {
        HandlerPreviewerLayoutContent(
            previewFields = previewFields,

            texts = texts,

            isLoaded = isLoaded, isSubmitting = isSubmitting,

            handleAccept = {
                handleAccept()
                sheetState.hide()
            },
            onCancel = {
                onCancel()
                sheetState.hide()
            }
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun HandlerPreviewerLayoutContent(
    previewFields: Map<String, String> = mapOf(),

    texts: HandlerPreviewerLayoutTexts,

    isLoaded: Boolean = false, isSubmitting: Boolean,

    handleAccept: () -> Unit = {}, onCancel: () -> Unit = {}
) {
    Box(
        modifier = Modifier
    )
    {
        Column(
            modifier = Modifier
                .padding(horizontal = 20.dp)
        ) {
            Row() {
                Text(
                    text = "Proof Request",
                    style = RarimeTheme.typography.h3,
                    color = RarimeTheme.colors.textPrimary,
                    modifier = Modifier
                        .padding(top = 30.dp)
                )
                Spacer(modifier = Modifier.weight(1f))
                IconButton(
                    onClick = onCancel,
                    modifier = Modifier
                        .padding(end = 20.dp, top = 24.dp)
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(color = RarimeTheme.colors.componentPrimary)
                ) {
                    AppIcon(
                        id = R.drawable.ic_close_fill,
                        tint = RarimeTheme.colors.textPrimary,
                    )
                }

            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

            Column {
                Text(
                    text = "VERIFICATION CRITERIA",
                    style = RarimeTheme.typography.overline2,
                    color = RarimeTheme.colors.textSecondary,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                previewFields.forEach { it ->
                    Row(modifier = Modifier.padding(4.dp)) {
                        Text(
                            text = it.key,
                            style = RarimeTheme.typography.body4,
                            color = RarimeTheme.colors.textPrimary
                        )
                        Spacer(
                            modifier = Modifier.weight(1f)
                        )
                        Text(
                            text = it.value,
                            style = RarimeTheme.typography.body4,
                            color = RarimeTheme.colors.textPrimary
                        )
                    }
                }


            }
            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
            Column {
                Text(
                    text = "REQUESTOR",
                    style = RarimeTheme.typography.overline2,
                    color = RarimeTheme.colors.textSecondary,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                previewFields.forEach { it -> //TODO change this for requestor information
                    Row(modifier = Modifier.padding(4.dp)) {
                        Text(
                            text = it.key,
                            style = RarimeTheme.typography.body4,
                            color = RarimeTheme.colors.textPrimary
                        )
                        Spacer(
                            modifier = Modifier.weight(1f)
                        )
                        Text(
                            text = it.value,
                            style = RarimeTheme.typography.body4,
                            color = RarimeTheme.colors.textPrimary
                        )
                    }
                }

            }
            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
            Column {
                Text(
                    text = "DATA TO SHARE",
                    style = RarimeTheme.typography.overline2,
                    color = RarimeTheme.colors.textSecondary,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    previewFields.forEach { it -> //TODO change this for data share

                        Box(
                            modifier = Modifier
                                .background(
                                    color = RarimeTheme.colors.componentPrimary,
                                    shape = RoundedCornerShape(12.dp)
                                )
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Text(text = it.key, color = RarimeTheme.colors.textPrimary)
                        }

                    }
                }

            }
            Spacer(modifier = Modifier.height(20.dp))
            Column {
                PrimaryButton(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    text = "Generate Proof",
                    size = ButtonSize.Large,
                    enabled = !isSubmitting,
                    onClick = { handleAccept() }
                )
                SecondaryButton(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    text = "Cancel",
                    size = ButtonSize.Large,
                    onClick = { onCancel() }
                )
            }


        }
    }
}


@Composable
fun HandlerPreviewerLayoutRow(
    modifier: Modifier = Modifier, key: String, value: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .then(modifier),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = key,
            style = RarimeTheme.typography.body3,
            color = RarimeTheme.colors.textPrimary,
            textAlign = TextAlign.End,
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = value,
            style = RarimeTheme.typography.subtitle4,
            color = RarimeTheme.colors.textPrimary,
            textAlign = TextAlign.End
        )
    }
}

@Preview(showBackground = true)
@Composable
fun HandlerPreviewerLayoutContentPreview() {
    HandlerPreviewerLayoutContent(
        previewFields = mapOf(
            "Key 1" to "Value 1", "Key 2" to "Value 2", "Key 3" to "Value 3"
        ),

        texts = HandlerPreviewerLayoutTexts(
            title = "Title"
        ),

        isSubmitting = false,

        handleAccept = { }, onCancel = { })
}