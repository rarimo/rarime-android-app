package com.rarilabs.rarime.modules.hiddenPrize

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.rarilabs.rarime.R
import com.rarilabs.rarime.ui.base.BaseButton
import com.rarilabs.rarime.ui.base.BaseIconButton
import com.rarilabs.rarime.ui.base.ButtonSize
import com.rarilabs.rarime.ui.theme.RarimeTheme

@Composable
fun HiddenPrizeWrongScreen(
    modifier: Modifier = Modifier,
    attemptsLeft: Int = 0,
    tip: String? = null,
    onRetry: () -> Unit = {},
    onClose: () -> Unit
) {
    val canRetry = attemptsLeft > 0
    val description = buildString {
        append(stringResource(R.string.hidden_prize_wrong_screen_description_1))
        if (canRetry) append(stringResource(R.string.hidden_prize_wrong_screen_description_2))
    }
    Box(Modifier.fillMaxSize()) {
        BaseIconButton(
            onClick = onClose,
            icon = R.drawable.ic_close_fill,
            colors = ButtonDefaults.buttonColors(containerColor = RarimeTheme.colors.componentPrimary,
                contentColor = RarimeTheme.colors.baseWhite),
            modifier = Modifier
                .padding(20.dp)
                .align(Alignment.TopEnd)
                .size(40.dp)
        )
        Column(
            modifier = modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(), contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_wrong_face),
                        contentDescription = null,
                        tint = RarimeTheme.colors.baseWhite,
                    )
                    Spacer(Modifier.height(32.dp))
                    Text(
                        stringResource(R.string.hidden_prize_wrong_screen_title),
                        color = RarimeTheme.colors.baseWhite,
                        textAlign = TextAlign.Center,
                        style = RarimeTheme.typography.h3
                    )
                    Spacer(Modifier.height(12.dp))
                    Text(
                        description,
                        color = RarimeTheme.colors.baseWhite.copy(alpha = 0.6f),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.width(230.dp),
                    )
                }
            }

            tip?.let {
                Text(
                    it,
                    color = RarimeTheme.colors.baseWhite.copy(alpha = 0.6f),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )
            }

            if (canRetry) {
                BaseButton(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    size = ButtonSize.Large,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = RarimeTheme.colors.baseWhite.copy(0.1f),
                        contentColor = RarimeTheme.colors.invertedLight,
                        disabledContainerColor = RarimeTheme.colors.componentDisabled,
                        disabledContentColor = RarimeTheme.colors.textDisabled
                    ),
                    onClick = onRetry
                ) {
                    Text(
                        stringResource(R.string.hidden_prize_wrong_screen_rescan_btn),
                        color = RarimeTheme.colors.baseWhite
                    )
                }
            } else {
                BaseButton(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    size = ButtonSize.Large,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = RarimeTheme.colors.baseWhite.copy(0.1f),
                        contentColor = RarimeTheme.colors.invertedLight,
                        disabledContainerColor = RarimeTheme.colors.componentDisabled,
                        disabledContentColor = RarimeTheme.colors.textDisabled
                    ),
                    onClick = onClose
                ) {
                    Text(
                        stringResource(R.string.hidden_prize_wrong_screen_back_home),
                        color = RarimeTheme.colors.baseWhite
                    )
                }
            }
        }
    }
}


@Composable
@Preview(showBackground = true)
fun WrongScreenPreview_WithBlur() {
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

        HiddenPrizeWrongScreen(
            attemptsLeft = 2,
            tip = "Tip: I think there's something as light as ether in that face...",
            onClose = {})
    }
}