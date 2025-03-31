package com.rarilabs.rarime.modules.wallet

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.rarilabs.rarime.R
import com.rarilabs.rarime.ui.components.CardContainer
import com.rarilabs.rarime.ui.components.PrimaryTextButton
import com.rarilabs.rarime.ui.theme.RarimeTheme

@Composable
fun WalletRouteLayout(
    headerModifier: Modifier = Modifier,
    title: String = "",
    description: String = "",
    onBack: () -> Unit,
    action: @Composable () -> Unit = {},
    content: @Composable () -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(24.dp),
        modifier = Modifier
            .fillMaxSize()
            .background(RarimeTheme.colors.backgroundPrimary)
            .padding(top = 20.dp)
            .padding(horizontal = 20.dp)
    ) {
        Column(
            modifier = headerModifier,
            verticalArrangement = Arrangement.spacedBy(24.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                PrimaryTextButton(
                    leftIcon = R.drawable.ic_caret_left,
                    onClick = onBack
                )

                action()
            }
            if (title.isNotEmpty() || description.isNotEmpty()) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    if (title.isNotEmpty()) {
                        Text(
                            text = title,
                            style = RarimeTheme.typography.subtitle4,
                            color = RarimeTheme.colors.textPrimary
                        )
                    }

                    if (description.isNotEmpty()) {
                        Text(
                            text = description,
                            style = RarimeTheme.typography.body4,
                            color = RarimeTheme.colors.textSecondary
                        )
                    }
                }
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
