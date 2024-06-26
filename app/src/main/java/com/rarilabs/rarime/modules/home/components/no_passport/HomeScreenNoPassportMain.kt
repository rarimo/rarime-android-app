package com.rarilabs.rarime.modules.home.components.no_passport

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.rarilabs.rarime.R
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
import com.rarilabs.rarime.ui.components.enter_program.UNSPECIFIED_PASSPORT_STEPS
import com.rarilabs.rarime.ui.components.rememberAppSheetState
import com.rarilabs.rarime.ui.theme.RarimeTheme
import com.rarilabs.rarime.util.Screen

@Composable
fun HomeScreenNoPassportMain(
    navigate: (String) -> Unit,
) {
    val homeViewModel = LocalHomeViewModel.current

    val selectedWalletAsset = homeViewModel.selectedWalletAsset.collectAsState()

    selectedWalletAsset.value?.let {
        HomeScreenNoPassportMainContent(navigate, it)
    }
}

@Composable
fun HomeScreenNoPassportMainContent(
    navigate: (String) -> Unit, selectedWalletAsset: WalletAsset
) {
    val homeViewModel = LocalHomeViewModel.current

    val pointsToken by homeViewModel.pointsToken.collectAsState()

    val rarimoInfoSheetState = rememberAppSheetState()

    val nonSpecificAppSheetState = rememberAppSheetState()

    val specificAppSheetState = rememberAppSheetState()

    Column(
        modifier = Modifier
            .padding(vertical = 20.dp, horizontal = 12.dp)
    ) {
        HomeScreenHeader(walletAsset = selectedWalletAsset) {
            pointsToken?.balanceDetails?.let {
                navigate(Screen.Main.Rewards.route)
            }
        }

        Spacer(modifier = Modifier.size(32.dp))

        Column(
            modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            GreetCommonActionCard(mediaContent = {
                Image(
                    modifier = Modifier.size(110.dp),
                    painter = painterResource(id = R.drawable.reward_coin),
                    contentDescription = "decor",
                )
            },
                title = stringResource(id = R.string.other_passport_card_title),
                subtitle = stringResource(id = R.string.other_passport_card_description),
                btnText = stringResource(id = R.string.greet_common_action_card_btn_text),
                onClick = { nonSpecificAppSheetState.show() })

//            ActionCard(title = stringResource(id = R.string.specific_citizens),
//                description = stringResource(R.string.programmable_rewards),
//                leadingContent = {
//                    Text(
//                        text = "🇺🇦",
//                        style = RarimeTheme.typography.h5,
//                        color = RarimeTheme.colors.textPrimary,
//                        textAlign = TextAlign.Center
//                    )
//                },
//                onClick = { specificAppSheetState.show() })

            ActionCard(title = stringResource(id = R.string.app_name),
                description = stringResource(R.string.learn_more_about_the_app),
                leadingContent = {
                    AppIcon(id = R.drawable.ic_info, size = 24.dp, tint = RarimeTheme.colors.textPrimary)
                },
                variant = ActionCardVariants.Outlined,
                onClick = {
                    rarimoInfoSheetState.show()
                })

            AppBottomSheet(state = rarimoInfoSheetState, fullScreen = true) { hide ->
                RarimeInfoScreen(onClose = { hide {} })
            }

            AppBottomSheet(
                state = nonSpecificAppSheetState,
                fullScreen = true,
                isHeaderEnabled = false,
            ) { hide ->
                EnterProgramFlow(
                    onFinish = { navigate(Screen.ScanPassport.ScanPassportPoints.route) },
                    sheetState = nonSpecificAppSheetState,
                    hide = hide,
                    initialStep = pointsToken?.balanceDetails?.let { UNSPECIFIED_PASSPORT_STEPS.POLICY_CONFIRMATION } ?: UNSPECIFIED_PASSPORT_STEPS.INVITATION
                )
            }

//            AppBottomSheet(state = specificAppSheetState, fullScreen = true) { hide ->
//                AirdropIntroScreen(onStart = {
//                    hide { navigate(Screen.ScanPassport.ScanPassportSpecific.route) }
//                })
//            }
        }
    }
}

@Preview
@Composable
fun HomeScreenNoPassportMainContentPreview() {
    Column(
        modifier = Modifier.background(RarimeTheme.colors.backgroundPrimary)
    ) {
        HomeScreenNoPassportMainContent(
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