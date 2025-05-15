package com.rarilabs.rarime.modules.hiddenPrize

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.rarilabs.rarime.R
import com.rarilabs.rarime.ui.theme.RarimeTheme

@Composable
fun HiddenPrizeLoadingML(
    modifier: Modifier = Modifier,
    processingValue: Float,
    processing: suspend () -> Unit
) {

    LaunchedEffect(Unit) {
        processing()
    }


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
                Column(
                    modifier = Modifier.width(230.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_dots_three_outline_rounded),
                        contentDescription = null,
                        tint = RarimeTheme.colors.baseWhite,
                    )
                    Spacer(Modifier.height(32.dp))
                    Text(
                        stringResource(R.string.hidden_prize_loading_ml_title),
                        color = RarimeTheme.colors.baseWhite,
                        textAlign = TextAlign.Center,
                        style = RarimeTheme.typography.h3
                    )
                    Spacer(Modifier.height(12.dp))
                    Text(
                        stringResource(R.string.hidden_prize_loading_ml_description),
                        color = RarimeTheme.colors.baseWhite.copy(alpha = 0.6f),
                        style = RarimeTheme.typography.body3,
                        textAlign = TextAlign.Center,
                    )

                    Spacer(modifier = Modifier.height(52.dp))

                    LinearProgressIndicator(
                        progress = { processingValue },
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    Text(
                        text = "${processingValue * 100f}%",
                        color = RarimeTheme.colors.baseWhite.copy(alpha = 0.6f),
                        textAlign = TextAlign.Center,
                        style = RarimeTheme.typography.body3,

                        )
                }
            }
        }
    }
}


@Preview
@Composable
private fun HiddenPrizeLoadingMLPreview() {
    HiddenPrizeLoadingML(processingValue = 0.64f) {}
}