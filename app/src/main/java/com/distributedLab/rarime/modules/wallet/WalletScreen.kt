package com.distributedLab.rarime.modules.wallet

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.absolutePadding
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.distributedLab.rarime.BaseConfig
import com.distributedLab.rarime.R
import com.distributedLab.rarime.modules.main.LocalMainViewModel
import com.distributedLab.rarime.modules.wallet.models.Transaction
import com.distributedLab.rarime.modules.wallet.models.TransactionState
import com.distributedLab.rarime.modules.wallet.view_model.WalletViewModel
import com.distributedLab.rarime.modules.wallet.walletTokens.WalletTokensList
import com.distributedLab.rarime.ui.components.AppIcon
import com.distributedLab.rarime.ui.components.HorizontalDivider
import com.distributedLab.rarime.ui.components.SecondaryIconButton
import com.distributedLab.rarime.ui.theme.RarimeTheme
import com.distributedLab.rarime.util.DateUtil
import com.distributedLab.rarime.util.NumberUtil
import com.distributedLab.rarime.util.Screen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WalletScreen(
    navigate: (String) -> Unit,
    walletViewModel: WalletViewModel = hiltViewModel(),
) {
    val mainViewModel = LocalMainViewModel.current
    val configuration = LocalConfiguration.current

    val balance by walletViewModel.balance.collectAsState()
    val transactions by walletViewModel.transactions.collectAsState()

    var scaffoldState = rememberBottomSheetScaffoldState()
    LaunchedEffect(scaffoldState.bottomSheetState.currentValue) {
        if (scaffoldState.bottomSheetState.currentValue == SheetValue.Expanded) {
            mainViewModel.setBottomBarVisibility(false)
        } else {
            mainViewModel.setBottomBarVisibility(true)
        }
    }

    BottomSheetScaffold (
        sheetPeekHeight = 275.dp,
        scaffoldState = scaffoldState,
        sheetContainerColor = RarimeTheme.colors.backgroundPure,
        sheetContent = {
            Column(
                modifier = Modifier
                    .verticalScroll(rememberScrollState())
                    .absolutePadding(left = 12.dp, right = 12.dp, bottom = 20.dp)
                    .height((configuration.screenHeightDp * 0.75).dp),
                verticalArrangement = Arrangement.spacedBy(20.dp),
            ) {
                Column (
                    modifier = Modifier.fillMaxSize()
                ) {
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
        },
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(RarimeTheme.colors.backgroundPrimary)
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(40.dp),
                modifier = Modifier.padding(20.dp)
            ) {
                Text(
                    text = stringResource(R.string.wallet_title),
                    style = RarimeTheme.typography.subtitle2,
                    color = RarimeTheme.colors.textPrimary
                )

                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = stringResource(R.string.available_rmo),
                        style = RarimeTheme.typography.body3,
                        color = RarimeTheme.colors.textSecondary
                    )
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            text = NumberUtil.formatAmount(balance),
                            style = RarimeTheme.typography.h4,
                            color = RarimeTheme.colors.textPrimary
                        )
                        // TODO: replace by select
                        Text(
                            text = BaseConfig.DENOM.uppercase(),
                            style = RarimeTheme.typography.overline2,
                            color = RarimeTheme.colors.textPrimary,
                        )
                    }
                    Text(
                        text = "---",
                        style = RarimeTheme.typography.caption2,
                        color = RarimeTheme.colors.textSecondary,
                    )
                }

                Column(
                    verticalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Column (
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            SecondaryIconButton(
                                icon = R.drawable.ic_arrow_down,
                                onClick = { navigate(Screen.Main.Wallet.Receive.route) }
                            )
                            Text(
                                text = stringResource(R.string.receive_btn),
                                color = RarimeTheme.colors.textSecondary,
                                style = RarimeTheme.typography.buttonSmall
                            )
                        }
                        Spacer(modifier = Modifier.width(32.dp))
                        Column (
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            SecondaryIconButton(
                                icon = R.drawable.ic_arrow_up,
                                onClick = { navigate(Screen.Main.Wallet.Send.route) }
                            )
                            Text(
                                text = stringResource(R.string.send_btn),
                                color = RarimeTheme.colors.textSecondary,
                                style = RarimeTheme.typography.buttonSmall
                            )
                        }
                    }

                    HorizontalDivider()
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .absolutePadding(
                        top = 20.dp,
                        left = 20.dp,
                        bottom = 20.dp,
                        right = 0.dp,
                    )
            ) {
                WalletTokensList()
            }
        }
    }
}

@Composable
private fun TransactionCard(transaction: Transaction) {
    val amountSign = if (transaction.state == TransactionState.INCOMING) "+" else "-"

    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
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
