package com.rarilabs.rarime.modules.hiddenPrize

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.rarilabs.rarime.R
import com.rarilabs.rarime.ui.base.BaseButton
import com.rarilabs.rarime.ui.base.ButtonSize
import com.rarilabs.rarime.ui.theme.RarimeTheme

@Composable
fun HiddenPrizeError(
    modifier: Modifier = Modifier, onBack: () -> Unit = {}
) {
    Box(Modifier.fillMaxSize()) {

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
                        stringResource(R.string.query_proof_error_subtitle),
                        color = RarimeTheme.colors.baseWhite,
                        textAlign = TextAlign.Center,
                        style = RarimeTheme.typography.h3
                    )
                }
            }


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
                onClick = {
                    onBack()
                }) {
                Text(
                    stringResource(R.string.hidden_prize_wrong_screen_back_home),
                    color = RarimeTheme.colors.baseWhite
                )
            }

        }
    }
}


@Composable
@Preview(showBackground = true)
fun HiddenPrizeErrorPreview() {
    Box(Modifier.fillMaxSize()) {
        HiddenPrizeError(
            onBack = {})
    }
}