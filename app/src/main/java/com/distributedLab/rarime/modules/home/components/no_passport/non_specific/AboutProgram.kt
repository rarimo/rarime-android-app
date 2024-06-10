package com.distributedLab.rarime.modules.home.components.no_passport.non_specific

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
import com.distributedLab.rarime.ui.theme.RarimeTheme

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
        // TODO: implement real text
        Text(
            text = """
                It is a long established fact that a reader will be distracted by the readable content of a page when looking at its layout. The point of using Lorem Ipsum is that it has a more-or-less normal distribution of letters, as opposed to using
                
                'Content here, content here', making it look like readable English. Many desktop publishing packages and web page editors now use Lorem Ipsum as their default model text, and a search for 'lorem ipsum' will uncover many web sites still in their infancy. Various versions have evolved over the years, sometimes by accident, sometimes on purpose (injected humour and the like).
            """.trimIndent(),
            style = RarimeTheme.typography.body3,
            color = RarimeTheme.colors.textPrimary,
        )

        Column(
            modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "HOW CAN I GET THIS CODE?",
                style = RarimeTheme.typography.overline2,
                color = RarimeTheme.colors.textSecondary,
            )

            Text(
                text = "You must be invited by someone or receive a code that we post on our social channels",
                style = RarimeTheme.typography.body3,
                color = RarimeTheme.colors.textPrimary,
            )
        }

        Column(
            modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "QUESTION TITLE 2?",
                style = RarimeTheme.typography.overline2,
                color = RarimeTheme.colors.textSecondary,
            )

            Text(
                text = "You must be invited by someone or receive a code that we post on our social channels",
                style = RarimeTheme.typography.body3,
                color = RarimeTheme.colors.textPrimary,
            )

        }

        Column(
            modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "QUESTION TITLE 3?",
                style = RarimeTheme.typography.overline2,
                color = RarimeTheme.colors.textSecondary,
            )

            Text(
                text = "You must be invited by someone or receive a code that we post on our social channels",
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