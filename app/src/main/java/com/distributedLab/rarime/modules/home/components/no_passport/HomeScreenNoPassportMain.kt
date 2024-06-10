package com.distributedLab.rarime.modules.home.components.no_passport

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOut
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.distributedLab.rarime.R
import com.distributedLab.rarime.data.tokens.PreviewerToken
import com.distributedLab.rarime.modules.common.WalletAsset
import com.distributedLab.rarime.modules.home.LocalHomeViewModel
import com.distributedLab.rarime.modules.home.components.HomeScreenHeader
import com.distributedLab.rarime.modules.home.components.RarimeInfoScreen
import com.distributedLab.rarime.modules.home.components.no_passport.non_specific.Invitation
import com.distributedLab.rarime.modules.home.components.no_passport.non_specific.OtherPassportIntroScreen
import com.distributedLab.rarime.modules.home.components.no_passport.non_specific.PolicyConfirmation
import com.distributedLab.rarime.modules.home.components.no_passport.specific.AirdropIntroScreen
import com.distributedLab.rarime.ui.components.ActionCard
import com.distributedLab.rarime.ui.components.ActionCardVariants
import com.distributedLab.rarime.ui.components.AppBottomSheet
import com.distributedLab.rarime.ui.components.AppIcon
import com.distributedLab.rarime.ui.components.rememberAppSheetState
import com.distributedLab.rarime.ui.theme.RarimeTheme
import com.distributedLab.rarime.util.Screen

@Composable
fun HomeScreenNoPassportMain(
    navigate: (String) -> Unit,
) {
    val homeViewModel = LocalHomeViewModel.current

    val rmoAsset = homeViewModel.rmoAsset.collectAsState()

    rmoAsset.value?.let {
        HomeScreenNoPassportMainContent(navigate, it)
    }
}

@Composable
fun HomeScreenNoPassportMainContent(
    navigate: (String) -> Unit,
    rmoAsset: WalletAsset
) {
    val rarimoInfoSheetState = rememberAppSheetState()

    val nonSpecificAppSheetState = rememberAppSheetState()

    val specificAppSheetState = rememberAppSheetState()

    Column(
        modifier = Modifier
            .padding(12.dp)
    ) {
        Spacer(modifier = Modifier.size(32.dp))

        HomeScreenHeader(walletAsset = rmoAsset) { navigate(Screen.Main.Wallet.route) }

        Spacer(modifier = Modifier.size(32.dp))

        Column(
            modifier = Modifier
                .weight(1f),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            GreetCommonActionCard(
                mediaContent = {
                    Image(
                        modifier = Modifier.size(110.dp),
                        painter = painterResource(id = R.drawable.reward_coin),
                        contentDescription = "decor",
                    )
                },
                title = "Join Rewards Program",
                subtitle = "Check your eligibility",
                btnText = "Let's start",
                onClick = { nonSpecificAppSheetState.show() }
            )

            ActionCard(
                title = "Ukrainian Citizens",
                description = "Programmable rewards",
                leadingContent = {
                    Text(
                        text = "🇺🇦",
                        style = RarimeTheme.typography.h5,
                        color = RarimeTheme.colors.textPrimary,
                        textAlign = TextAlign.Center
                    )
                },
                onClick = { specificAppSheetState.show() }
            )

            ActionCard(
                title = "RARIME",
                description = "Learn more about the App",
                leadingContent = {
                    AppIcon(id = R.drawable.ic_info, size = 24.dp)
                },
                variant = ActionCardVariants.Outlined,
                onClick = {
                    rarimoInfoSheetState.show()
                }
            )

            AppBottomSheet(state = rarimoInfoSheetState, fullScreen = true) { hide ->
                RarimeInfoScreen(onClose = { hide {} })
            }

            AppBottomSheet(state = nonSpecificAppSheetState, fullScreen = true) { hide ->
                var currStep by remember {
                    mutableStateOf(0)
                }

                AnimatedVisibility(
                    visible = currStep.equals(0),
                    enter = fadeIn() + slideInHorizontally(initialOffsetX = { it }),
                    exit = fadeOut() + slideOutHorizontally(targetOffsetX = { -it })
                ) {
                    Invitation(onNext = {
                        currStep = 1
                    })
                }

                AnimatedVisibility(
                    visible = currStep.equals(1),
                    enter = fadeIn() + slideInHorizontally(initialOffsetX = { it }),
                    exit = fadeOut() + slideOutHorizontally(targetOffsetX = { -it })
                ) {
                    OtherPassportIntroScreen(onStart = {
                        currStep = 2
                    })
                }

                AnimatedVisibility(
                    visible = currStep.equals(2),
                    enter = fadeIn() + slideInHorizontally(initialOffsetX = { it }),
                    exit = fadeOut() + slideOutHorizontally(targetOffsetX = { -it })
                ) {
                    PolicyConfirmation(
                        onNext = {
                            hide({ navigate(Screen.ScanPassport.route) })
                        }
                    )
                }
            }

            AppBottomSheet(state = specificAppSheetState, fullScreen = true) { hide ->
                AirdropIntroScreen(onStart = {
                    hide({ navigate(Screen.ScanPassport.route) })
                })
            }
        }
    }
}

@Preview
@Composable
fun HomeScreenNoPassportMainContentPreview() {

    Column(
        modifier = Modifier
            .background(RarimeTheme.colors.backgroundPrimary)
    ) {
        HomeScreenNoPassportMainContent(
            navigate = {},
            rmoAsset = WalletAsset(
                "",
                PreviewerToken(
                    "",
                    "Reserved RMO",
                    "RRMO",
                )
            )
        )
    }
}