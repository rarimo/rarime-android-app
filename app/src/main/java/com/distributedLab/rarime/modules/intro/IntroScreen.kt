package com.distributedLab.rarime.modules.intro

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.distributedLab.rarime.modules.main.Screen
import com.distributedLab.rarime.ui.components.PrimaryButton
import com.distributedLab.rarime.ui.theme.RarimeTheme

@Composable
fun IntroScreen(navigateTo: (route: String) -> Unit) {
    Surface(color = RarimeTheme.colors.backgroundPrimary) {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp)
        ) {
            Text(
                text = "Intro Screen",
                style = RarimeTheme.typography.subtitle1,
                color = RarimeTheme.colors.textPrimary
            )
            PrimaryButton(
                text = "Open main",
                onClick = { navigateTo(Screen.Main.route) },
                modifier = Modifier.fillMaxWidth().padding(top = 16.dp)
            )
        }
    }
}

@Preview
@Composable
private fun IntroScreenPreview() {
    IntroScreen(navigateTo = {})
}