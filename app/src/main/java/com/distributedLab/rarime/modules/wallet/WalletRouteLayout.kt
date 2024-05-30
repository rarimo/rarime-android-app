package com.distributedLab.rarime.modules.wallet

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.distributedLab.rarime.R
import com.distributedLab.rarime.ui.components.CardContainer
import com.distributedLab.rarime.ui.components.PrimaryTextButton
import com.distributedLab.rarime.ui.theme.RarimeTheme

@Composable
fun WalletRouteLayout(
    headerModifier: Modifier = Modifier,
    title: String,
    description: String,
    onBack: () -> Unit,
    content: @Composable () -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(24.dp),
        modifier = Modifier
            .fillMaxSize()
            .background(RarimeTheme.colors.backgroundPrimary)
            .padding(top = 20.dp)
    ) {
        Column(
            modifier = headerModifier,
            verticalArrangement = Arrangement.spacedBy(24.dp),
        ) {
            PrimaryTextButton(
                leftIcon = R.drawable.ic_caret_left,
                onClick = onBack
            )
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = title,
                    style = RarimeTheme.typography.subtitle2,
                    color = RarimeTheme.colors.textPrimary
                )
                Text(
                    text = description,
                    style = RarimeTheme.typography.body3,
                    color = RarimeTheme.colors.textSecondary
                )
            }
        }
        content()
    }
}

@Preview
@Composable
private fun WalletRouteLayoutPreview() {
    WalletRouteLayout(
        title = "Wallet Route Title",
        description = "Lorem ipsum dolor sit amet consectetur adipiscing elit",
        onBack = {},
    ) {
        CardContainer(modifier = Modifier.padding(horizontal = 20.dp)) {
            Text("Wallet Route Content")
        }
    }
}
