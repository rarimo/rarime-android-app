package com.rarilabs.rarime.api.ext_integrator.ext_int_action_preview.handlers.light_proof_handler

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.rarilabs.rarime.R
import com.rarilabs.rarime.api.ext_integrator.ext_int_action_preview.components.HandlerPreviewerLayout
import com.rarilabs.rarime.api.ext_integrator.ext_int_action_preview.components.HandlerPreviewerLayoutTexts
import com.rarilabs.rarime.modules.main.LocalMainViewModel
import com.rarilabs.rarime.ui.components.AlertModalContent
import com.rarilabs.rarime.ui.components.AppIcon
import com.rarilabs.rarime.ui.theme.RarimeTheme

@Composable
fun LightProofHandler(
    queryParams: Map<String, String?>?,
    onCancel: () -> Unit = {},
    onSuccess: () -> Unit = {},
    onFail: () -> Unit = {},

    viewModel: LightProofHandlerViewModel = hiltViewModel()
) {
    val mainViewModel = LocalMainViewModel.current

    fun onSuccessHandler() {
        mainViewModel.setModalContent {
            AlertModalContent(
                title = "Success", // TODO: translate me
                subtitle = "You have successfully withdraw", // TODO: translate me
                buttonText = "Ok", // TODO: translate me
                onClose = {
                    mainViewModel.setModalVisibility(false)
                    onSuccess.invoke()
                },
            )
        }
        mainViewModel.setModalVisibility(true)
    }

    fun onFailHandler() {
        mainViewModel.setModalContent {
            AlertModalContent(
                withConfetti = false,
                title = "Failed", // TODO: translate me
                subtitle = "Failed to withdraw", // TODO: translate me
                mediaContent = {
                    AppIcon(
                        id = R.drawable.ic_warning,
                        size = 24.dp,
                        tint = RarimeTheme.colors.baseWhite,
                        modifier = Modifier
                            .background(RarimeTheme.colors.errorMain, CircleShape)
                            .padding(28.dp)
                    )
                },
                buttonBg = RarimeTheme.colors.errorMain,
                buttonColor = RarimeTheme.colors.baseWhite,
                buttonText = "Ok", // TODO: translate me
                onClose = {
                    mainViewModel.setModalVisibility(false)
                    onFail.invoke()
                },
            )
        }
        mainViewModel.setModalVisibility(true)
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
            title = "Light verification" // TODO: translate me
        ),

        onSuccess = { onSuccessHandler() },
        onFail = { onFailHandler() },
        onCancel = onCancel,
    )
}