package com.distributedLab.rarime.modules.wallet.walletTokens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.distributedLab.rarime.R
import com.distributedLab.rarime.modules.wallet.view_model.WalletViewModel
import com.distributedLab.rarime.ui.components.StepIndicator
import com.distributedLab.rarime.ui.theme.RarimeTheme
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

// TODO: md just listof tokens?
@Composable
fun WalletTokensList(walletViewModel: WalletViewModel) {
    val userAssets by walletViewModel.walletAssets.collectAsState()
    val selectedUserAsset by walletViewModel.selectedWalletAsset.collectAsState()

    val configuration = LocalConfiguration.current

    var selectedIndex by remember {
        mutableStateOf(0)
    }
    val scrollState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    suspend fun scrollToTokenByIndex(index: Int) {
        selectedIndex = index
        scrollState.animateScrollToItem(index)
    }

    LaunchedEffect(selectedUserAsset) {
        scrollToTokenByIndex(userAssets.indexOf(selectedUserAsset))
    }

    val halfScreenWidth = configuration.screenWidthDp / 2

    LaunchedEffect(scrollState) {
        snapshotFlow { scrollState.layoutInfo.visibleItemsInfo }
            .map { visibleItems ->
                visibleItems.find { item ->
                    val itemStart = item.offset
                    val itemEnd = item.offset + item.size

                    itemStart < halfScreenWidth && itemEnd > halfScreenWidth
                }?.index
            }
            .distinctUntilChanged()
            .collect { index ->
                if (index != null) {
                    selectedIndex = index
                }
            }
    }

    Column (
        verticalArrangement = Arrangement.spacedBy(20.dp),
    ) {
        Row (
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = stringResource(
                    R.string.wallet_tokens_list_title,
                    userAssets.size,
                ).uppercase(),
                style = RarimeTheme.typography.overline2,
                color = RarimeTheme.colors.textSecondary
            )

            // TODO: handle case, when tokens is too much to fit on the screen
            StepIndicator(
                itemsCount = userAssets.size,
                selectedIndex = selectedIndex,
                updateSelectedIndex = { index ->
                    coroutineScope.launch {
                        scrollToTokenByIndex(index)
                    }
                }
            )
        }

        Row (
            modifier = Modifier.fillMaxWidth(),
        ) {
            LazyRow (
                state = scrollState,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(start = 20.dp, end = 20.dp)
            ) {
                items(userAssets) {
                    WalletTokenCard(it)
                }
            }
        }
    }
}

@Preview
@Composable
private fun WalletTokensListPreview(walletViewModel: WalletViewModel = hiltViewModel()) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(RarimeTheme.colors.backgroundPrimary)
            .padding(20.dp)
    ) {
        WalletTokensList(walletViewModel)
    }
}