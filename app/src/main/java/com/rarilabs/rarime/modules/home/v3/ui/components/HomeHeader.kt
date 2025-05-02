package com.rarilabs.rarime.modules.home.v3.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.rarilabs.rarime.R
import com.rarilabs.rarime.ui.components.CircledBadgeWithCounter
import com.rarilabs.rarime.ui.theme.RarimeTheme

@Composable
fun HomeHeader(
    modifier: Modifier = Modifier,
    name: String? = null,
    notificationsCount: Int? = 0,
    onNotificationClick: () -> Unit,
) {
    Row(
        modifier.padding(top = 20.dp, start = 20.dp, end = 20.dp, bottom = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(

            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(R.string.hi),
                style = RarimeTheme.typography.subtitle4,
                color = RarimeTheme.colors.textSecondary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = name ?: stringResource(R.string.stranger),
                style = RarimeTheme.typography.subtitle4,
                color = RarimeTheme.colors.textPrimary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }

        Spacer(modifier = Modifier.weight(1f))
        CircledBadgeWithCounter(
            modifier = Modifier.clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = rememberRipple(
                    bounded = true,
                    radius = 20.dp,
                )
            ) { onNotificationClick() },
            iconId = R.drawable.ic_bell,
            containerSize = 40,
            containerColor = RarimeTheme.colors.componentPrimary,
            contentSize = 20,
            badgeSize = 16,
            count = notificationsCount ?: 0,
            contentColor = RarimeTheme.colors.textPrimary
        )
    }
}