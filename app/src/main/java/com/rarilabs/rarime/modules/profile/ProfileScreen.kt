package com.rarilabs.rarime.modules.profile

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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.rarilabs.rarime.BuildConfig
import com.rarilabs.rarime.R
import com.rarilabs.rarime.data.enums.AppIcon
import com.rarilabs.rarime.data.enums.toLocalizedString
import com.rarilabs.rarime.ui.components.AppIcon
import com.rarilabs.rarime.ui.components.CardContainer
import com.rarilabs.rarime.ui.components.PassportImage
import com.rarilabs.rarime.ui.theme.RarimeTheme
import com.rarilabs.rarime.util.Screen
import com.rarilabs.rarime.util.WalletUtil

@Composable
fun ProfileScreen(
    appIcon: AppIcon,
    navigate: (String) -> Unit,
    viewModel: ProfileViewModel = hiltViewModel()
) {

    val language by viewModel.language
    val colorScheme by viewModel.colorScheme



    Column(
        verticalArrangement = Arrangement.spacedBy(20.dp),
        modifier = Modifier
            .fillMaxSize()
            .background(RarimeTheme.colors.backgroundPrimary)
            .padding(vertical = 20.dp, horizontal = 12.dp)
    ) {
        Text(
            text = stringResource(R.string.profile),
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
                            text = stringResource(R.string.account),
                            style = RarimeTheme.typography.subtitle3,
                            color = RarimeTheme.colors.textPrimary
                        )
                        Text(
                            text = stringResource(
                                R.string.user_address,
                                WalletUtil.formatAddress(viewModel.rarimoAddress)
                            ),
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
                        title = stringResource(R.string.auth_method),
                        onClick = { navigate(Screen.Main.Profile.AuthMethod.route) }
                    )
                    ProfileRow(
                        iconId = R.drawable.ic_key,
                        title = stringResource(R.string.export_keys),
                        onClick = { navigate(Screen.Main.Profile.ExportKeys.route) }
                    )
                }
            }
            CardContainer {
                Column(verticalArrangement = Arrangement.spacedBy(20.dp)) {
                    ProfileRow(
                        iconId = R.drawable.ic_globe_simple,
                        title = stringResource(R.string.language),
                        value = language.toLocalizedString(),
                        onClick = { navigate(Screen.Main.Profile.Language.route) }
                    )
                    ProfileRow(
                        iconId = R.drawable.ic_sun,
                        title = stringResource(R.string.theme),
                        value = colorScheme.toLocalizedString(),
                        onClick = { navigate(Screen.Main.Profile.Theme.route) }
                    )
                    ProfileRow(
                        iconId = R.drawable.ic_rarime,
                        title = stringResource(R.string.app_icon),
                        value = appIcon.toLocalizedString(),
                        onClick = { navigate(Screen.Main.Profile.AppIcon.route) }
                    )
                    ProfileRow(
                        iconId = R.drawable.ic_question,
                        title = stringResource(R.string.privacy_policy),
                        onClick = { navigate(Screen.Main.Profile.Privacy.route) }
                    )
                    ProfileRow(
                        iconId = R.drawable.ic_flag,
                        title = stringResource(R.string.terms_of_use),
                        onClick = { navigate(Screen.Main.Profile.Terms.route) }
                    )
                    ProfileRow(
                        iconId = R.drawable.ic_trash_simple,
                        title = "Delete profile",
                        onClick = {
                            viewModel.clearAllData()
                            navigate(Screen.Intro.route)
                        }
                    )
                }
            }
            Text(
                text = stringResource(R.string.app_version, BuildConfig.VERSION_NAME),
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
    ProfileScreen(
        appIcon = AppIcon.BLACK_AND_WHITE,
        navigate = {}
    )
}
