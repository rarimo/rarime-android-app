package com.rarilabs.rarime.modules.home.components.no_passport

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import com.rarilabs.rarime.ui.base.ButtonSize
import com.rarilabs.rarime.ui.components.CardContainer
import com.rarilabs.rarime.ui.components.HorizontalDivider
import com.rarilabs.rarime.ui.components.PrimaryButton
import com.rarilabs.rarime.ui.theme.RarimeTheme

@Composable
fun GreetCommonActionCard(
    mediaContent: @Composable () -> Unit,
    title: String,
    subtitle: String,
    btnText: String,
    onClick: () -> Unit
) {
    CardContainer {
        Column(
            verticalArrangement = Arrangement.spacedBy(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            mediaContent()

            Text(
                text = title,
                style = RarimeTheme.typography.h6,
                textAlign = TextAlign.Center,
                color = RarimeTheme.colors.textPrimary
            )
            Text(
                text = subtitle,
                style = RarimeTheme.typography.body2,
                textAlign = TextAlign.Center,
                color = RarimeTheme.colors.textSecondary
            )
            HorizontalDivider()
            PrimaryButton(
                modifier = Modifier.fillMaxWidth(),
                size = ButtonSize.Large,
                text = btnText,
                rightIcon = R.drawable.ic_arrow_right,
                onClick = onClick
            )
        }
    }
}

@Preview
@Composable
fun GreetCommonActionCardPreview() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(vertical = 16.dp, horizontal = 12.dp)
            .background(RarimeTheme.colors.backgroundPrimary)
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
            btnText = stringResource(id = R.string.greet_common_action_card_btn_text),
            onClick = {}
        )
    }
}