package com.rarilabs.rarime.modules.hiddenPrize

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.rarilabs.rarime.R
import com.rarilabs.rarime.ui.base.BaseButton
import com.rarilabs.rarime.ui.base.ButtonSize
import com.rarilabs.rarime.ui.theme.RarimeTheme
import nl.dionsegijn.konfetti.compose.KonfettiView
import nl.dionsegijn.konfetti.core.Party
import nl.dionsegijn.konfetti.core.Position
import nl.dionsegijn.konfetti.core.emitter.Emitter
import java.util.concurrent.TimeUnit

@Composable
fun HiddenPrizeFinish(
    modifier: Modifier = Modifier,
    prizeAmount: String,
    prizeSymbol: @Composable () -> Unit = {},
    onViewWallet: () -> Unit,
    onShareWallet: () -> Unit
) {
    Box(Modifier.fillMaxSize()) {
        Column(
            modifier = modifier
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(), contentAlignment = Alignment.Center
            ) {
                KonfettiView(
                    modifier = Modifier.fillMaxSize(), parties = listOf(
                        Party(
                            speed = 0f,
                            maxSpeed = 30f,
                            damping = 0.9f,
                            spread = 360,
                            colors = listOf(
                                Color(0xB4AEA2E2).toArgb(),
                                Color(0xF1EDD9FF).toArgb(),
                            ),
                            position = Position.Relative(0.5, 0.3),
                            emitter = Emitter(duration = 100, TimeUnit.MILLISECONDS).max(100)
                        )
                    )
                )
                Column(
                    modifier = Modifier.width(230.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_success_color),
                        contentDescription = null,
                        tint = RarimeTheme.colors.baseWhite,
                    )
                    Spacer(Modifier.height(32.dp))
                    Text(
                        stringResource(R.string.hidden_prize_finish_screen_title),
                        color = RarimeTheme.colors.baseWhite,
                        textAlign = TextAlign.Center,
                        style = RarimeTheme.typography.h3
                    )
                    Spacer(Modifier.height(12.dp))
                    Text(
                        stringResource(R.string.hidden_prize_finish_screen_description),
                        color = RarimeTheme.colors.baseWhite.copy(alpha = 0.6f),
                        textAlign = TextAlign.Center,
                    )

                }
            }
        }

        Column(
            modifier = Modifier.align(Alignment.BottomCenter),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            BaseButton(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                size = ButtonSize.Large,
                colors = ButtonDefaults.buttonColors(
                    containerColor = RarimeTheme.colors.baseWhite.copy(0.1f),
                    contentColor = RarimeTheme.colors.invertedLight,
                    disabledContainerColor = RarimeTheme.colors.componentDisabled,
                    disabledContentColor = RarimeTheme.colors.textDisabled
                ),
                onClick = onViewWallet
            ) {
                Text(
                    stringResource(R.string.hidden_prize_success_screen_wallet_btn),
                    color = RarimeTheme.colors.baseWhite
                )
            }
            BaseButton(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .padding(bottom = 16.dp),
                size = ButtonSize.Large,
                colors = ButtonDefaults.buttonColors(
                    containerColor = RarimeTheme.colors.baseWhite,
                    contentColor = RarimeTheme.colors.baseBlack,
                    disabledContainerColor = RarimeTheme.colors.componentDisabled,
                    disabledContentColor = RarimeTheme.colors.textDisabled
                ),
                onClick = onShareWallet
            ) {
                Text(
                    stringResource(R.string.hidden_prize_success_share_btn),
                    color = RarimeTheme.colors.baseBlack,
                )
            }
        }
    }
}


@Composable
@Preview
fun HiddenPrizeFinishPreview() {
    Box(Modifier.fillMaxSize()) {
        // Image for blur example
        Image(
            painter = painterResource(R.drawable.drawable_digital_likeness),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxSize()
                .blur(20.dp)
        )

        HiddenPrizeFinish(prizeAmount = stringResource(R.string.hidden_prize_prize_pool_value), onViewWallet = {}, prizeSymbol = {
            Image(painterResource(R.drawable.ic_ethereum), contentDescription = "ETH")
        }, onShareWallet = {})
    }
}