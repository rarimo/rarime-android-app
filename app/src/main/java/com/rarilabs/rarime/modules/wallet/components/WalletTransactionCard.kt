package com.rarilabs.rarime.modules.wallet.components

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat.startActivity
import androidx.core.net.toUri
import com.rarilabs.rarime.BaseConfig
import com.rarilabs.rarime.data.tokens.Erc20Token
import com.rarilabs.rarime.data.tokens.TokenType
import com.rarilabs.rarime.manager.WalletAsset
import com.rarilabs.rarime.modules.wallet.models.Transaction
import com.rarilabs.rarime.modules.wallet.models.TransactionState
import com.rarilabs.rarime.modules.wallet.models.TransactionType
import com.rarilabs.rarime.ui.components.AppIcon
import com.rarilabs.rarime.ui.theme.RarimeTheme
import com.rarilabs.rarime.util.Constants
import com.rarilabs.rarime.util.DateUtil
import com.rarilabs.rarime.util.NumberUtil
import org.web3j.utils.Numeric
import java.math.BigInteger
import java.util.Date

@Composable
fun WalletTransactionCard(
    transaction: Transaction,
    asset: WalletAsset
) {
    val amountSign = if (transaction.state == TransactionState.INCOMING) "+" else "-"
    val txHumanAmount = NumberUtil.toHumanAmount(transaction.amount, asset.getTokenDecimals())
    val context = LocalContext.current
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                val intent = Intent(
                    Intent.ACTION_VIEW,
                    (BaseConfig.RARIMO_EXPLORER +"/"+ transaction.id
                    ).toUri()
                )
                context.startActivity(intent)
            }
    ) {

        //TODO update item
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            AppIcon(
                id = transaction.getIconId(),
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
                    text = stringResource(transaction.getStringId()),
                    style = RarimeTheme.typography.subtitle6,
                    color = RarimeTheme.colors.textPrimary
                )
                Text(
                    text = DateUtil.formatDate(transaction.date),
                    style = RarimeTheme.typography.body5,
                    color = RarimeTheme.colors.textSecondary
                )
            }
        }

        Text(
            text = "${amountSign}${
                txHumanAmount.toBigDecimal().stripTrailingZeros().toPlainString()
                    .replace(Regex("(\\.\\d{4})\\d+"), "$1")
            } ${asset.getTokenSymbol()}",
            style = RarimeTheme.typography.subtitle7,
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
fun WalletTransactionCardPreview() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(RarimeTheme.colors.backgroundPrimary)
            .padding(20.dp)
    ) {
        WalletTransactionCard(
            transaction = Transaction(
                id = "1",
                amount = Constants.AIRDROP_REWARD,
                date = Date(),
                state = TransactionState.INCOMING,
                from = "0x0",
                to = "0x0",
                tokenType = TokenType.DEFAULT,
                operationType = TransactionType.TRANSFER
            ),
            asset = WalletAsset("0x0000000", Erc20Token("0x00000000000"))
        )
    }
}