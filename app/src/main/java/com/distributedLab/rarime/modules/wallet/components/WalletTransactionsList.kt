package com.distributedLab.rarime.modules.wallet.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.distributedLab.rarime.R
import com.distributedLab.rarime.data.tokens.Erc20Token
import com.distributedLab.rarime.manager.WalletAsset
import com.distributedLab.rarime.modules.wallet.models.Transaction
import com.distributedLab.rarime.modules.wallet.models.TransactionState
import com.distributedLab.rarime.ui.theme.RarimeTheme
import com.distributedLab.rarime.util.Constants
import java.util.Date

@Composable
fun WalletTransactionsList(
    modifier: Modifier = Modifier,
    walletAsset: WalletAsset,
    transactions: List<Transaction>
) {
    Column (
        modifier = modifier.fillMaxSize()
    ) {
        Text(
            text = stringResource(R.string.transactions_title),
            style = RarimeTheme.typography.subtitle3,
            color = RarimeTheme.colors.textPrimary
        )

        Spacer(modifier = Modifier.height(20.dp))

        if (transactions.isEmpty()) {
            Text(
                text = stringResource(R.string.no_transactions_msg),
                style = RarimeTheme.typography.body3,
                color = RarimeTheme.colors.textSecondary
            )
        } else {
            Column(
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                transactions.forEach {
                    WalletTransactionCard(it, walletAsset)
                }
            }
        }
    }
}

@Preview
@Composable
fun WalletTransactionsListPreview() {
    Column (
        modifier = Modifier
            .fillMaxSize()
            .background(RarimeTheme.colors.backgroundPrimary)
            .padding(20.dp)
    ) {
        WalletTransactionsList(
            walletAsset = WalletAsset("0x000000", Erc20Token("0x00000000")),
            transactions = listOf(
                Transaction(
                    id = 1,
                    iconId = R.drawable.ic_airdrop,
                    titleId = R.string.airdrop_tx_title,
                    amount = Constants.AIRDROP_REWARD,
                    date = Date(),
                    state = TransactionState.INCOMING
                ),
                Transaction(
                    id = 2,
                    iconId = R.drawable.ic_arrow_up,
                    titleId = R.string.airdrop_tx_title,
                    amount = Constants.AIRDROP_REWARD,
                    date = Date(),
                    state = TransactionState.INCOMING
                ),
                Transaction(
                    id = 3,
                    iconId = R.drawable.ic_arrow_down,
                    titleId = R.string.airdrop_tx_title,
                    amount = Constants.AIRDROP_REWARD,
                    date = Date(),
                    state = TransactionState.INCOMING
                ),
                Transaction(
                    id = 4,
                    iconId = R.drawable.ic_shield_check,
                    titleId = R.string.airdrop_tx_title,
                    amount = Constants.AIRDROP_REWARD,
                    date = Date(),
                    state = TransactionState.INCOMING
                ),
            )
        )
    }
}