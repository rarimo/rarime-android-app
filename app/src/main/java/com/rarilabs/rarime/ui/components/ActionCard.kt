package com.rarilabs.rarime.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.rarilabs.rarime.R
import com.rarilabs.rarime.ui.theme.RarimeTheme

enum class ActionCardVariants {
    Filled,
    Outlined,
}

@Composable
fun ActionCardContent (
    title: String,
    description: String,
    leadingContent: @Composable (() -> Unit)? = null,
) {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        leadingContent?.let {
            it()
            Spacer(modifier = Modifier.width(16.dp))
        }
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = title,
                style = RarimeTheme.typography.subtitle3,
                color = RarimeTheme.colors.textPrimary
            )
            Text(
                text = description,
                style = RarimeTheme.typography.body3,
                color = RarimeTheme.colors.textSecondary
            )
        }
        AppIcon(
            id = R.drawable.ic_caret_right,
            size = 16.dp,
            modifier = Modifier
                .background(RarimeTheme.colors.primaryMain, CircleShape)
                .padding(4.dp)
        )
    }
}

@Composable
fun ActionCard(
    title: String,
    description: String,
    leadingContent: @Composable (() -> Unit)? = null,
    variant: ActionCardVariants = ActionCardVariants.Filled,
    onClick: () -> Unit,
) {
    val interactionSource = remember { MutableInteractionSource() }

    if (variant == ActionCardVariants.Filled) {
        CardContainer(
            modifier = Modifier
                .clickable(
                    interactionSource = interactionSource,
                    indication = null,
                    onClick = onClick
                )
        ) {
            ActionCardContent(
                title = title,
                description = description,
                leadingContent = leadingContent
            )
        }
    } else if (variant == ActionCardVariants.Outlined) {
        CardContainer(
            modifier = Modifier
                .clickable(
                    interactionSource = interactionSource,
                    indication = null,
                    onClick = onClick
                )
                .border(
                    width = 1.dp,
                    color = RarimeTheme.colors.componentPrimary,
                    shape = RoundedCornerShape(24.dp)
                ),
            backgroundColor = Color.Transparent
        ) {
            ActionCardContent(
                title = title,
                description = description,
                leadingContent = leadingContent
            )
        }
    }
}

@Composable
@Preview(showBackground = true)
private fun ActionCardPreview() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(vertical = 16.dp, horizontal = 12.dp)
            .background(RarimeTheme.colors.backgroundPrimary),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        ActionCard(
            title = "Scan passport",
            description = "Scan your passport to verify your identity",
            onClick = {}
        )

        ActionCard(
            title = "Scan passport",
            description = "Scan your passport to verify your identity",
            leadingContent = {
                Box(
                    modifier = Modifier
                        .width(38.dp)
                        .height(38.dp)
                        .background(RarimeTheme.colors.componentPrimary, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    AppIcon(id = R.drawable.ic_share)
                }
            },
            onClick = {}
        )

        ActionCard(
            title = "Scan passport",
            description = "Scan your passport to verify your identity",
            leadingContent = {
                Image(
                    modifier = Modifier.size(42.dp),
                    painter = painterResource(id = R.drawable.reward_coin),
                    contentDescription = "decor",
                )
            },
            onClick = {}
        )

        ActionCard(
            title = "Scan passport",
            description = "Scan your passport to verify your identity",
            leadingContent = {
                Text(
                    text = "🇺🇦",
                    style = RarimeTheme.typography.h5,
                    color = RarimeTheme.colors.textPrimary,
                    textAlign = TextAlign.Center
                )
            },
            onClick = {}
        )

        ActionCard(
            title = "RARIME",
            description = "Learn more about the App",
            leadingContent = {
                AppIcon(id = R.drawable.ic_info, size = 24.dp)
            },
            variant = ActionCardVariants.Outlined,
            onClick = {}
        )
    }
}
