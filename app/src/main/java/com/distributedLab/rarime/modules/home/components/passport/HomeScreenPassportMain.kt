package com.distributedLab.rarime.modules.home.components.passport

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.distributedLab.rarime.R
import com.distributedLab.rarime.data.enums.PassportStatus
import com.distributedLab.rarime.data.tokens.PreviewerToken
import com.distributedLab.rarime.modules.common.WalletAsset
import com.distributedLab.rarime.modules.home.LocalHomeViewModel
import com.distributedLab.rarime.modules.home.components.HomeScreenHeader
import com.distributedLab.rarime.modules.home.components.RarimeInfoScreen
import com.distributedLab.rarime.modules.home.components.no_passport.specific.AirdropIntroScreen
import com.distributedLab.rarime.ui.components.ActionCard
import com.distributedLab.rarime.ui.components.ActionCardVariants
import com.distributedLab.rarime.ui.components.AppBottomSheet
import com.distributedLab.rarime.ui.components.AppIcon
import com.distributedLab.rarime.ui.components.rememberAppSheetState
import com.distributedLab.rarime.ui.theme.RarimeTheme
import com.distributedLab.rarime.util.Constants
import com.distributedLab.rarime.util.Screen

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
    val homeViewModel = LocalHomeViewModel.current

    val passport = homeViewModel.passport

    val passportCardLook by homeViewModel.passportCardLook
    val passportIdentifiers by homeViewModel.passportIdentifiers
    val isIncognito by homeViewModel.isIncognito
    val passportStatus by homeViewModel.passportStatus.collectAsState()

    val isReserved by homeViewModel.isReserved.collectAsState()
    val isUkrClaimed by homeViewModel.isUkrClaimed.collectAsState()

    val rarimoInfoSheetState = rememberAppSheetState()
    val specificAppSheetState = rememberAppSheetState()

    Column(
        modifier = Modifier.padding(12.dp)
    ) {
        Spacer(modifier = Modifier.size(32.dp))

        HomeScreenHeader(walletAsset = rmoAsset) { navigate(Screen.Main.Wallet.route) }

        Spacer(modifier = Modifier.size(32.dp))

        Column(
            modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            PassportCard(passport = passport.value!!,
                isIncognito = isIncognito,
                look = passportCardLook,
                identifiers = passportIdentifiers,
                onLookChange = { homeViewModel.onPassportCardLookChange(it) },
                onIncognitoChange = { homeViewModel.onIncognitoChange(it) },
                passportStatus = passportStatus,
                onIdentifiersChange = { homeViewModel.onPassportIdentifiersChange(it) })
            if (!isReserved && passportStatus == PassportStatus.ALLOWED) {
                ActionCard(title = stringResource(R.string.reserve_tokens),
                    description = stringResource(
                        R.string.you_re_entitled_of_x_rmo, Constants.AIRDROP_REWARD
                    ),
                    leadingContent = {
                        Image(
                            modifier = Modifier.size(42.dp),
                            painter = painterResource(id = R.drawable.reward_coin),
                            contentDescription = "decor",
                        )
                    },
                    onClick = { navigate(Screen.Claim.Reserve.route) })
            }

            if (!isUkrClaimed && passportStatus == PassportStatus.ALLOWED) {
                ActionCard(title = stringResource(id = R.string.ukrainian_citizens),
                    description = stringResource(R.string.programmable_rewards),
                    leadingContent = {
                        Text(
                            text = "🇺🇦",
                            style = RarimeTheme.typography.h5,
                            color = RarimeTheme.colors.textPrimary,
                            textAlign = TextAlign.Center
                        )
                    },
                    onClick = { specificAppSheetState.show() })
            }



            ActionCard(title = stringResource(id = R.string.app_name),
                description = stringResource(R.string.learn_more_about_the_app),
                leadingContent = {
                    AppIcon(id = R.drawable.ic_info, size = 24.dp)
                },
                variant = ActionCardVariants.Outlined,
                onClick = {
                    rarimoInfoSheetState.show()
                })


        }


        AppBottomSheet(state = rarimoInfoSheetState, fullScreen = true) { hide ->
            RarimeInfoScreen(onClose = { hide {} })
        }

        AppBottomSheet(state = specificAppSheetState, fullScreen = true) { hide ->
            AirdropIntroScreen(onStart = {
                hide { navigate(Screen.Claim.Ukr.route) }
            })
        }
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