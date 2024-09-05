package com.rarilabs.rarime.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import com.rarilabs.rarime.R
import com.rarilabs.rarime.api.ext_integrator.models.QrAction
import com.rarilabs.rarime.modules.main.LocalMainViewModel
import com.rarilabs.rarime.ui.base.ButtonSize
import com.rarilabs.rarime.ui.theme.RarimeTheme
import com.rarilabs.rarime.util.ErrorHandler
import kotlinx.coroutines.launch

@Composable
fun ExtIntActionPreview(
    qrAction: QrAction,
    qrActionHandler: suspend (qrAction: QrAction) -> Unit = {},
    onCancel: () -> Unit = {},
    onSuccess: () -> Unit = {},
) {
    val context = LocalContext.current
    val mainViewModel = LocalMainViewModel.current

    ExtIntActionPreviewContent(
        qrAction = qrAction,
        qrActionHandler = qrActionHandler,
        onCancel = onCancel,
        onSuccess = {
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
        },
        onFail = {
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
    )
}

@Composable
fun ExtIntActionPreviewContent(
    qrAction: QrAction,
    qrActionHandler: suspend (qrAction: QrAction) -> Unit = {},
    onSuccess: () -> Unit = {},
    onFail: () -> Unit = {},
    onCancel: () -> Unit = {}
) {
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    var isSubmitting by remember { mutableStateOf(false) }

    val sheetState = rememberAppSheetState(true)

    fun handleAccept() {
        scope.launch {
            isSubmitting = true

            try {
                qrActionHandler.invoke(qrAction)
                onSuccess.invoke()
            } catch (e: Exception) {
                ErrorHandler.logError("ExtIntActionPreview", "handleAccept", e)
                onFail.invoke()
            }

            sheetState.hide()
            isSubmitting = false
        }
    }

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
                    bottom = 16.dp
                ),
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

            qrAction.getPreviewFields(context).forEach { (key, value) ->
                ExtIntActionPreviewRow(
                    key = key,
                    value = value
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

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
fun ExtIntActionPreviewDefault() {
    ExtIntActionPreviewContent(
        qrAction = QrAction(
            id = "19",
            type = "QueryProofGen",
            callbackUrl = "https://some-large-call-back-url.com?with=parameters,and=more,options=true",
            dataUrl = "https://some-large-data-url.com?with=parameters,and=more,options=true"
        )
    )
}