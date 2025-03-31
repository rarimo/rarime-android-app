package com.rarilabs.rarime.modules.rewards

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.rarilabs.rarime.R
import com.rarilabs.rarime.ui.theme.RarimeTheme
import com.rarilabs.rarime.util.Country

@Composable
fun UnSupportedPassport(
    nationality: String
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(RarimeTheme.colors.backgroundPrimary)
            .padding(top = 20.dp, bottom = 20.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(
                text = "Rewards",
                style = RarimeTheme.typography.subtitle4,
                color = RarimeTheme.colors.textPrimary
            )
        }

        Column(
            verticalArrangement = Arrangement.spacedBy(0.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(top = 150.dp)
        ) {

            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .size(72.dp)
                    .background(RarimeTheme.colors.componentPrimary, CircleShape)
                    .border(2.dp, RarimeTheme.colors.backgroundPrimary, CircleShape)
            ) {
                Text(
                    text = Country.fromISOCode(nationality)!!.flag,
                    style = RarimeTheme.typography.h3,
                    color = RarimeTheme.colors.textPrimary,
                )
            }
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 32.dp, end = 32.dp, top = 24.dp)
            ) {
                Text(
                    text = stringResource(
                        R.string.unsupported_country_title
                    ),
                    style = RarimeTheme.typography.h4,
                    color = RarimeTheme.colors.textPrimary,
                )
            }
            Column(
                modifier = Modifier
                    .align(Alignment.Start)
                    .padding(start = 32.dp, end = 32.dp, top = 8.dp),
                horizontalAlignment = Alignment.Start
            ) {
                Text(
                    text = stringResource(id = R.string.unsupported_country_description),
                    color = RarimeTheme.colors.textPrimary,
                    style = RarimeTheme.typography.body4,
                    textAlign = TextAlign.Center
                )
            }

        }
    }


}


@Preview
@Composable
private fun UnsupportedPassportPreview() {
    UnSupportedPassport(nationality = "GEO")
}