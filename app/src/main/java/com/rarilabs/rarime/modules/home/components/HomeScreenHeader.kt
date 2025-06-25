package com.rarilabs.rarime.modules.home.components

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.rarilabs.rarime.R
import com.rarilabs.rarime.data.enums.PassportStatus
import com.rarilabs.rarime.data.tokens.PreviewerToken
import com.rarilabs.rarime.manager.WalletAsset
import com.rarilabs.rarime.modules.home.LocalHomeViewModel
import com.rarilabs.rarime.modules.main.LocalMainViewModel
import com.rarilabs.rarime.modules.qr.ScanQrScreen
import com.rarilabs.rarime.ui.components.AppIcon
import com.rarilabs.rarime.ui.components.CircledBadgeWithCounter
import com.rarilabs.rarime.ui.components.SecondaryTextButton
import com.rarilabs.rarime.ui.theme.RarimeTheme
import com.rarilabs.rarime.util.ErrorHandler
import com.rarilabs.rarime.util.NumberUtil
import com.rarilabs.rarime.util.Screen
import kotlinx.coroutines.launch

@Composable
fun HomeScreenHeader(
    navigate: (String) -> Unit,
    walletAsset: WalletAsset,
    onBalanceClick: () -> Unit = {}
) {
    val scope = rememberCoroutineScope()
    val mainViewModel = LocalMainViewModel.current
    val homeViewModel = LocalHomeViewModel.current

    var isQrCodeScannerOpen by remember { mutableStateOf(false) }

    val passportStatus = mainViewModel.passportStatus.collectAsState()
    val notifications by homeViewModel.notReadNotifications.collectAsState()

    fun showQrScanner() {
        mainViewModel.setBottomBarVisibility(false)
        isQrCodeScannerOpen = true
    }

    fun hideQrScanner() {
        mainViewModel.setBottomBarVisibility(true)
        isQrCodeScannerOpen = false
    }

    fun onCompletion(text: String) {
        scope.launch {
            try {
                hideQrScanner()
                mainViewModel.setExtIntDataURI(Uri.parse(text))
            } catch (e: Exception) {
                ErrorHandler.logError("HomeScreenHeader", "HomeScreenHeaderError", e)
            }
        }
    }

    if (isQrCodeScannerOpen) {
        ScanQrScreen(
            onBack = { hideQrScanner() },
            onScan = { onCompletion(it) }
        )
    }


    HomeScreenHeaderContent(
        walletAsset = walletAsset,
        onBalanceClick = onBalanceClick,
        actionContent = {
            Row(
                horizontalArrangement = Arrangement.spacedBy(24.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (passportStatus.value != PassportStatus.UNSCANNED) {
                    CircledBadgeWithCounter(
                        modifier = Modifier.clickable(
                            indication = null,
                            interactionSource = remember { MutableInteractionSource() },
                            onClick = { showQrScanner() }
                        ),
                        iconId = R.drawable.ic_qr_code,
                        contentSize = 20,
                        contentColor = RarimeTheme.colors.textPrimary,
                        containerSize = 40,
                        containerColor = RarimeTheme.colors.backgroundPrimary
                    )
                }
            }
        },
        onNotificationClick = { navigate(Screen.NotificationsList.route) },
        notificationsSize = notifications.filter { it.isActive }.size
    )
}

@Composable
fun HomeScreenHeaderContent(
    walletAsset: WalletAsset,
    onBalanceClick: () -> Unit = {},
    actionContent: @Composable () -> Unit = {},
    onNotificationClick: () -> Unit,
    notificationsSize: Int
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier.padding(horizontal = 8.dp)
    ) {
        Row(
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                SecondaryTextButton(onClick = onBalanceClick) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = stringResource(R.string.balance_rmo, walletAsset.getTokenName()),
                            style = RarimeTheme.typography.body3,
                            color = RarimeTheme.colors.textSecondary
                        )
                        AppIcon(
                            id = R.drawable.ic_caret_right,
                            size = 16.dp,
                            tint = RarimeTheme.colors.textSecondary
                        )
                    }
                }
                Text(
                    text = NumberUtil.formatAmount(walletAsset.humanBalance()),
                    style = RarimeTheme.typography.h4,
                    color = RarimeTheme.colors.textPrimary
                )
            }


            Spacer(modifier = Modifier.weight(1f))

            actionContent()

            Spacer(modifier = Modifier.width(20.dp))

            CircledBadgeWithCounter(
                modifier = Modifier.clickable { onNotificationClick.invoke() },
                iconId = R.drawable.ic_bell,
                containerSize = 40,
                count = notificationsSize,
                containerColor = RarimeTheme.colors.backgroundPrimary,
                contentSize = 20,
                badgeSize = 16,
                contentColor = RarimeTheme.colors.textPrimary
            )
        }
    }
}

@Preview
@Composable
fun HomeScreenHeaderContentPreview() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .background(RarimeTheme.colors.backgroundPrimary)
    ) {
        HomeScreenHeaderContent(
            walletAsset = WalletAsset(
                "0x000000",
                PreviewerToken("0x00000000", "Reserved RMO", "RRMO")
            ),
            actionContent = {
                CircledBadgeWithCounter(
                    iconId = R.drawable.ic_qr_code,
                    contentSize = 20,
                    contentColor = RarimeTheme.colors.textPrimary,
                    containerSize = 40,
                    containerColor = RarimeTheme.colors.backgroundPrimary
                )
            },
            onNotificationClick = {},
            notificationsSize = 5
        )
    }
}