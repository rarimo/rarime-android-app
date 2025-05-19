package com.rarilabs.rarime.ui.components.enter_program.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.rarilabs.rarime.ui.theme.RarimeTheme
import com.rarilabs.rarime.util.Constants.NOT_ALLOWED_COUNTRIES
import com.rarilabs.rarime.util.Country

@Composable
fun AboutProgram(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(RarimeTheme.colors.backgroundPure)
            .padding(20.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(32.dp)
    ) {
        val localizedBannedCountries = NOT_ALLOWED_COUNTRIES.map {
            Country.fromISOCode(it).localizedName
        }

        // TODO: implement real text
        Text(
            text = """
                RMO tokens will be exclusively distributed via the RariMe app. To claim tokens, create an incognito profile using your biometric passport. Depending on which country issued the passport, you’ll either be able to claim a token right away or be put on a waitlist.
                
                If you are added to the waitlist it means that you are eligible to claim tokens in the next wave of airdrops. The app will notify you when you are added.
                
                Certain jurisdictions are excluded from the reward program: ${
                localizedBannedCountries.joinToString(
                    ", "
                )
            }
            """.trimIndent(),
            style = RarimeTheme.typography.body3,
            color = RarimeTheme.colors.textPrimary,
        )

        Column(
            modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "HOW CAN I GET THE INVITE CODE?",
                style = RarimeTheme.typography.overline2,
                color = RarimeTheme.colors.textSecondary,
            )

            Text(
                text = "The app’s rewards program is invite-only. Get invited by an authorized user or ask a community member for an invite code.",
                style = RarimeTheme.typography.body3,
                color = RarimeTheme.colors.textPrimary,
            )
        }
    }
}

@Preview
@Composable
fun AboutProgrammPreview() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(RarimeTheme.colors.backgroundPrimary)
    ) {
        AboutProgram()
    }
}