package com.rarilabs.rarime.api.ext_integrator.ext_int_action_preview.handlers.ext_int_query_proof_handler

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.rarilabs.rarime.R
import com.rarilabs.rarime.api.ext_integrator.models.ExtIntegratorActions
import com.rarilabs.rarime.api.ext_integrator.models.QrAction
import com.rarilabs.rarime.ui.base.ButtonSize
import com.rarilabs.rarime.ui.components.AppBottomSheet
import com.rarilabs.rarime.ui.components.AppIcon
import com.rarilabs.rarime.ui.components.AppSheetState
import com.rarilabs.rarime.ui.components.AppSkeleton
import com.rarilabs.rarime.ui.components.PrimaryButton
import com.rarilabs.rarime.ui.components.TertiaryButton
import com.rarilabs.rarime.ui.components.rememberAppSheetState
import com.rarilabs.rarime.ui.theme.RarimeTheme
import com.rarilabs.rarime.util.ErrorHandler
import kotlinx.coroutines.launch

@Composable
fun ExtIntQueryProofHandler(
    viewModel: ExtIntQueryProofHandlerViewModel = hiltViewModel(),

    qrAction: QrAction,
    onSuccess: () -> Unit = {},
    onFail: () -> Unit = {},
    onCancel: () -> Unit = {}
) {
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    val previewFields by viewModel.fieldsParams.collectAsState()

    var isSubmitting by remember { mutableStateOf(false) }
    var isLoaded by remember { mutableStateOf(false) }

    val sheetState = rememberAppSheetState(true)

    fun handleAccept() {
        scope.launch {
            isSubmitting = true

            try {
                viewModel.generateQueryProof(context)
                onSuccess.invoke()
            } catch (e: Exception) {
                ErrorHandler.logError("ExtIntActionPreview", "handleAccept", e)
                onFail.invoke()
            }

            sheetState.hide()
            isSubmitting = false
        }
    }

    LaunchedEffect(Unit) {
        scope.launch {
            isLoaded = false

            try {
                viewModel.loadDetails(qrAction, context)
            } catch (e: Exception) {
                onFail.invoke()
            }

            isLoaded = true
        }
    }

    ExtIntQueryProofHandlerContent(
        qrAction = qrAction,
        previewFields = previewFields,
        isSubmitting = isSubmitting,
        sheetState = sheetState,
        handleAccept = { handleAccept() },
        onCancel = { onCancel.invoke() }
    )
}

@Composable
private fun ExtIntQueryProofHandlerContent(
    qrAction: QrAction,
    previewFields: Map<String, String> = mapOf(),
    isSubmitting: Boolean,
    sheetState: AppSheetState,
    handleAccept: () -> Unit = {},
    onCancel: () -> Unit = {}
) {
    val context = LocalContext.current

    AppBottomSheet(
        state = sheetState,
        isHeaderEnabled = false,
    ) { hide ->
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.75f)
                .padding(
                    top = 24.dp,
                    start = 24.dp,
                    end = 24.dp,
                    bottom = 16.dp
                )
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = qrAction.getTitle(context),
                    style = RarimeTheme.typography.h6,
                    color = RarimeTheme.colors.textPrimary,
                    modifier = Modifier.align(Alignment.CenterVertically)
                )
                AppIcon(
                    modifier = Modifier.clickable { hide.invoke({}) },
                    id = R.drawable.ic_close,
                    tint = RarimeTheme.colors.textPrimary,
                    size = 22.dp
                )
            }

            if (previewFields.isNotEmpty()) {
                previewFields.forEach { (key, value) ->
                    ExtIntActionPreviewRow(
                        key = key,
                        value = value
                    )
                }
            } else {
                repeat(3) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        AppSkeleton(
                            modifier = Modifier
                                .weight(1f)
                                .height(32.dp),
                        )
                        AppSkeleton(
                            modifier = Modifier
                                .weight(1f)
                                .height(32.dp),
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                PrimaryButton(
                    modifier = Modifier.fillMaxWidth(),
                    text = "Accept",
                    size = ButtonSize.Large,
                    enabled = !isSubmitting,
                    onClick = { handleAccept() }
                )

                TertiaryButton(
                    modifier = Modifier.fillMaxWidth(),
                    text = "Cancel",
                    size = ButtonSize.Large,
                    enabled = !isSubmitting,
                    onClick = { onCancel.invoke() }
                )
            }
        }
    }
}

@Composable
fun ExtIntActionPreviewRow(
    modifier: Modifier = Modifier,
    key: String,
    value: String
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
private fun ExtIntQueryProofHandlerContentPreview() {
    ExtIntQueryProofHandlerContent(
        qrAction = QrAction(
            id = "1",
            type = ExtIntegratorActions.QueryProofGen.value,
        ),
        previewFields = mapOf(
            "Key 1" to "Value 1",
            "Key 2" to "Value 2",
            "Key 3" to "Value 3"
        ),
        isSubmitting = false,
        sheetState = rememberAppSheetState(true),
        handleAccept = {  },
        onCancel = {  }
    )
}
