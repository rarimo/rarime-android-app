package com.rarilabs.rarime.api.ext_integrator.ext_int_action_preview

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.rarilabs.rarime.R
import com.rarilabs.rarime.api.ext_integrator.models.ExtIntegratorActions
import com.rarilabs.rarime.api.ext_integrator.models.QrAction
import com.rarilabs.rarime.modules.main.LocalMainViewModel
import com.rarilabs.rarime.ui.components.AlertModalContent
import com.rarilabs.rarime.ui.components.AppIcon
import com.rarilabs.rarime.api.ext_integrator.ext_int_action_preview.handlers.ext_int_query_proof_handler.ExtIntQueryProofHandler
import com.rarilabs.rarime.ui.theme.RarimeTheme

@Composable
fun ExtIntActionPreview(
    qrAction: QrAction,
    onCancel: () -> Unit = {},
    onSuccess: () -> Unit = {},
) {
    val context = LocalContext.current
    val mainViewModel = LocalMainViewModel.current

    fun onSuccessHandler() {
        mainViewModel.setModalContent {
            AlertModalContent(
                title = "Success",
                subtitle = qrAction.getSuccessMessage(context),
                buttonText = "Ok",
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
                title = "Failed",
                subtitle = qrAction.getFailMessage(context),
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
                buttonText = "Ok",
                onClose = { mainViewModel.setModalVisibility(false) },
            )
        }
        mainViewModel.setModalVisibility(true)
    }

    when (qrAction.type) {
        ExtIntegratorActions.QueryProofGen.value -> {
            ExtIntQueryProofHandler(
                qrAction = qrAction,
                onCancel = onCancel,
                onSuccess = { onSuccessHandler() },
                onFail = { onFailHandler() }
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
