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
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.rarilabs.rarime.R
import com.rarilabs.rarime.ui.base.ButtonSize
import com.rarilabs.rarime.ui.components.AppBottomSheet
import com.rarilabs.rarime.ui.components.AppIcon
import com.rarilabs.rarime.ui.components.HorizontalDivider
import com.rarilabs.rarime.ui.components.PrimaryButton
import com.rarilabs.rarime.ui.components.TransparentButton
import com.rarilabs.rarime.ui.components.rememberAppSheetState
import com.rarilabs.rarime.ui.theme.RarimeTheme
import com.rarilabs.rarime.util.ErrorHandler
import com.rarilabs.rarime.util.QueryProofField
import com.rarilabs.rarime.util.WalletUtil.formatAddress
import kotlinx.coroutines.launch

data class HandlerPreviewerLayoutTitle(
    val title: String
)

@Composable
fun HandlerPreviewerLayout(
    onAcceptHandler: suspend () -> Unit = {},
    loadPreviewFields: suspend () -> Map<String, String> = { mapOf() },


    onSuccess: () -> Unit = {},
    onFail: (e: Exception) -> Unit = {},
    onCancel: () -> Unit = {},
    selector: String = "0",
    requestorId: String,
    requestorHost: String
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


            isLoaded = isLoaded, isSubmitting = isSubmitting,

            handleAccept = {
                handleAccept()
                sheetState.hide()
            },
            onCancel = {
                sheetState.hide()
                onCancel()
            },
            selector = selector,
            requestorId = requestorId,
            requestorHost = requestorHost
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun HandlerPreviewerLayoutContent(
    previewFields: Map<String, String> = mapOf(),


    isLoaded: Boolean = false, isSubmitting: Boolean,

    handleAccept: () -> Unit = {}, onCancel: () -> Unit = {},
    selector: String = "0",
    requestorId: String,
    requestorHost: String
) {
    val dataToShare = QueryProofField.fromSelector(selector)
    Box(
        modifier = Modifier
    ) {
        Column(
            modifier = Modifier
        ) {
            Row(modifier = Modifier.padding(horizontal = 20.dp)) {
                Text(
                    text = stringResource(R.string.light_proof_header),
                    style = RarimeTheme.typography.h3,
                    color = RarimeTheme.colors.textPrimary,
                    modifier = Modifier
                        .padding(top = 30.dp)
                )
                Spacer(modifier = Modifier.weight(1f))
                IconButton(
                    onClick = onCancel,
                    modifier = Modifier
                        .padding(top = 24.dp)
                        .size(40.dp)
                        .clip(CircleShape)
                ) {
                    AppIcon(
                        id = R.drawable.ic_close_fill,
                        tint = RarimeTheme.colors.textPrimary,
                        size = 30.dp
                    )
                }

            }

            HorizontalDivider(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 20.dp)
            )

            Column(modifier = Modifier.padding(horizontal = 20.dp)) {
                Text(
                    text = stringResource(R.string.light_proof_verification_criteria_section_title),
                    style = RarimeTheme.typography.overline2,
                    color = RarimeTheme.colors.textSecondary,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                previewFields.forEach { it ->
                    Row(modifier = Modifier.padding(8.dp)) {
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
            HorizontalDivider(modifier = Modifier.padding(vertical = 20.dp, horizontal = 20.dp))
            Column(modifier = Modifier.padding(horizontal = 20.dp)) {
                Text(
                    text = stringResource(R.string.light_proof_requestor_section_title),
                    style = RarimeTheme.typography.overline2,
                    color = RarimeTheme.colors.textSecondary,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Row(modifier = Modifier.padding(8.dp)) {
                    Text(
                        text = stringResource(R.string.light_proof_id),
                        style = RarimeTheme.typography.body4,
                        color = RarimeTheme.colors.textPrimary
                    )
                    Spacer(
                        modifier = Modifier.weight(1f)
                    )
                    Text(
                        text = formatAddress(
                            address = requestorId,
                            charsStartAmount = 8,
                            charsEndAmount = 8
                        ),
                        style = RarimeTheme.typography.body4,
                        color = RarimeTheme.colors.textPrimary
                    )
                }
                Row(modifier = Modifier.padding(8.dp)) {
                    Text(
                        text = stringResource(R.string.light_proof_host),
                        style = RarimeTheme.typography.body4,
                        color = RarimeTheme.colors.textPrimary
                    )
                    Spacer(
                        modifier = Modifier.weight(1f)
                    )
                    Text(
                        text = requestorHost,
                        style = RarimeTheme.typography.body4,
                        color = RarimeTheme.colors.textPrimary
                    )
                }
            }


            HorizontalDivider(modifier = Modifier.padding(vertical = 20.dp, horizontal = 20.dp))
            Column(modifier = Modifier.padding(horizontal = 20.dp)) {
                Text(
                    text = stringResource(R.string.ligth_profe_revealed_data_section_title),
                    style = RarimeTheme.typography.overline2,
                    color = RarimeTheme.colors.textSecondary,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    dataToShare.forEach { it ->

                        Box(
                            modifier = Modifier
                                .background(
                                    color = RarimeTheme.colors.componentPrimary,
                                    shape = RoundedCornerShape(12.dp)
                                )
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = it.displayName,
                                style = RarimeTheme.typography.subtitle6,
                                color = RarimeTheme.colors.textPrimary
                            )
                        }

                    }
                }

            }

            Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 20.dp)) {
                PrimaryButton(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    text = stringResource(R.string.light_proof_generate_proof_btn_label),
                    size = ButtonSize.Large,
                    enabled = !isSubmitting,
                    onClick = { handleAccept() }
                )
                Spacer(modifier = Modifier.height(8.dp))
                TransparentButton(
                    modifier = Modifier
                        .fillMaxWidth(),
                    text = stringResource(R.string.light_proof_cancel_btn_label),
                    size = ButtonSize.Large,
                    onClick = { onCancel() },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Transparent,
                        contentColor = RarimeTheme.colors.textPrimary,
                        disabledContainerColor = Color.Transparent,
                        disabledContentColor = RarimeTheme.colors.textDisabled
                    )
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


        isSubmitting = false,

        handleAccept = { }, onCancel = { },
        selector = "1098",
        requestorId = "24",
        requestorHost = "Rarime",
    )
}