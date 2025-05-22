package com.rarilabs.rarime.modules.intro

import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.rarilabs.rarime.R
import com.rarilabs.rarime.ui.components.AppIcon
import com.rarilabs.rarime.ui.components.HorizontalDivider
import com.rarilabs.rarime.ui.theme.RarimeTheme

data class AuthorizationMethod(
    val title: String, @DrawableRes val icon: Int, val onSelect: () -> Unit
)

@Composable
fun AuthorizationMethodsList(
    variants: List<AuthorizationMethod>
) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = stringResource(id = R.string.create_identity_selector_subtitle),
            style = RarimeTheme.typography.body3,
            color = RarimeTheme.colors.textSecondary,
        )
        Spacer(modifier = Modifier.height(16.dp))
        Column(
            modifier = Modifier
                .clip(RoundedCornerShape(24.dp))
                .border(1.dp, RarimeTheme.colors.componentPrimary, RoundedCornerShape(24.dp))
                .fillMaxWidth()
        ) {
            variants.forEachIndexed { index, item ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = rememberRipple(bounded = true)
                        ) { item.onSelect() }
                        .clip(RoundedCornerShape(16.dp))
                        .padding(horizontal = 16.dp, vertical = 20.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .background(
                                RarimeTheme.colors.gradient1, shape = RoundedCornerShape(50)
                            ),
                        contentAlignment = Alignment.Center,
                    ) {
                        AppIcon(id = item.icon, tint = RarimeTheme.colors.baseBlack)
                    }

                    Text(
                        text = item.title,
                        style = RarimeTheme.typography.h6,
                        color = RarimeTheme.colors.textPrimary,
                    )

                    Spacer(modifier = Modifier.weight(1f))

                    Icon(
                        modifier = Modifier.size(16.dp),
                        painter = painterResource(R.drawable.ic_caret_right),
                        contentDescription = stringResource(
                            R.string.select_icon_description
                        ),
                        tint = RarimeTheme.colors.textSecondary
                    )
                }

                if (index < variants.lastIndex) {
                    HorizontalDivider(
                        color = RarimeTheme.colors.componentPrimary,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AuthorizationMethodsListPreview() {
    AuthorizationMethodsList(
        variants = listOf(
            AuthorizationMethod(
                title = "Option 1", icon = R.drawable.ic_share, onSelect = {}),
            AuthorizationMethod(
                title = "Option 2", icon = R.drawable.ic_arrow_counter_clockwise, onSelect = {}),
            AuthorizationMethod(
                title = "Option 3", icon = R.drawable.ic_share_1, onSelect = {}),
        )
    )
}
