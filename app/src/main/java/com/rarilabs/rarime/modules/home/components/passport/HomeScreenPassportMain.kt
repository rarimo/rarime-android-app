package com.rarilabs.rarime.modules.home.components.passport

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.rarilabs.rarime.R
import com.rarilabs.rarime.data.enums.PassportStatus
import com.rarilabs.rarime.data.tokens.PreviewerToken
import com.rarilabs.rarime.manager.WalletAsset
import com.rarilabs.rarime.modules.home.LocalHomeViewModel
import com.rarilabs.rarime.modules.home.components.HomeScreenHeader
import com.rarilabs.rarime.modules.home.components.RarimeInfoScreen
import com.rarilabs.rarime.ui.components.ActionCard
import com.rarilabs.rarime.ui.components.ActionCardVariants
import com.rarilabs.rarime.ui.components.AppBottomSheet
import com.rarilabs.rarime.ui.components.AppIcon
import com.rarilabs.rarime.ui.components.enter_program.EnterProgramFlow
import com.rarilabs.rarime.ui.components.rememberAppSheetState
import com.rarilabs.rarime.util.Constants
import com.rarilabs.rarime.util.Screen
import kotlinx.coroutines.launch

@Composable
fun HomeScreenPassportMain(
    navigate: (String) -> Unit
) {
    val homeViewModel = LocalHomeViewModel.current

    val rmoAsset = homeViewModel.rmoAsset.collectAsState()

    rmoAsset.value?.let {
        HomeScreenPassportMainContent(navigate, it)
    }
}

@Composable
fun HomeScreenPassportMainContent(
    navigate: (String) -> Unit, rmoAsset: WalletAsset
) {
    val scope = rememberCoroutineScope()

    val homeViewModel = LocalHomeViewModel.current

    var isLoading by remember { mutableStateOf(false) }

    val passport = homeViewModel.passport

    val passportCardLook by homeViewModel.passportCardLook
    val passportIdentifiers by homeViewModel.passportIdentifiers
    val isIncognito by homeViewModel.isIncognito
    val passportStatus by homeViewModel.passportStatus.collectAsState()

    val pointsToken by homeViewModel.pointsToken.collectAsState()

    val isAirDropClaimed by homeViewModel.isAirDropClaimed.collectAsState()

    val rarimoInfoSheetState = rememberAppSheetState()
    val specificAppSheetState = rememberAppSheetState()
    val verifyPassportSheetState = rememberAppSheetState()

    fun reloadUserDetails() = run {
        scope.launch {
            isLoading = true
            homeViewModel.loadUserDetails()
            isLoading = false
        }
    }

    Column(
        modifier = Modifier.padding(12.dp)
    ) {
        Spacer(modifier = Modifier.size(32.dp))

        HomeScreenHeader(walletAsset = rmoAsset) { navigate(Screen.Main.Wallet.route) }

        Spacer(modifier = Modifier.size(32.dp))

        Column(
            modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            PassportCard(
                passport = passport.value!!,
                isIncognito = isIncognito,
                look = passportCardLook,
                identifiers = passportIdentifiers,
                onLookChange = { homeViewModel.onPassportCardLookChange(it) },
                onIncognitoChange = { homeViewModel.onIncognitoChange(it) },
                passportStatus = passportStatus,
                onIdentifiersChange = { homeViewModel.onPassportIdentifiersChange(it) }
            )

            val isVerified = pointsToken?.balanceDetails?.attributes?.is_verified ?: false
            val isBalanceCreated = pointsToken?.balanceDetails?.attributes?.created_at != null

            if (!isVerified) {
                when (passportStatus) {
                    PassportStatus.ALLOWED -> {
                        ActionCard(
                            title = stringResource(R.string.reserve_tokens),
                            description = stringResource(
                                R.string.you_re_entitled_of_x_rmo,
                                Constants.AIRDROP_REWARD
                            ),
                            leadingContent = {
                                Image(
                                    modifier = Modifier.size(42.dp),
                                    painter = painterResource(id = R.drawable.reward_coin),
                                    contentDescription = "decor",
                                )
                            },
                            onClick = {
                                if (isBalanceCreated) {
                                    navigate(Screen.Claim.Reserve.route)
                                } else {
                                    verifyPassportSheetState.show()
                                }
                            }
                        )
                    }

                    PassportStatus.WAITLIST -> {
                        pointsToken?.balanceDetails?.let {} ?: ActionCard(
                            title = stringResource(id = R.string.join_waitlist_btn),
                            description = stringResource(id = R.string.joined_waitlist_description),
                            leadingContent = {
                                Image(
                                    modifier = Modifier.size(42.dp),
                                    painter = painterResource(id = R.drawable.reward_coin),
                                    contentDescription = "decor",
                                )
                            },
                            onClick = { verifyPassportSheetState.show() }
                        )
                    }

                    else -> {}
                }
            }

//            if (!isAirDropClaimed && passportStatus == PassportStatus.ALLOWED) {
//                ActionCard(title = stringResource(id = R.string.specific_citizens),
//                    description = stringResource(R.string.programmable_rewards),
//                    leadingContent = {
//                        Text(
//                            text = "🇺🇦",
//                            style = RarimeTheme.typography.h5,
//                            color = RarimeTheme.colors.textPrimary,
//                            textAlign = TextAlign.Center
//                        )
//                    },
//                    onClick = { specificAppSheetState.show() })
//            }

            ActionCard(
                title = stringResource(id = R.string.app_name),
                description = stringResource(R.string.learn_more_about_the_app),
                leadingContent = {
                    AppIcon(id = R.drawable.ic_info, size = 24.dp)
                },
                variant = ActionCardVariants.Outlined,
                onClick = {
                    rarimoInfoSheetState.show()
                }
            )
        }

        AppBottomSheet(state = rarimoInfoSheetState, fullScreen = true) { hide ->
            RarimeInfoScreen(onClose = { hide {} })
        }

        AppBottomSheet(
            state = verifyPassportSheetState,
            fullScreen = true,
            isHeaderEnabled = false
        ) { hide ->
            EnterProgramFlow(
                onFinish = {
                    when (passportStatus) {
                        PassportStatus.WAITLIST -> {
                            reloadUserDetails()
                        }
                        else -> {
                            navigate(Screen.Claim.Reserve.route)
                        }
                    }
                },
                sheetState = verifyPassportSheetState,
                hide = hide
            )
        }

//        AppBottomSheet(state = specificAppSheetState, fullScreen = true) { hide ->
//            AirdropIntroScreen(onStart = {
//                hide { navigate(Screen.Claim.Specific.route) }
//            })
//        }
    }
}

@Preview
@Composable
fun HomeScreenPassportMainContentPreview() {
    Column {
        HomeScreenPassportMainContent(
            navigate = {}, rmoAsset = WalletAsset(
                "", PreviewerToken(
                    "",
                    "Reserved RMO",
                    "RRMO",
                )
            )
        )
    }
}