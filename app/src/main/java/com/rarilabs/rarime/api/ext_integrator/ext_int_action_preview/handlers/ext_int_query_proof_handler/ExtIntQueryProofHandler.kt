package com.rarilabs.rarime.api.ext_integrator.ext_int_action_preview.handlers.ext_int_query_proof_handler

import android.annotation.SuppressLint
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
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
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
import com.rarilabs.rarime.ui.base.ButtonSize
import com.rarilabs.rarime.ui.components.AppBottomSheet
import com.rarilabs.rarime.ui.components.AppIcon
import com.rarilabs.rarime.ui.components.HorizontalDivider
import com.rarilabs.rarime.ui.components.PrimaryButton
import com.rarilabs.rarime.ui.components.SnackbarSeverity
import com.rarilabs.rarime.ui.components.TransparentButton
import com.rarilabs.rarime.ui.components.getSnackbarDefaultShowOptions
import com.rarilabs.rarime.ui.components.rememberAppSheetState
import com.rarilabs.rarime.ui.theme.RarimeTheme
import com.rarilabs.rarime.util.ErrorHandler
import com.rarilabs.rarime.util.QueryProofField
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch
import java.net.URL


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

    val previewFields by viewModel.fieldsParams.collectAsState()

    var isSubmitting by remember { mutableStateOf(false) }
    var isLoaded by remember { mutableStateOf(false) }
    val innerPaddings by LocalMainViewModel.current.screenInsets.collectAsState()
    val sheetState = rememberAppSheetState(false)

    if (isLoaded) {
        LaunchedEffect(Unit) {
            sheetState.show()
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
    AppBottomSheet(
        state = sheetState,
        isHeaderEnabled = false,
        backgroundColor = RarimeTheme.colors.backgroundSurface1
    ) {
        ExtIntQueryProofHandlerContent(
            previewFields = previewFields,
            isSubmitting = isSubmitting,
            handleAccept = { handleAccept() },
            onCancel = { onCancel.invoke() },
            selector = selector,
            requestorId = requestorId,
            requestorHost = requestorHost,
        )
    }

}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun ExtIntQueryProofHandlerContent(
    previewFields: Map<String, String> = mapOf(),
    isSubmitting: Boolean,
    handleAccept: () -> Unit = {},
    onCancel: () -> Unit = {},
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
                    text = "VERIFICATION CRITERIA",
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
                    text = "REQUESTOR",
                    style = RarimeTheme.typography.overline2,
                    color = RarimeTheme.colors.textSecondary,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Row(modifier = Modifier.padding(8.dp)) {
                    Text(
                        text = "ID",
                        style = RarimeTheme.typography.body4,
                        color = RarimeTheme.colors.textPrimary
                    )
                    Spacer(
                        modifier = Modifier.weight(1f)
                    )
                    Text(
                        text = requestorId,
                        style = RarimeTheme.typography.body4,
                        color = RarimeTheme.colors.textPrimary
                    )
                }
                Row(modifier = Modifier.padding(8.dp)) {
                    Text(
                        text = "Host",
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
                    text = "DATA TO SHARE",
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
                            Text(text = it.displayName, color = RarimeTheme.colors.textPrimary)
                        }

                    }
                }

            }

            Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 20.dp)) {
                PrimaryButton(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    text = "Generate Proof",
                    size = ButtonSize.Large,
                    enabled = !isSubmitting,
                    onClick = { handleAccept() }
                )
                Spacer(modifier = Modifier.height(8.dp))
                TransparentButton(
                    modifier = Modifier
                        .fillMaxWidth(),
                    text = "Cancel",
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

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
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
        handleAccept = { },
        onCancel = { },
        selector = "1098",
        requestorId = "24",
        requestorHost = "Rarime",
    )

}
