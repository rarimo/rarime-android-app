package com.rarilabs.rarime.modules.wallet

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.absolutePadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
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
import com.rarilabs.rarime.R
import com.rarilabs.rarime.modules.main.LocalMainViewModel
import com.rarilabs.rarime.modules.wallet.components.WalletTransactionsList
import com.rarilabs.rarime.modules.wallet.view_model.WalletViewModel
import com.rarilabs.rarime.modules.wallet.walletTokens.WalletTokensList
import com.rarilabs.rarime.ui.base.ButtonIconSize
import com.rarilabs.rarime.ui.components.CardContainer
import com.rarilabs.rarime.ui.components.DropdownOption
import com.rarilabs.rarime.ui.components.HorizontalDivider
import com.rarilabs.rarime.ui.components.SecondaryIconButton
import com.rarilabs.rarime.ui.components.TextDropdown
import com.rarilabs.rarime.ui.theme.RarimeTheme
import com.rarilabs.rarime.util.ErrorHandler
import com.rarilabs.rarime.util.NumberUtil
import com.rarilabs.rarime.util.Screen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WalletScreen(
    navigate: (String) -> Unit,
    walletViewModel: WalletViewModel = hiltViewModel(),
) {
    val mainViewModel = LocalMainViewModel.current

    val userAssets by walletViewModel.walletAssets.collectAsState()
    val selectedUserAsset by walletViewModel.selectedWalletAsset.collectAsState()

    val scaffoldState = rememberBottomSheetScaffoldState()
    LaunchedEffect(scaffoldState.bottomSheetState.currentValue) {
        if (scaffoldState.bottomSheetState.currentValue == SheetValue.Expanded) {
            mainViewModel.setBottomBarVisibility(false)
        } else {
            mainViewModel.setBottomBarVisibility(true)
        }
    }

    LaunchedEffect(Unit) {
        walletViewModel.updateBalances()
    }


    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(RarimeTheme.colors.backgroundPrimary)
            .verticalScroll(rememberScrollState())
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(40.dp),
            modifier = Modifier
                .padding(vertical = 20.dp, horizontal = 12.dp)
        ) {
            Text(
                text = stringResource(R.string.wallet_title),
                style = RarimeTheme.typography.subtitle4,
                color = RarimeTheme.colors.textPrimary,
                modifier = Modifier.padding(horizontal = 8.dp)
            )

            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = stringResource(R.string.available_rmo),
                    style = RarimeTheme.typography.body4,
                    color = RarimeTheme.colors.textSecondary
                )
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = NumberUtil.formatAmount(selectedUserAsset.humanBalance()),
                        style = RarimeTheme.typography.h4,
                        color = RarimeTheme.colors.textPrimary
                    )

                    // TODO: rollback at next releases
                    if (userAssets.size > 1) {
                        TextDropdown(
                            value = selectedUserAsset.token.symbol,
                            options = userAssets.map {
                                DropdownOption(
                                    label = it.token.symbol,
                                    value = it.token.symbol
                                )
                            },
                            onChange = { symb ->
                                run {
                                    val asset = userAssets.find { it.token.symbol == symb }
                                    ErrorHandler.logDebug("onChange: walletViewModel:", symb)
                                    ErrorHandler.logDebug(
                                        "onChange: asset:",
                                        asset?.token?.symbol ?: "nope"
                                    )

                                    asset?.let { newAsset ->
                                        walletViewModel.updateSelectedWalletAsset(
                                            newAsset
                                        )
                                    }
                                }
                            }
                        )
                    } else {
                        Text(
                            text = selectedUserAsset.token.symbol,
                            style = RarimeTheme.typography.overline2,
                            color = RarimeTheme.colors.textPrimary
                        )
                    }
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
                    Column(
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        SecondaryIconButton(
                            size = ButtonIconSize.Large,
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
                    Column(
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        SecondaryIconButton(
                            size = ButtonIconSize.Large,
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
                .padding(vertical = 20.dp)
        ) {
            WalletTokensList(walletViewModel)
        }

        Column(
            verticalArrangement = Arrangement.spacedBy(20.dp),
            modifier = Modifier.padding(horizontal = 12.dp)
        ) {
            HorizontalDivider()
            CardContainer {
                WalletTransactionsList(
                    modifier = Modifier.fillMaxSize(),
                    transactions = selectedUserAsset.transactions.value,
                    walletAsset = selectedUserAsset
                )
            }
        }
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
