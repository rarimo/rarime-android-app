package com.rarilabs.rarime.modules.hiddenPrize

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.rarilabs.rarime.R
import com.rarilabs.rarime.data.enums.AppColorScheme
import com.rarilabs.rarime.ui.base.BaseButton
import com.rarilabs.rarime.ui.base.ButtonSize
import com.rarilabs.rarime.ui.components.PrimaryButton
import com.rarilabs.rarime.ui.theme.RarimeTheme
import kotlinx.coroutines.launch
import nl.dionsegijn.konfetti.compose.KonfettiView
import nl.dionsegijn.konfetti.core.Party
import nl.dionsegijn.konfetti.core.Position
import nl.dionsegijn.konfetti.core.emitter.Emitter
import java.util.concurrent.TimeUnit

enum class HiddenPrizeCongratsScreenState {
    CLAIM, DOWNLOAD, FINISH
}

@Composable
fun HiddenPrizeCongratsScreen(
    modifier: Modifier = Modifier,
    prizeAmount: String,
    prizeSymbol: @Composable () -> Unit = {},
    onClaim: suspend () -> Unit,
    imageLink: String,
    colorScheme: AppColorScheme,
    downloadProgress: Int,
    onViewWallet: () -> Unit,
    onShare: () -> Unit,

    ) {
    val isDark = when (colorScheme) {
        AppColorScheme.SYSTEM -> isSystemInDarkTheme()
        AppColorScheme.DARK -> true
        AppColorScheme.LIGHT -> false
    }
    val bgDark = RarimeTheme.colors.baseBlack
    val bgLight = RarimeTheme.colors.baseWhite
    val bgColor = remember(isDark) {
        if (isDark) bgDark
        else bgLight
    }

    val backgroundRes = remember(isDark) {
        if (isDark) R.drawable.ic_bg_winner_screen_dark
        else R.drawable.ic_bg_winner_screen_light
    }
    var currentState by remember { mutableStateOf(HiddenPrizeCongratsScreenState.CLAIM) }
    val coroutineScope = rememberCoroutineScope()

    Box(
        Modifier
            .fillMaxSize()
            .background(bgColor)
    ) {

        Image(
            painter = painterResource(backgroundRes),
            contentDescription = null,
            contentScale = ContentScale.FillWidth,
            modifier = Modifier.fillMaxWidth()

        )

    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 90.dp),

        ) {
        KonfettiView(
            modifier = Modifier.fillMaxSize(), parties = listOf(
                Party(
                    speed = 0f, maxSpeed = 30f, damping = 0.9f, spread = 360, colors = listOf(
                        Color(0xB4AEA2E2).toArgb(),
                        Color(0xF1EDD9FF).toArgb(),
                    ), position = Position.Relative(0.5, 0.3), emitter = Emitter(
                        duration = 100, TimeUnit.MILLISECONDS
                    ).max(100)
                )
            )
        )

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            AsyncImage(
                model = imageLink, contentDescription = null, modifier = Modifier.size(
                    width = 118.dp, height = 135.dp
                )
            )
            Spacer(modifier = Modifier.size(62.dp))
            Text(
                stringResource(R.string.hidden_prize_success_screen_title),
                color = RarimeTheme.colors.textPrimary,
                textAlign = TextAlign.Center,
                style = RarimeTheme.typography.h3
            )
            Spacer(Modifier.height(12.dp))
            Text(
                stringResource(R.string.hidden_prize_success_screen_description),
                color = RarimeTheme.colors.textSecondary,
                textAlign = TextAlign.Center,
            )
            Spacer(Modifier.height(32.dp))

            Card(
                modifier = Modifier
                    .shadow(
                        elevation = 60.dp,
                        shape = RoundedCornerShape(20.dp),
                        ambientColor = Color(0xFF9D4EDD),
                        spotColor = Color(0xFF9D4EDD)
                    )
                    .padding(vertical = 20.dp, horizontal = 23.dp),
                shape = RoundedCornerShape(20.dp),
                elevation = CardDefaults.cardElevation(0.dp),
                colors = CardDefaults.cardColors(
                    containerColor = bgColor,
                )

            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.background(bgColor, shape = RoundedCornerShape(20.dp))

                ) {
                    Text(
                        stringResource(R.string.hidden_prize_success_screen_prize),
                        color = RarimeTheme.colors.textSecondary,
                        modifier = Modifier.padding(top = 20.dp)
                    )
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text(
                            prizeAmount.toString().format(),
                            style = RarimeTheme.typography.h3,
                            color = RarimeTheme.colors.textPrimary
                        )
                        prizeSymbol()
                    }
                    LoadingButton(
                        progress = downloadProgress, onClick = {
                            coroutineScope.launch {
                                currentState = HiddenPrizeCongratsScreenState.DOWNLOAD
                                onClaim()
                                currentState = HiddenPrizeCongratsScreenState.FINISH
                            }
                        }, currentState = currentState
                    )

                }


            }

        }
        if (currentState == HiddenPrizeCongratsScreenState.FINISH) {
            Column(modifier = Modifier.align(Alignment.BottomCenter)) {
                BaseButton(
                    onClick = onViewWallet,
                    text = "View wallet",
                    size = ButtonSize.Large,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp),
                    colors = ButtonDefaults.buttonColors(
                        contentColor = RarimeTheme.colors.textPrimary,
                        containerColor = bgColor,
                    ),
                    enabled = true
                )
                Spacer(
                    modifier = Modifier.size(8.dp)
                )
                PrimaryButton(
                    onClick = onShare,
                    text = "Share",
                    size = ButtonSize.Large,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp),
                    enabled = true
                )
            }
        }
    }
}


