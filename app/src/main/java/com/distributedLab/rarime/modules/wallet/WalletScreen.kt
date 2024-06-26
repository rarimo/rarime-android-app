package com.distributedLab.rarime.modules.wallet

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.distributedLab.rarime.R
import com.distributedLab.rarime.modules.wallet.models.Transaction
import com.distributedLab.rarime.modules.wallet.models.TransactionState
import com.distributedLab.rarime.modules.wallet.view_model.WalletViewModel
import com.distributedLab.rarime.ui.components.AppIcon
import com.distributedLab.rarime.ui.components.CardContainer
import com.distributedLab.rarime.ui.components.HorizontalDivider
import com.distributedLab.rarime.ui.components.SecondaryButton
import com.distributedLab.rarime.ui.theme.RarimeTheme
import com.distributedLab.rarime.util.DateUtil
import com.distributedLab.rarime.util.NumberUtil
import com.distributedLab.rarime.util.Screen

@Composable
fun WalletScreen(
    navigate: (String) -> Unit,
    walletViewModel: WalletViewModel = hiltViewModel(),
) {
    val balance by walletViewModel.balance.collectAsState()
    val transactions by walletViewModel.transactions.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(RarimeTheme.colors.backgroundPrimary)
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(32.dp),
            modifier = Modifier
                .background(RarimeTheme.colors.backgroundPure)
                .padding(20.dp)
        ) {
            Text(
                text = stringResource(R.string.wallet_title),
                style = RarimeTheme.typography.subtitle2,
                color = RarimeTheme.colors.textPrimary
            )
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = stringResource(R.string.available_rmo),
                    style = RarimeTheme.typography.body3,
                    color = RarimeTheme.colors.textSecondary
                )
                Text(
                    text = NumberUtil.formatAmount(balance),
                    style = RarimeTheme.typography.h4,
                    color = RarimeTheme.colors.textPrimary
                )
            }
            Column(verticalArrangement = Arrangement.spacedBy(20.dp)) {
                HorizontalDivider()
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    SecondaryButton(text = stringResource(R.string.receive_btn),
                        leftIcon = R.drawable.ic_arrow_down,
                        modifier = Modifier.weight(1f),
                        onClick = { navigate(Screen.Main.Wallet.Receive.route) })
                    SecondaryButton(text = stringResource(R.string.send_btn),
                        leftIcon = R.drawable.ic_arrow_up,
                        modifier = Modifier.weight(1f),
                        onClick = { navigate(Screen.Main.Wallet.Send.route) })
                }
            }
        }
        Column(modifier = Modifier.padding(12.dp)) {
            CardContainer {
                Column(verticalArrangement = Arrangement.spacedBy(20.dp)) {
                    Text(
                        text = stringResource(R.string.transactions_title),
                        style = RarimeTheme.typography.subtitle3,
                        color = RarimeTheme.colors.textPrimary
                    )
                    transactions.forEach {
                        TransactionCard(it)
                    }

                    if (transactions.isEmpty()) {
                        Text(
                            text = stringResource(R.string.no_transactions_msg),
                            style = RarimeTheme.typography.body3,
                            color = RarimeTheme.colors.textSecondary
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun TransactionCard(transaction: Transaction) {
    val amountSign = if (transaction.state == TransactionState.INCOMING) "+" else "-"

    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = androidx.compose.ui.Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            AppIcon(
                id = transaction.iconId,
                size = 20.dp,
                tint = RarimeTheme.colors.textSecondary,
                modifier = Modifier
                    .background(
                        RarimeTheme.colors.componentPrimary, shape = CircleShape
                    )
                    .padding(10.dp)
            )
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = stringResource(transaction.titleId),
                    style = RarimeTheme.typography.subtitle4,
                    color = RarimeTheme.colors.textPrimary
                )
                Text(
                    text = DateUtil.formatDate(transaction.date),
                    style = RarimeTheme.typography.body4,
                    color = RarimeTheme.colors.textSecondary
                )
            }
        }

        Text(
            text = "${amountSign}${NumberUtil.formatAmount(transaction.amount)} RMO",
            style = RarimeTheme.typography.subtitle5,
            color = if (transaction.state == TransactionState.INCOMING) {
                RarimeTheme.colors.successMain
            } else {
                RarimeTheme.colors.errorMain
            }
        )
    }
}

@Preview
@Composable
private fun WalletScreenPreview() {
    WalletScreen(
//        balance = 100.0,
//        transactions = listOf(
//            Transaction(
//                id = 2,
//                iconId = R.drawable.ic_arrow_up,
//                titleId = R.string.send_btn,
//                date = Date(),
//                amount = 100.0,
//                state = TransactionState.OUTGOING
//            ),
//            Transaction(
//                id = 1,
//                iconId = R.drawable.ic_airdrop,
//                titleId = R.string.airdrop_tx_title,
//                date = Date(),
//                amount = 100.0,
//                state = TransactionState.INCOMING
//            )
//        ),
        navigate = {})
}
