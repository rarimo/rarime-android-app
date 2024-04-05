package com.distributedLab.rarime.modules.passport

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.distributedLab.rarime.R
import com.distributedLab.rarime.ui.base.ButtonSize
import com.distributedLab.rarime.ui.components.AppBottomSheet
import com.distributedLab.rarime.ui.components.AppSheetState
import com.distributedLab.rarime.ui.components.HorizontalDivider
import com.distributedLab.rarime.ui.components.PrimaryButton
import com.distributedLab.rarime.ui.components.rememberAppSheetState
import com.distributedLab.rarime.ui.theme.RarimeTheme

@Composable
fun PassportIntroSheet(
    sheetState: AppSheetState,
    onStart: () -> Unit
) {
    AppBottomSheet(
        state = sheetState,
        bottomBar = { hide ->
            PrimaryButton(
                text = "Let's Start",
                modifier = Modifier.fillMaxWidth(),
                size = ButtonSize.Large,
                onClick = { hide { onStart() } }
            )
        }
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(24.dp),
            modifier = Modifier.padding(bottom = 80.dp)
        ) {
            Image(
                painter = painterResource(id = R.drawable.passport),
                contentDescription = null,
                contentScale = ContentScale.FillWidth,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(40.dp)
                    .padding(bottom = 20.dp)
            )
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    text = "Passport",
                    modifier = Modifier.fillMaxWidth(),
                    style = RarimeTheme.typography.h5,
                    color = RarimeTheme.colors.textPrimary,
                    textAlign = TextAlign.Center
                )
                Text(
                    text = "You’ll need a biometric document",
                    modifier = Modifier.fillMaxWidth(),
                    style = RarimeTheme.typography.body3,
                    color = RarimeTheme.colors.textSecondary,
                    textAlign = TextAlign.Center
                )
            }
            HorizontalDivider()
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                DescriptionStepRow("1.", "Personal data never leaves the device ")
                DescriptionStepRow("2.", "Convert your data into ZK proofs")
                DescriptionStepRow("3.", "Use proofs across the ecosystem")
                DescriptionStepRow("\uD83C\uDF81", "Get rewarded with 50 RRMO")
            }
        }
    }
}

@Composable
fun DescriptionStepRow(
    prefix: String,
    text: String
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            modifier = Modifier.width(18.dp),
            text = prefix,
            style = RarimeTheme.typography.subtitle4,
            color = RarimeTheme.colors.textPrimary,
        )
        Text(
            text = text,
            style = RarimeTheme.typography.body3,
            color = RarimeTheme.colors.textPrimary,
        )
    }
}

@Preview
@Composable
private fun PassportIntroSheetPreview() {
    PassportIntroSheet(
        sheetState = rememberAppSheetState(true),
        onStart = {}
    )
}