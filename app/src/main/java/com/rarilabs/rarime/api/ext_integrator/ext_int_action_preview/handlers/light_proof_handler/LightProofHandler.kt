package com.rarilabs.rarime.api.ext_integrator.ext_int_action_preview.handlers.light_proof_handler

import androidx.compose.material3.SnackbarDuration
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import com.rarilabs.rarime.R
import com.rarilabs.rarime.api.ext_integrator.ext_int_action_preview.components.HandlerPreviewerLayout
import com.rarilabs.rarime.api.ext_integrator.ext_int_action_preview.components.HandlerPreviewerLayoutTitle
import com.rarilabs.rarime.api.ext_integrator.models.NoPassport
import com.rarilabs.rarime.api.ext_integrator.models.YourAgeDoesNotMeetTheRequirements
import com.rarilabs.rarime.api.ext_integrator.models.YourCitizenshipDoesNotMeetTheRequirements
import com.rarilabs.rarime.modules.main.LocalMainViewModel
import com.rarilabs.rarime.ui.components.SnackbarSeverity
import com.rarilabs.rarime.ui.components.getSnackbarDefaultShowOptions
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch
import java.net.URL

@Composable
fun LightProofHandler(
    queryParams: Map<String, String?>?,
    onCancel: () -> Unit = {},
    onSuccess: (destination: String?) -> Unit = {},
    onFail: () -> Unit = {},
    viewModel: LightProofHandlerViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val mainViewModel = LocalMainViewModel.current
    val queryProofParametersRequest by viewModel.queryProofParametersRequest.collectAsState()
    val selector by remember {
        derivedStateOf {
            queryProofParametersRequest!!.data.attributes.selector
        }
    }
    val requestorId by remember {
        derivedStateOf {
            queryProofParametersRequest!!.data.id
        }
    }
    val requestorHost by remember {
        derivedStateOf {
            URL(queryProofParametersRequest!!.data.attributes.callback_url).host
        }
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
            val redirectUrl = queryParams["redirect_uri"]

            viewModel.loadDetails(proofParamsUrl, redirectUrl)
        },



        onSuccess = { onSuccessHandler() },
        onFail = { onFailHandler(it) },
        onCancel = onCancel,
        selector = selector,
        requestorId = requestorId,
        requestorHost = requestorHost,
    )
}
