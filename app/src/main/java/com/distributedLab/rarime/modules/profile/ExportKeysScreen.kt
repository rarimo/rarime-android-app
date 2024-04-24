package com.distributedLab.rarime.modules.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.distributedLab.rarime.R
import com.distributedLab.rarime.ui.components.CardContainer
import com.distributedLab.rarime.ui.components.HorizontalDivider
import com.distributedLab.rarime.ui.components.InfoAlert
import com.distributedLab.rarime.ui.components.PrimaryTextButton
import com.distributedLab.rarime.ui.theme.RarimeTheme
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.seconds

@Composable
fun ExportKeysScreen(onBack: () -> Unit) {
    // TODO: Replace with actual private key
    val privateKey = "d4f1dc5332e5f0263746a31d3563e42ad8bef24a8989d8b0a5ad71f8d5de28a6"

    val clipboardManager = LocalClipboardManager.current
    var isCopied by remember { mutableStateOf(false) }

    if (isCopied) {
        LaunchedEffect(Unit) {
            delay(3.seconds)
            isCopied = false
        }
    }

    ProfileRouteLayout(
        title = "Export Keys",
        onBack = onBack
    ) {
        CardContainer {
            Column(verticalArrangement = Arrangement.spacedBy(20.dp)) {
                Text(
                    text = com.distributedLab.rarime.modules.register.privateKey,
                    style = RarimeTheme.typography.body3,
                    color = RarimeTheme.colors.textPrimary,
                    modifier = Modifier
                        .background(RarimeTheme.colors.componentPrimary, RoundedCornerShape(8.dp))
                        .padding(vertical = 14.dp, horizontal = 16.dp)
                )
                Row(
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    PrimaryTextButton(
                        leftIcon = if (isCopied) R.drawable.ic_check else R.drawable.ic_copy_simple,
                        text = if (isCopied) {
                            stringResource(R.string.copied_text)
                        } else {
                            stringResource(R.string.copy_to_clipboard_btn)
                        },
                        onClick = {
                            clipboardManager.setText(AnnotatedString(com.distributedLab.rarime.modules.register.privateKey))
                            isCopied = true
                        }
                    )
                }
                HorizontalDivider()
                InfoAlert(text = stringResource(R.string.new_identity_warning))
            }
        }
    }
}

@Preview
@Composable
private fun ExportKeysScreenPreview() {
    ExportKeysScreen {}
}
