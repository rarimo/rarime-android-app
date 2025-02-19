package com.rarilabs.rarime.modules.home.components.passport

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import com.rarilabs.rarime.services.NotificationService
import com.rarilabs.rarime.ui.components.ActionCard
import com.rarilabs.rarime.ui.components.ActionCardVariants
import com.rarilabs.rarime.ui.components.AppBottomSheet
import com.rarilabs.rarime.ui.components.AppIcon
import com.rarilabs.rarime.ui.components.enter_program.EnterProgramFlow
import com.rarilabs.rarime.ui.components.rememberAppSheetState
import com.rarilabs.rarime.ui.theme.RarimeTheme
import com.rarilabs.rarime.util.Constants
import com.rarilabs.rarime.util.ErrorHandler
import com.rarilabs.rarime.util.Screen
import kotlinx.coroutines.launch

@Composable
fun HomeScreenPassportMain(
    navigate: (String) -> Unit
) {
    val homeViewModel = LocalHomeViewModel.current

    val selectedWalletAsset = homeViewModel.selectedWalletAsset.collectAsState()

    selectedWalletAsset.value?.let {
        HomeScreenPassportMainContent(navigate, it)
    }
}

@Composable
fun HomeScreenPassportMainContent(
    navigate: (String) -> Unit, selectedWalletAsset: WalletAsset
) {
    val scope = rememberCoroutineScope()

    val homeViewModel = LocalHomeViewModel.current

    var isLoading by remember { mutableStateOf(false) }

    val passport = homeViewModel.passport.collectAsState()

    val passportCardLook by homeViewModel.passportCardLook
    val passportIdentifiers by homeViewModel.passportIdentifiers
    val isIncognito by homeViewModel.isIncognito
    val passportStatus by homeViewModel.passportStatus.collectAsState()

    val pointsToken by homeViewModel.pointsToken.collectAsState()


    val rarimoInfoSheetState = rememberAppSheetState()
    val verifyPassportSheetState = rememberAppSheetState()

    fun reloadUserDetails() = run {
        scope.launch {
            isLoading = true
            homeViewModel.loadNotifications()
            homeViewModel.loadUserDetails()
            isLoading = false
        }
    }

    LaunchedEffect(Unit) {
        try {
            NotificationService.subscribeToRewardableTopic()
        } catch (e: Exception) {
            ErrorHandler.logError("HomeScreenPassportMain", "error sub to rewardable topic", e)
        }
    }

    Column(
        modifier = Modifier
            .padding(vertical = 20.dp, horizontal = 12.dp)
    ) {
        HomeScreenHeader(
            walletAsset = selectedWalletAsset,
            navigate = navigate,
        ) {
            pointsToken?.balanceDetails?.let {
                navigate(Screen.Main.Rewards.route)
            }
        }

        Spacer(modifier = Modifier.size(24.dp))

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            passport.value?.let {
                PassportCard(
                    passport = it,
                    isIncognito = isIncognito,
                    look = passportCardLook,
                    identifiers = passportIdentifiers,
                    onLookChange = { homeViewModel.onPassportCardLookChange(it) },
                    onIncognitoChange = { homeViewModel.onIncognitoChange(it) },
                    passportStatus = passportStatus,
                    onIdentifiersChange = { homeViewModel.onPassportIdentifiersChange(it) }
                )
            }

            if (pointsToken?.balanceDetails?.attributes?.is_verified == null || pointsToken?.balanceDetails?.attributes?.is_verified == false) {
                when (passportStatus) {
                    PassportStatus.ALLOWED -> {
                        if (!homeViewModel.getIsAlreadyReserved()) {
                            ActionCard(
                                title = stringResource(R.string.reserve_tokens),
                                description = stringResource(
                                    R.string.you_re_entitled_of_x_rmo,
                                    Constants.SCAN_PASSPORT_REWARD.toInt()
                                ),
                                leadingContent = {
                                    Image(
                                        modifier = Modifier.size(42.dp),
                                        painter = painterResource(id = R.drawable.reward_coin),
                                        contentDescription = "decor",
                                    )
                                },
                                onClick = {
                                    if (pointsToken?.balanceDetails?.attributes?.created_at != null) {
                                        navigate(Screen.Claim.Reserve.route)
                                    } else {
                                        verifyPassportSheetState.show()
                                    }
                                }
                            )
                        }
                    }

                    PassportStatus.WAITLIST -> {}

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
                    AppIcon(
                        id = R.drawable.ic_info,
                        size = 24.dp,
                        tint = RarimeTheme.colors.textPrimary
                    )
                },
                variant = ActionCardVariants.Outlined,
                onClick = {
                    rarimoInfoSheetState.show()
                }
            )

            Spacer(modifier = Modifier.weight(1f))
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
            navigate = {}, selectedWalletAsset = WalletAsset(
                "", PreviewerToken(
                    "",
                    "Reserved RMO",
                    "RRMO",
                )
            )
        )
    }
}