package com.rarilabs.rarime.api.ext_integrator.ext_int_action_preview.handlers.ext_int_query_proof_handler

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.SnackbarDuration
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.rarilabs.rarime.R
import com.rarilabs.rarime.api.ext_integrator.models.NoActiveIdentity
import com.rarilabs.rarime.api.ext_integrator.models.NoPassport
import com.rarilabs.rarime.api.ext_integrator.models.YourAgeDoesNotMeetTheRequirements
import com.rarilabs.rarime.api.ext_integrator.models.YourCitizenshipDoesNotMeetTheRequirements
import com.rarilabs.rarime.modules.main.LocalMainViewModel
import com.rarilabs.rarime.modules.main.ScreenInsets
import com.rarilabs.rarime.ui.base.ButtonSize
import com.rarilabs.rarime.ui.components.AppBottomSheet
import com.rarilabs.rarime.ui.components.AppIcon
import com.rarilabs.rarime.ui.components.AppSheetState
import com.rarilabs.rarime.ui.components.AppSkeleton
import com.rarilabs.rarime.ui.components.PrimaryButton
import com.rarilabs.rarime.ui.components.SnackbarSeverity
import com.rarilabs.rarime.ui.components.getSnackbarDefaultShowOptions
import com.rarilabs.rarime.ui.components.rememberAppSheetState
import com.rarilabs.rarime.ui.theme.RarimeTheme
import com.rarilabs.rarime.util.ErrorHandler
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch

@Composable
fun ExtIntQueryProofHandler(
    viewModel: ExtIntQueryProofHandlerViewModel = hiltViewModel(),
    queryParams: Map<String, String?>?,
    onSuccess: (destination: String?) -> Unit = {},
    onFail: () -> Unit = {},
    onCancel: () -> Unit = {}
) {
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val mainViewModel = LocalMainViewModel.current

    val previewFields by viewModel.fieldsParams.collectAsState()

    var isSubmitting by remember { mutableStateOf(false) }
    var isLoaded by remember { mutableStateOf(false) }
    val innerPaddings by LocalMainViewModel.current.screenInsets.collectAsState()
    val sheetState = rememberAppSheetState(false)

    LaunchedEffect(isLoaded) {
        sheetState.show()
    }

    fun onSuccessHandler() {
        val redirectUrl = queryParams?.get("redirect_uri")

        scope.launch {
            val snackBar = async {
                mainViewModel.showSnackbar(
                    options = getSnackbarDefaultShowOptions(
                        severity = SnackbarSeverity.Success,
                        duration = SnackbarDuration.Long,
                        title = context.getString(R.string.light_verification_success_title),
                        message = context.getString(R.string.light_verification_success_subtitle),
                    )
                )
            }
            val redirect = async { onSuccess.invoke(redirectUrl) }

            awaitAll(snackBar, redirect)
        }
    }

    fun onFailHandler(e: Exception) {
        scope.launch {
            val message = when (e) {
                is YourAgeDoesNotMeetTheRequirements -> context.getString(R.string.light_verification_error_age)
                is YourCitizenshipDoesNotMeetTheRequirements -> context.getString(R.string.light_verification_error_citizenship)
                is NoPassport -> context.getString(R.string.no_passport_error)
                is NoActiveIdentity -> context.getString(R.string.no_active_identity)
                else -> context.getString(R.string.light_verification_error_subtitle)
            }

            mainViewModel.showSnackbar(
                options = getSnackbarDefaultShowOptions(
                    severity = SnackbarSeverity.Error,
                    duration = SnackbarDuration.Long,
                    title = context.getString(R.string.light_verification_error_title),
                    message = message,
                )
            )
        }
        onFail.invoke()
    }

    fun handleAccept() {
        scope.launch {
            isSubmitting = true

            try {
                viewModel.generateQueryProof(context)
                onSuccessHandler()
            } catch (e: Exception) {
                ErrorHandler.logError("ExtIntActionPreview", "handleAccept", e)
                onFailHandler(e)
            }

            sheetState.hide()
            isSubmitting = false
        }
    }

    LaunchedEffect(Unit) {
        scope.launch {
            isLoaded = false

            try {
                val proofParamsUrl = queryParams?.get("proof_params_url")
                    ?: throw Exception("Missing required parameters")
                val redirectUrl = queryParams["redirect_uri"]

                viewModel.loadDetails(proofParamsUrl, redirectUrl)
            } catch (e: Exception) {
                onFailHandler(e)
            }

            isLoaded = true
        }
    }

    ExtIntQueryProofHandlerContent(
        previewFields = previewFields,
        isSubmitting = isSubmitting,
        sheetState = sheetState,
        handleAccept = { handleAccept() },
        onCancel = { onCancel.invoke() },
        innerPaddings = innerPaddings
    )
}

@Composable
private fun ExtIntQueryProofHandlerContent(
    previewFields: Map<String, String> = mapOf(),
    isSubmitting: Boolean,
    sheetState: AppSheetState,
    handleAccept: () -> Unit = {},
    onCancel: () -> Unit = {},
    innerPaddings: Map<ScreenInsets, Number>,
    ) {
    AppBottomSheet(
        state = sheetState,
        isHeaderEnabled = false,
    ) { hide ->
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    top = 24.dp,
                    start = 24.dp,
                    end = 24.dp,
                )
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = stringResource(id = R.string.query_proof_sheet_title),
                    style = RarimeTheme.typography.h4,
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

            Spacer(modifier = Modifier
                .weight(1f)
                .height(50.dp))

            PrimaryButton(
                modifier = Modifier.fillMaxWidth(),
                text = "Accept",
                size = ButtonSize.Large,
                enabled = !isSubmitting,
                onClick = { handleAccept() }
            )
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
            style = RarimeTheme.typography.body4,
            color = RarimeTheme.colors.textSecondary,
            textAlign = TextAlign.End,
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = value,
            style = RarimeTheme.typography.subtitle5,
            color = RarimeTheme.colors.textPrimary,
            textAlign = TextAlign.End
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun ExtIntQueryProofHandlerContentPreview() {
    ExtIntQueryProofHandlerContent(
        previewFields = mapOf(
            "Key 1" to "Value 1",
            "Key 2" to "Value 2",
            "Key 3" to "Value 3"
        ),
        isSubmitting = false,
        sheetState = rememberAppSheetState(true),
        handleAccept = { },
        onCancel = { },
        innerPaddings = mapOf(ScreenInsets.TOP to 0, ScreenInsets.BOTTOM to 0),
    )
}
