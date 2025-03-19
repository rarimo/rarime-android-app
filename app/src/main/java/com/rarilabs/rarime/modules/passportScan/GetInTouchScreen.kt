package com.rarilabs.rarime.modules.passportScan

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.gson.Gson
import com.rarilabs.rarime.R
import com.rarilabs.rarime.modules.passportScan.models.EDocument
import com.rarilabs.rarime.ui.base.ButtonSize
import com.rarilabs.rarime.ui.components.PrimaryButton
import com.rarilabs.rarime.ui.theme.RarimeTheme
import com.rarilabs.rarime.util.ErrorHandler
import com.rarilabs.rarime.util.SendEmailUtil

@Composable
fun GetInTouchScreen(
    eDoc: EDocument?,
    onClose: () -> Unit,
    onSent: () -> Unit,
) {
    val context = LocalContext.current

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult(),
        onResult = { onSent() }
    )

    fun getInTouch() {
        try {
            run {
                val logsFile = ErrorHandler.getLogFile()

                eDoc?.let {
                    logsFile.writeText(Gson().toJson(it))
                }

                launcher.launch(SendEmailUtil.sendEmail(logsFile, context))
            }
        } catch (e: Exception) {
            ErrorHandler.logError("GetInTouchScreen", e.toString(), e)
        }
    }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Image(
                modifier = Modifier.size(120.dp),
                painter = painterResource(id = R.drawable.isometric_two_gears),
                contentDescription = "decor",
            )
            Spacer(modifier = Modifier.height(32.dp))
            Text(
                text = stringResource(id = R.string.get_in_touch_title),
                style = RarimeTheme.typography.subtitle4,
                color = RarimeTheme.colors.textPrimary,
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = stringResource(id = R.string.get_in_touch_subtitle),
                style = RarimeTheme.typography.body4,
                color = RarimeTheme.colors.textSecondary,
                textAlign = TextAlign.Center
            )
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            PrimaryButton(
                modifier = Modifier.fillMaxWidth(),
                size = ButtonSize.Large,
                text = stringResource(id = R.string.get_in_touch_confirm),
                rightIcon = R.drawable.ic_arrow_right,
                onClick = { getInTouch() }
            )

            TextButton(
                modifier = Modifier.fillMaxWidth(),
                onClick = onClose
            ) {
                Text(
                    text = stringResource(id = R.string.get_in_touch_cancel),
                    style = RarimeTheme.typography.buttonLarge,
                    color = RarimeTheme.colors.textPrimary,
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GetInTouchScreenPreview() {
    GetInTouchScreen(
        eDoc = null,
        onClose = {},
        onSent = {}
    )
}