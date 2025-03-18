package com.rarilabs.rarime.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.rarilabs.rarime.R
import com.rarilabs.rarime.ui.base.ButtonSize
import com.rarilabs.rarime.ui.theme.RarimeTheme

@Composable
fun TxConfirmBottomSheet(
    modifier: Modifier = Modifier,
    sheetState: AppSheetState = rememberAppSheetState(),
    onCancel: () -> Unit = {},
    onConfirm: () -> Unit = {},
    totalDetails: Map<String, String>,
) {
    AppBottomSheet(
        modifier = modifier,
        state = sheetState,
        fullScreen = false,
        isHeaderEnabled = false
    ) { hide ->
        TxConfirmBottomSheetContent(
            hide = { hide {} },
            onCancel = {
                hide { onCancel() }
            },
            onConfirm = {
                hide { onConfirm() }
            },
            totalDetails = totalDetails,
        )
    }
}

@Composable
private fun TxConfirmBottomSheetContent(
    hide: () -> Unit = {},
    onCancel: () -> Unit = {},
    onConfirm: () -> Unit = {},
    totalDetails: Map<String, String>,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
            .background(RarimeTheme.colors.backgroundPure)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = "Review Transaction",
                style = RarimeTheme.typography.h4,
                color = RarimeTheme.colors.textPrimary
            )

            IconButton(onClick = { hide() }) {
                AppIcon(id = R.drawable.ic_close, tint = RarimeTheme.colors.textPrimary)
            }
        }

        HorizontalDivider()

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                totalDetails.forEach { (key, value) ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            text = key,
                            style = RarimeTheme.typography.body4,
                            color = RarimeTheme.colors.textPrimary
                        )

                        Text(
                            text = value,
                            style = RarimeTheme.typography.subtitle6,
                            color = RarimeTheme.colors.textPrimary
                        )
                    }
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                PrimaryButton(
                    text = "Confirm",
                    size = ButtonSize.Large,
                    modifier = Modifier.fillMaxWidth(),
                    onClick = onConfirm
                )

                TextButton(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    onClick = onCancel
                ) {
                    Text(
                        text = "Cancel",
                        style = RarimeTheme.typography.buttonLarge,
                        color = RarimeTheme.colors.errorDark
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun TxConfirmBottomSheetContentPreview() {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Bottom,
    ) {
        TxConfirmBottomSheetContent(
            totalDetails = mapOf(
                "Address" to "1x01asd0112491",
                "Withdraw amount" to "3 RMO",
                "Fee" to "0.001 RMO"
            )
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun TxConfirmBottomSheetPreview() {
    val sheetState = rememberAppSheetState()

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        PrimaryButton(
            onClick = { sheetState.show() },
            text = "Show"
        )

        TxConfirmBottomSheet(
            modifier = Modifier,
            sheetState = sheetState,
            onCancel = {},
            onConfirm = {},
            totalDetails = mapOf(
                "Address" to "1x01asd0112491",
                "Withdraw amount" to "3 RMO",
                "Fee" to "0.001 RMO"
            )
        )
    }
}