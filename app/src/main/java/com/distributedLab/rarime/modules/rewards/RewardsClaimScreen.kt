package com.distributedLab.rarime.modules.rewards

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text2.input.rememberTextFieldState
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.distributedLab.rarime.R
import com.distributedLab.rarime.modules.rewards.view_models.RewardsClaimViewModel
import com.distributedLab.rarime.modules.wallet.WalletRouteLayout
import com.distributedLab.rarime.ui.components.AppTextField
import com.distributedLab.rarime.ui.components.CardContainer
import com.distributedLab.rarime.ui.components.HorizontalDivider
import com.distributedLab.rarime.ui.components.PrimaryButton
import com.distributedLab.rarime.ui.components.SecondaryTextButton
import com.distributedLab.rarime.ui.components.rememberAppTextFieldState
import com.distributedLab.rarime.ui.theme.RarimeTheme
import com.distributedLab.rarime.util.NumberUtil
import kotlinx.coroutines.launch

@Composable
fun RewardsClaimScreen(
    onBack: () -> Unit,
    rewardsClaimViewModel: RewardsClaimViewModel = hiltViewModel()
) {
    val scope = rememberCoroutineScope()

    val walletAssets = rewardsClaimViewModel.walletAssets.collectAsState()

    val pointsWalletAsset = rewardsClaimViewModel.pointsWalletAsset.collectAsState()
    val rarimoWalletAsset = rewardsClaimViewModel.rarimoWalletAsset.collectAsState()

    val amountInputState = rememberAppTextFieldState("")

    suspend fun submit () {
        try {
            rewardsClaimViewModel.withdrawPoints(amountInputState.text.toDouble())

            rewardsClaimViewModel.reloadWalletAssets()
            onBack()
        } catch (e: Exception) {

        }
    }

    LaunchedEffect(walletAssets) {
        rewardsClaimViewModel.updateScreenWalletAssets()
    }

    if (pointsWalletAsset.value == null || rarimoWalletAsset.value == null) {
        // TODO: implement loader and error view
        Text(text = "Loading...")
    } else {
        WalletRouteLayout(
            headerModifier = Modifier.padding(horizontal = 20.dp),
            title = stringResource(id = R.string.rewards_claim_screen_title, pointsWalletAsset.value!!.token.symbol),
            description = stringResource(id = R.string.rewards_claim_screen_subtitle),
            onBack = onBack
        ) {
            Column (
                verticalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxSize()
            ) {
                CardContainer (
                    modifier = Modifier.padding(horizontal = 12.dp)
                ) {
                    Column (
                        verticalArrangement = Arrangement.spacedBy(20.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(RarimeTheme.colors.backgroundPrimary)
                                .fillMaxWidth()
                        ) {
                            Column {
                                Row (
                                    verticalAlignment = Alignment.CenterVertically,
                                ) {
                                    Row (
                                        modifier = Modifier.fillMaxWidth(0.2f),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(
                                            text = stringResource(id = R.string.rewards_claim_screen_from_lbl),
                                            style = RarimeTheme.typography.buttonMedium,
                                            color = RarimeTheme.colors.textSecondary,
                                            modifier = Modifier.padding(horizontal = 16.dp)
                                        )

                                        VerticalDivider(
                                            modifier = Modifier.height(20.dp),
                                            color = RarimeTheme.colors.componentPrimary
                                        )
                                    }

                                    Column {
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(16.dp),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Text(
                                                text = stringResource(id = R.string.rewards_claim_screen_from_name),
                                                style = RarimeTheme.typography.body3,
                                                color = RarimeTheme.colors.textPrimary,
                                                modifier = Modifier.padding(horizontal = 16.dp)
                                            )

                                            Text(
                                                text = "${NumberUtil.formatAmount(pointsWalletAsset.value!!.humanBalance())} ${pointsWalletAsset.value!!.token.symbol}",
                                                style = RarimeTheme.typography.subtitle5,
                                                color = RarimeTheme.colors.textPrimary,
                                            )
                                        }

                                        HorizontalDivider(
                                            modifier = Modifier
                                                .height(1.dp)
                                                .background(RarimeTheme.colors.componentPrimary)
                                        )
                                    }
                                }

                                Row (
                                    verticalAlignment = Alignment.CenterVertically,
                                ) {
                                    Row (
                                        modifier = Modifier.fillMaxWidth(0.2f),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(
                                            text = stringResource(id = R.string.rewards_claim_screen_to_lbl),
                                            style = RarimeTheme.typography.buttonMedium,
                                            color = RarimeTheme.colors.textSecondary,
                                            modifier = Modifier.padding(horizontal = 16.dp)
                                        )

                                        VerticalDivider(
                                            modifier = Modifier.height(20.dp),
                                            color = RarimeTheme.colors.componentPrimary
                                        )
                                    }

                                    Column {
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(16.dp),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Text(
                                                text = stringResource(id = R.string.rewards_claim_screen_to_name),
                                                style = RarimeTheme.typography.body3,
                                                color = RarimeTheme.colors.textPrimary,
                                                modifier = Modifier.padding(horizontal = 16.dp)
                                            )

                                            Text(
                                                text = "${NumberUtil.formatAmount(rarimoWalletAsset.value!!.humanBalance())} ${rarimoWalletAsset.value!!.token.symbol}",
                                                style = RarimeTheme.typography.subtitle5,
                                                color = RarimeTheme.colors.textPrimary,
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        AppTextField(
                            state = amountInputState,
                            label = stringResource(id = R.string.rewards_claim_screen_input_lbl),
                            placeholder = stringResource(id = R.string.rewards_claim_screen_input_placeholder),
                            trailingItem = {
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                                    modifier = Modifier
                                        .width(64.dp)
                                        .height(20.dp)
                                ) {
                                    VerticalDivider()
                                    SecondaryTextButton(
                                        text = stringResource(R.string.max_btn),
                                        onClick = {  }
                                    )
                                }
                            }
                        )
                    }
                }

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(RarimeTheme.colors.backgroundPure)
                        .padding(horizontal = 20.dp, vertical = 12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text (
                        text = stringResource(id = R.string.rewards_claim_screen_warning),
                        textAlign = TextAlign.Center,
                        style = RarimeTheme.typography.body4,
                        color = RarimeTheme.colors.textSecondary
                    )

                    PrimaryButton(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = {
                                  scope.launch {
                                      submit()
                                  }
                        },
                        text = stringResource(id = R.string.rewards_claim_screen_claim_btn),
                    )
                }
            }
        }
    }
}

@Preview
@Composable
fun RewardsClaimScreenPreview() {
    RewardsClaimScreen(onBack = {})
}