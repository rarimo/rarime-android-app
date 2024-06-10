package com.distributedLab.rarime.modules.home.components.no_passport

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.distributedLab.rarime.R
import com.distributedLab.rarime.data.tokens.PreviewerToken
import com.distributedLab.rarime.modules.common.WalletAsset
import com.distributedLab.rarime.modules.home.LocalHomeViewModel
import com.distributedLab.rarime.modules.home.components.HomeScreenHeader
import com.distributedLab.rarime.modules.home.components.RarimeInfoScreen
import com.distributedLab.rarime.modules.home.components.no_passport.non_specific.AboutProgram
import com.distributedLab.rarime.modules.home.components.no_passport.non_specific.Invitation
import com.distributedLab.rarime.modules.home.components.no_passport.non_specific.PolicyConfirmation
import com.distributedLab.rarime.modules.home.components.no_passport.specific.AirdropIntroScreen
import com.distributedLab.rarime.ui.components.ActionCard
import com.distributedLab.rarime.ui.components.ActionCardVariants
import com.distributedLab.rarime.ui.components.AppBottomSheet
import com.distributedLab.rarime.ui.components.AppIcon
import com.distributedLab.rarime.ui.components.PrimaryTextButton
import com.distributedLab.rarime.ui.components.rememberAppSheetState
import com.distributedLab.rarime.ui.theme.RarimeTheme
import com.distributedLab.rarime.util.Screen

enum class UNSPECIFIED_PASSPORT_STEPS(val value: Int) {
    INVITATION(1),
    POLICY_CONFIRMATION(3),

    ABOUT_PROGRAM(5),
}

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
                title = stringResource(id = R.string.other_passport_card_title),
                subtitle = stringResource(id = R.string.other_passport_card_description),
                btnText = stringResource(id = R.string.greet_Common_action_card_btn_text),
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

            AppBottomSheet(
                state = nonSpecificAppSheetState,
                fullScreen = true,
                isHeaderEnabled = false,
            ) { hide ->
                var currStep by remember {
                    mutableStateOf(UNSPECIFIED_PASSPORT_STEPS.INVITATION)
                }

                AnimatedVisibility(
                    visible = currStep.equals(UNSPECIFIED_PASSPORT_STEPS.INVITATION),
                    enter = fadeIn() + slideInHorizontally(initialOffsetX = { it }),
                    exit = fadeOut() + slideOutHorizontally(targetOffsetX = { -it })
                ) {
                    Column (
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row (
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            horizontalArrangement = Arrangement.End,
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            PrimaryTextButton(
                                leftIcon = R.drawable.ic_close,
                                onClick = { nonSpecificAppSheetState.hide() }
                            )
                        }

                        Column (
                            modifier = Modifier.weight(1f)
                        ) {
                            Invitation(
                                onNext = { currStep = UNSPECIFIED_PASSPORT_STEPS.POLICY_CONFIRMATION },
                                updateStep = { currStep = it }
                            )
                        }
                    }
                }

                AnimatedVisibility(
                    visible = currStep.equals(UNSPECIFIED_PASSPORT_STEPS.POLICY_CONFIRMATION),
                    enter = fadeIn() + slideInHorizontally(initialOffsetX = { it }),
                    exit = fadeOut() + slideOutHorizontally(targetOffsetX = { -it })
                ) {
                    Column (
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row (
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            horizontalArrangement = Arrangement.End,
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            PrimaryTextButton(
                                leftIcon = R.drawable.ic_close,
                                onClick = { nonSpecificAppSheetState.hide() }
                            )
                        }
                        PolicyConfirmation(
                            onNext = {
                                hide({ navigate(Screen.ScanPassport.route) })
                            }
                        )
                    }
                }

                AnimatedVisibility(
                    visible = currStep.equals(UNSPECIFIED_PASSPORT_STEPS.ABOUT_PROGRAM),
                    enter = fadeIn() + slideInHorizontally(initialOffsetX = { it }),
                    exit = fadeOut() + slideOutHorizontally(targetOffsetX = { -it })
                ) {
                    Column {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            PrimaryTextButton(
                                leftIcon = R.drawable.ic_arrow_left,
                                onClick = { currStep = UNSPECIFIED_PASSPORT_STEPS.INVITATION }
                            )

                            Text(
                                text = "About the program",
                                style = RarimeTheme.typography.subtitle4,
                                color = RarimeTheme.colors.textPrimary,
                            )

                            PrimaryTextButton(
                                leftIcon = R.drawable.ic_close,
                                onClick = { nonSpecificAppSheetState.hide() }
                            )
                        }
                        AboutProgram(
                            modifier = Modifier.weight(1f)
                        )
                    }
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