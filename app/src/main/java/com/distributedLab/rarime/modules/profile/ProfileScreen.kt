package com.distributedLab.rarime.modules.profile

import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.distributedLab.rarime.R
import com.distributedLab.rarime.modules.main.Screen
import com.distributedLab.rarime.ui.components.AppIcon
import com.distributedLab.rarime.ui.components.CardContainer
import com.distributedLab.rarime.ui.components.PassportImage
import com.distributedLab.rarime.ui.theme.RarimeTheme

@Composable
fun ProfileScreen(navigate: (String) -> Unit) {
    Column(
        verticalArrangement = Arrangement.spacedBy(20.dp),
        modifier = Modifier
            .fillMaxSize()
            .background(RarimeTheme.colors.backgroundPrimary)
            .padding(vertical = 20.dp, horizontal = 12.dp)
    ) {
        Text(
            text = "Profile",
            style = RarimeTheme.typography.subtitle2,
            color = RarimeTheme.colors.textPrimary,
            modifier = Modifier.padding(horizontal = 8.dp)
        )
        Column(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            CardContainer {
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            text = "Account",
                            style = RarimeTheme.typography.subtitle3,
                            color = RarimeTheme.colors.textPrimary
                        )
                        Text(
                            text = "DID: Didq234234rw3423",
                            style = RarimeTheme.typography.body4,
                            color = RarimeTheme.colors.textSecondary
                        )
                    }
                    PassportImage(image = null, size = 40.dp)
                }
            }
            CardContainer {
                Column(verticalArrangement = Arrangement.spacedBy(20.dp)) {
                    ProfileRow(
                        iconId = R.drawable.ic_user_focus,
                        title = "Auth Method",
                        onClick = { navigate(Screen.Main.Profile.AuthMethod.route) }
                    )
                    ProfileRow(
                        iconId = R.drawable.ic_key,
                        title = "Export Keys",
                        onClick = { navigate(Screen.Main.Profile.ExportKeys.route) }
                    )
                }
            }
            CardContainer {
                Column(verticalArrangement = Arrangement.spacedBy(20.dp)) {
                    ProfileRow(
                        iconId = R.drawable.ic_globe_simple,
                        title = "Language",
                        value = "English",
                        onClick = { navigate(Screen.Main.Profile.Language.route) }
                    )
                    ProfileRow(
                        iconId = R.drawable.ic_sun,
                        title = "Theme",
                        value = "System",
                        onClick = { navigate(Screen.Main.Profile.Theme.route) }
                    )
                    ProfileRow(
                        iconId = R.drawable.ic_question,
                        title = "Privacy Policy",
                        onClick = {}
                    )
                    ProfileRow(
                        iconId = R.drawable.ic_flag,
                        title = "Terms of Use",
                        onClick = {}
                    )
                }
            }
            Text(
                text = "App version: 1.0",
                style = RarimeTheme.typography.body4,
                color = RarimeTheme.colors.textDisabled
            )
        }
    }
}

@Composable
private fun ProfileRow(
    @DrawableRes iconId: Int,
    title: String,
    value: String? = null,
    onClick: () -> Unit
) {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick
            )
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AppIcon(
                id = iconId,
                size = 20.dp,
                tint = RarimeTheme.colors.textPrimary,
                modifier = Modifier
                    .background(RarimeTheme.colors.componentPrimary, CircleShape)
                    .padding(6.dp)
            )
            Text(
                text = title,
                style = RarimeTheme.typography.subtitle4,
                color = RarimeTheme.colors.textPrimary
            )
        }
        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = value ?: "",
                style = RarimeTheme.typography.body3,
                color = RarimeTheme.colors.textSecondary
            )
            AppIcon(
                id = R.drawable.ic_caret_right,
                size = 16.dp,
                tint = RarimeTheme.colors.textSecondary,
            )
        }
    }
}

@Preview
@Composable
private fun ProfileScreenPreview() {
    ProfileScreen {}
}
