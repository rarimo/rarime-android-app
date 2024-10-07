package com.rarilabs.rarime.api.ext_integrator.ext_int_action_preview.handlers.light_proof_handler

import androidx.compose.material3.SnackbarDuration
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import com.rarilabs.rarime.R
import com.rarilabs.rarime.api.ext_integrator.ext_int_action_preview.components.HandlerPreviewerLayout
import com.rarilabs.rarime.api.ext_integrator.ext_int_action_preview.components.HandlerPreviewerLayoutTexts
import com.rarilabs.rarime.modules.main.LocalMainViewModel
import com.rarilabs.rarime.ui.components.SnackbarSeverity
import com.rarilabs.rarime.ui.components.getSnackbarDefaultShowOptions
import kotlinx.coroutines.launch

@Composable
fun LightProofHandler(
    queryParams: Map<String, String?>?,
    onCancel: () -> Unit = {},
    onSuccess: () -> Unit = {},
    onFail: () -> Unit = {},

    viewModel: LightProofHandlerViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val mainViewModel = LocalMainViewModel.current

    fun onSuccessHandler() {
        scope.launch {
            mainViewModel.showSnackbar(
                options = getSnackbarDefaultShowOptions(
                    severity = SnackbarSeverity.Success,
                    duration = SnackbarDuration.Long,
                    title = context.getString(R.string.light_verification_success_title),
                    message = context.getString(R.string.light_verification_success_subtitle),
                )
            )
        }
        onSuccess.invoke()
    }

    fun onFailHandler(e: Exception) {
        scope.launch {
            val message = when(e) {
                is YourAgeDoesNotMeetTheRequirements -> context.getString(R.string.light_verification_error_age)
                is YourCitizenshipDoesNotMeetTheRequirements -> context.getString(R.string.light_verification_error_citizenship)
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

    HandlerPreviewerLayout(
        onAcceptHandler = {
            viewModel.signHashedEventId()
        },
        loadPreviewFields = {
            val proofParamsUrl = queryParams?.get("proof_params_url")
                ?: throw Exception("Missing required parameters")

            viewModel.loadDetails(proofParamsUrl)
        },

        texts = HandlerPreviewerLayoutTexts(
            title = stringResource(R.string.light_verification_sheet_title)
        ),

        onSuccess = { onSuccessHandler() },
        onFail = { onFailHandler(it) },
        onCancel = onCancel,
    )
}
