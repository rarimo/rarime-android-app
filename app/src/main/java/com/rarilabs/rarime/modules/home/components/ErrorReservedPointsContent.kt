package com.rarilabs.rarime.modules.home.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.rarilabs.rarime.R
import com.rarilabs.rarime.ui.base.ButtonSize
import com.rarilabs.rarime.ui.components.CircledBadge
import com.rarilabs.rarime.ui.components.HorizontalDivider
import com.rarilabs.rarime.ui.components.PrimaryButton
import com.rarilabs.rarime.ui.theme.RarimeTheme

@Composable
fun ErrorReservedPointsContent(onClose: () -> Unit, title: String, description: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(RarimeTheme.colors.backgroundPure, RoundedCornerShape(24.dp))
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(20.dp)
        ) {
            CircledBadge(iconId = R.drawable.ic_warning) {

            }
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = title,
                    style = RarimeTheme.typography.h6,
                    color = RarimeTheme.colors.textPrimary
                )
                Text(
                    text = description,
                    style = RarimeTheme.typography.body2,
                    color = RarimeTheme.colors.textSecondary,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.width(240.dp)
                )
            }
            HorizontalDivider()
            PrimaryButton(
                text = stringResource(R.string.okay_btn),
                size = ButtonSize.Large,
                modifier = Modifier.fillMaxWidth(),
                onClick = onClose
            )
//            SecondaryButton(
//                text = "Share Achievement",
//                leftIcon = R.drawable.ic_share,
//                size = ButtonSize.Large,
//                modifier = Modifier.fillMaxWidth(),
//                onClick = {
//                    // TODO: add share
//                    onClose()
//                }
//            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ErrorReservedPointsContentPreview() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(RarimeTheme.colors.backgroundPrimary)
            .padding(24.dp)
    ) {
        Dialog(onDismissRequest = {}) {
            ErrorReservedPointsContent(onClose = {}, "Header", "Description")
        }
    }
}