@Composable
private fun LoadingButton(
    progress: Int, onClick: () -> Unit, currentState: HiddenPrizeCongratsScreenState
) {
    when (currentState) {
        HiddenPrizeCongratsScreenState.CLAIM -> BaseButton(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .padding(bottom = 16.dp),
            size = ButtonSize.Large,
            colors = ButtonDefaults.buttonColors(
                containerColor = RarimeTheme.colors.baseBlack,
                contentColor = RarimeTheme.colors.baseWhite,
                disabledContainerColor = RarimeTheme.colors.componentDisabled,
                disabledContentColor = RarimeTheme.colors.textDisabled
            ),
            onClick = {
                onClick()
            },
            enabled = true

        ) {
            Text(
                text = stringResource(R.string.hidden_prize_congrats_share_btn),
                color = RarimeTheme.colors.baseWhite,
            )
        }

        HiddenPrizeCongratsScreenState.DOWNLOAD -> BaseButton(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .padding(bottom = 16.dp),
            size = ButtonSize.Large,
            colors = ButtonDefaults.buttonColors(
                containerColor = RarimeTheme.colors.baseBlack,
                contentColor = RarimeTheme.colors.baseWhite,
                disabledContainerColor = RarimeTheme.colors.componentDisabled,
                disabledContentColor = RarimeTheme.colors.textDisabled
            ),
            onClick = {
                onClick()
            },
            enabled = false

        ) {
            if (progress != 100) {
                Text(
                    text = "Claiming ($progress%)", color = RarimeTheme.colors.textDisabled
                )
            } else {
                Text(
                    text = stringResource(R.string.ic_hidden_prize_congats_screen_button_label_while_creating_ZK),
                    color = RarimeTheme.colors.textDisabled
                )
            }
        }


        HiddenPrizeCongratsScreenState.FINISH -> BaseButton(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .padding(bottom = 16.dp),
            size = ButtonSize.Large,
            colors = ButtonDefaults.buttonColors(
                disabledContainerColor = RarimeTheme.colors.successLighter,
                disabledContentColor = RarimeTheme.colors.successDark
            ),
            onClick = {
                onClick()
            },
            enabled = false,
            leftIcon = R.drawable.ic_check_line


        ) {
            Text(
                stringResource(R.string.hidden_prize_congrats_share_btn_final),
                color = RarimeTheme.colors.successDark,
            )
        }

    }


}


@Composable
@Preview
fun HiddenPrizeCongratsScreenPreview() {
    Box(Modifier.fillMaxSize()) {
        HiddenPrizeCongratsScreen(
            prizeAmount = stringResource(R.string.hidden_prize_prize_pool_value),
            onClaim = {},
            prizeSymbol = {
                Image(painterResource(R.drawable.ic_ethereum), contentDescription = "ETH")
            },
            imageLink = "",
            colorScheme = AppColorScheme.SYSTEM,
            downloadProgress = 100,
            onShare = {},
            onViewWallet = {})
    }
}