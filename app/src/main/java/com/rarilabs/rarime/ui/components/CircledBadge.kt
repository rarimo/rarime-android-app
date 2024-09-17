package com.rarilabs.rarime.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Badge
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.rarilabs.rarime.R
import com.rarilabs.rarime.ui.theme.RarimeTheme

@Composable
fun CircledBadge(
    modifier: Modifier = Modifier,
    painter: Painter? = null,
    iconId: Int? = null,
    containerSize: Int = 80,
    containerColor: Color = RarimeTheme.colors.primaryMain,
    contentSize: Int = 40,
    contentColor: Color = RarimeTheme.colors.baseBlack,
    content: @Composable () -> Unit = {}
) {
    Box(
        modifier = Modifier
            .size(containerSize.dp)
            .background(containerColor, shape = CircleShape)
            .then(modifier), contentAlignment = Alignment.Center
    ) {
        if (painter != null) {
            Image(
                modifier = Modifier.size(contentSize.dp),
                painter = painter,
                contentDescription = "circled-badge-painter",
            )
        } else if (iconId != null) {
            AppIcon(id = iconId, size = contentSize.dp, tint = contentColor)
        } else {
            content()
        }
    }
}


@Composable
fun CircledBadgeWithCounter(
    modifier: Modifier = Modifier,
    painter: Painter? = null,
    iconId: Int? = null,
    containerSize: Int = 80,
    containerColor: Color = RarimeTheme.colors.primaryMain,
    contentSize: Int = 40,
    contentColor: Color = RarimeTheme.colors.baseBlack,
    count: Int = 0,
    badgeSize: Int = 20,
    content: @Composable () -> Unit = {}
) {
    Box(
        modifier = Modifier
            .size(containerSize.dp)
            .background(containerColor, shape = CircleShape)
            .then(modifier), contentAlignment = Alignment.Center
    ) {
        if (count <= 0) {
            if (painter != null) {
                Image(
                    modifier = Modifier.size(contentSize.dp),
                    painter = painter,
                    contentDescription = "circled-badge-painter",
                )
            } else if (iconId != null) {
                AppIcon(id = iconId, size = contentSize.dp, tint = contentColor)
            } else {
                content()
            }

        } else {

            if (painter != null) {
                Image(
                    modifier = Modifier.size(contentSize.dp),
                    painter = painter,
                    contentDescription = "circled-badge-painter",
                )
            } else if (iconId != null) {
                AppIcon(id = iconId, size = contentSize.dp, tint = contentColor)
            } else {
                content()
            }
            Badge(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .size(badgeSize.dp),
                containerColor = RarimeTheme.colors.errorMain
            ) {
                Text(text = count.toString(), color = RarimeTheme.colors.baseWhite)
            }

        }


    }
}

@Preview(showBackground = true)
@Composable
private fun CircledBadgePreview() {
    Row(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier,
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            CircledBadge(
                iconId = R.drawable.ic_lock, contentColor = RarimeTheme.colors.baseBlack
            )
            CircledBadge(
                containerSize = 160,
                iconId = R.drawable.ic_lock,
                contentSize = 80,
                contentColor = RarimeTheme.colors.baseBlack
            )
            CircledBadge(
                containerSize = 160,
                painter = painterResource(id = R.drawable.reward_coin),
                contentSize = 80,
            )
            CircledBadge(
                containerSize = 160,
                containerColor = RarimeTheme.colors.secondaryMain,
                painter = painterResource(id = R.drawable.reward_coin),
                contentSize = 120
            )
        }
        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            CircledBadgeWithCounter(
                count = 2, containerSize = 40, badgeSize = 20
            ) {
                AppIcon(
                    id = R.drawable.ic_bell, size = 20.dp, tint = RarimeTheme.colors.baseBlack
                )
            }

            CircledBadgeWithCounter(
                containerSize = 160, badgeSize = 40, count = 4
            ) {
                AppIcon(
                    id = R.drawable.ic_lock,
                    size = 80.dp,
                    tint = RarimeTheme.colors.baseBlack
                )
            }
            CircledBadgeWithCounter(
                containerSize = 160
            ) {
                Image(
                    painter = painterResource(id = R.drawable.reward_coin),
                    contentDescription = ""
                )
            }
            CircledBadgeWithCounter(
                containerSize = 160, containerColor = RarimeTheme.colors.secondaryMain
            ) {
                Image(
                    painter = painterResource(id = R.drawable.reward_coin),
                    contentDescription = ""
                )
            }
        }
    }
}
