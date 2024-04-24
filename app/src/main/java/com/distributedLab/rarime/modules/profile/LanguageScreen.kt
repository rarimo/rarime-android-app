package com.distributedLab.rarime.modules.profile

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.distributedLab.rarime.R
import com.distributedLab.rarime.ui.components.AppIcon
import com.distributedLab.rarime.ui.components.CardContainer
import com.distributedLab.rarime.ui.theme.RarimeTheme

@Composable
fun LanguageScreen(onBack: () -> Unit) {
    ProfileRouteLayout(
        title = "Language",
        onBack = onBack
    ) {
        CardContainer {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "English",
                    style = RarimeTheme.typography.subtitle4,
                    color = RarimeTheme.colors.textPrimary
                )
                AppIcon(
                    id = R.drawable.ic_check,
                    size = 20.dp,
                    tint = RarimeTheme.colors.textPrimary,
                )
            }
        }
    }
}

@Preview
@Composable
private fun LanguageScreenPreview() {
    LanguageScreen {}
}
