package com.rarilabs.rarime.modules.maintenanceScreen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
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
import com.rarilabs.rarime.ui.theme.RarimeTheme


@Composable
fun MaintenanceScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(RarimeTheme.colors.backgroundPrimary),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(32.dp)
        ) {
            Image(
                painter = painterResource(R.drawable.isometric_two_gears),
                contentDescription = null
            )

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = stringResource(R.string.maintenance_in_progress),
                    style = RarimeTheme.typography.h3,
                    color = RarimeTheme.colors.textPrimary,
                    textAlign = TextAlign.Center
                )
                Text(
                    text = stringResource(R.string.we_re_upgrading_for_a_better_experience_back_soon),
                    style = RarimeTheme.typography.body3,
                    color = RarimeTheme.colors.textSecondary,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Preview
@Composable
fun MaintenanceScreenPreview() {
    MaintenanceScreen()
}