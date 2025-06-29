package com.rarilabs.rarime.modules.earn.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.rarilabs.rarime.R
import com.rarilabs.rarime.ui.components.AppIcon
import com.rarilabs.rarime.ui.theme.RarimeTheme

@Composable
fun RewardAmount(
    modifier: Modifier = Modifier,
    amount: Long = 0,
    tokenIconId: Int = R.drawable.ic_rarimo,
) {
    Box(
        modifier = modifier
            .padding(vertical = 2.dp, horizontal = 8.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Text(
                text = "+${amount}",
                style = RarimeTheme.typography.subtitle5,
                color = RarimeTheme.colors.textSecondary,
            )
            AppIcon(id = tokenIconId, size = 14.dp, tint = RarimeTheme.colors.primaryMain)
        }
    }
}

