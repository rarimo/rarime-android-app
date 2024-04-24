package com.distributedLab.rarime.modules.wallet

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.distributedLab.rarime.R
import com.distributedLab.rarime.ui.base.ButtonSize
import com.distributedLab.rarime.ui.components.AppTextField
import com.distributedLab.rarime.ui.components.CardContainer
import com.distributedLab.rarime.ui.components.PrimaryButton
import com.distributedLab.rarime.ui.components.SecondaryTextButton
import com.distributedLab.rarime.ui.components.VerticalDivider
import com.distributedLab.rarime.ui.theme.RarimeTheme

@Composable
fun WalletSendScreen(onBack: () -> Unit) {
    WalletRouteLayout(
        title = "Send RMO",
        description = "Withdraw the RMO token",
        onBack = onBack
    ) {
        Column(
            verticalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxSize()
        ) {
            CardContainer(modifier = Modifier.padding(horizontal = 20.dp)) {
                Column(verticalArrangement = Arrangement.spacedBy(20.dp)) {
                    AppTextField(
                        label = "Address",
                        placeholder = "rarimo1...",
                        trailingItem = {
                            SecondaryTextButton(
                                leftIcon = R.drawable.ic_qr_code,
                                onClick = { /*TODO*/ }
                            )
                        }
                    )
                    AppTextField(
                        label = "Amount",
                        placeholder = "Enter amount in RMO",
                        hint = {
                            Row(
                                horizontalArrangement = Arrangement.SpaceBetween,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = "Available:",
                                    style = RarimeTheme.typography.body4,
                                    color = RarimeTheme.colors.textSecondary
                                )
                                Text(
                                    text = "3 RMO",
                                    style = RarimeTheme.typography.body4,
                                    color = RarimeTheme.colors.textPrimary
                                )
                            }
                        },
                        trailingItem = {
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(16.dp),
                                modifier = Modifier
                                    .width(64.dp)
                                    .height(20.dp)
                            ) {
                                VerticalDivider()
                                SecondaryTextButton(
                                    text = "MAX",
                                    onClick = { /*TODO*/ }
                                )
                            }
                        }
                    )
                }
            }
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .fillMaxWidth()
                    .background(RarimeTheme.colors.backgroundPure)
                    .padding(top = 12.dp, bottom = 20.dp)
                    .padding(horizontal = 20.dp),
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = "Receiver gets",
                        style = RarimeTheme.typography.body3,
                        color = RarimeTheme.colors.textSecondary
                    )
                    Text(
                        text = "0.00 RMO",
                        style = RarimeTheme.typography.subtitle3,
                        color = RarimeTheme.colors.textPrimary
                    )
                }
                PrimaryButton(
                    text = "Send",
                    size = ButtonSize.Large,
                    modifier = Modifier.width(160.dp),
                    onClick = { /*TODO*/ }
                )
            }
        }
    }
}

@Preview
@Composable
private fun WalletSendScreenPreview() {
    WalletSendScreen {}
}
