package com.rarilabs.rarime.api.ext_integrator.ext_int_action_preview

import android.net.Uri
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.rarilabs.rarime.api.ext_integrator.ext_int_action_preview.handlers.ext_int_query_proof_handler.ExtIntQueryProofHandler
import com.rarilabs.rarime.api.ext_integrator.ext_int_action_preview.handlers.light_proof_handler.LightProofHandler
import com.rarilabs.rarime.api.ext_integrator.models.ExtIntegratorActions
import com.rarilabs.rarime.ui.theme.RarimeTheme

@Composable
fun ExtIntActionPreview(
    dataUri: Uri?,
    onCancel: () -> Unit = {},
    onSuccess: (destination: String?) -> Unit = {},
    onError: () -> Unit = {}
) {
    val queryParams = remember {
        dataUri?.queryParameterNames?.associateWith { paramName ->
            dataUri.getQueryParameter(paramName)
        }
    }
    val requestType = remember {
        queryParams?.get("type")
    }

    if (queryParams?.isNotEmpty() == true) {
        when (requestType) {
            ExtIntegratorActions.QueryProofGen.value -> {
                ExtIntQueryProofHandler(
                    queryParams = queryParams,
                    onCancel = onCancel,
                    onSuccess = { destination -> onSuccess(destination) },
                    onFail = { onError() }
                )
            }

            ExtIntegratorActions.LightVerification.value -> {
                LightProofHandler(
                    queryParams = queryParams,
                    onCancel = onCancel,
                    onSuccess = { destination -> onSuccess(destination) },
                    onFail = { onError() }
                )
            }

            else -> {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Not implemented",
                        style = RarimeTheme.typography.subtitle4,
                        color = RarimeTheme.colors.textPrimary
                    )
                }
            }
        }
    }
}
