package com.distributedLab.rarime.modules.wallet.walletTokens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.absolutePadding
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import com.distributedLab.rarime.R
import com.distributedLab.rarime.ui.components.StepIndicator
import com.distributedLab.rarime.ui.theme.RarimeTheme
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

const val MOCKED_ASSETS_COUNT = 5

@Composable
fun WalletTokensList() {
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

    var halfScreenWidth = configuration.screenWidthDp / 2

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

    // FIXME: hardcoded paddings
    Column (
        verticalArrangement = Arrangement.spacedBy(20.dp),
    ) {
        Row (
            modifier = Modifier
                .fillMaxWidth()
                .absolutePadding(
                    top = 0.dp,
                    left = 0.dp,
                    bottom = 0.dp,
                    right = 20.dp,
                ),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = stringResource(
                    R.string.wallet_tokens_list_title,
                    MOCKED_ASSETS_COUNT
                ).uppercase(),
                style = RarimeTheme.typography.overline2,
                color = RarimeTheme.colors.textSecondary
            )

            // TODO: handle case, when tokens is too much to fit on the screen
            StepIndicator(
                itemsCount = MOCKED_ASSETS_COUNT,
                selectedIndex = selectedIndex,
                updateSelectedIndex = { index ->
                    coroutineScope.launch {
                        scrollToTokenByIndex(index)
                    }
                }
            )
        }

        LazyRow (
            state = scrollState,
            modifier = Modifier
                .fillMaxWidth()
                .absolutePadding(
                    right = 20.dp
                ),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            itemsIndexed(List(MOCKED_ASSETS_COUNT) { it }) { index, _ ->
                WalletTokenCard()
            }
        }
    }
}

@Preview
@Composable
private fun WalletTokensListPreview() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(RarimeTheme.colors.backgroundPrimary)
            .padding(20.dp)
    ) {
        WalletTokensList()
    }
}