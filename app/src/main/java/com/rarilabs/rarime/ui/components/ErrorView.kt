package com.rarilabs.rarime.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.rarilabs.rarime.R
import com.rarilabs.rarime.ui.theme.RarimeTheme

@Composable
fun ErrorView(
    title: String = "Error",
    subtitle: String = "Something went wrong",
    iconId: Int = R.drawable.ic_globe_simple_x
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(4.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = title,
                style = RarimeTheme.typography.h4,
                color = RarimeTheme.colors.textPrimary
            )
            Text(
                text = subtitle,
                style = RarimeTheme.typography.subtitle2,
                color = RarimeTheme.colors.textSecondary
            )
        }

        AppIcon(id = iconId, size = 100.dp, tint = RarimeTheme.colors.errorDarker)
    }
}

@Preview(showBackground = true)
@Composable
private fun ErrorViewPreview() {
    ErrorView()
}