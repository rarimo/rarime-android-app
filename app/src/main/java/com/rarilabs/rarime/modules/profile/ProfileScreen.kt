package com.rarilabs.rarime.modules.profile

import android.graphics.Bitmap
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.rarilabs.rarime.BuildConfig
import com.rarilabs.rarime.R
import com.rarilabs.rarime.data.enums.AppColorScheme
import com.rarilabs.rarime.data.enums.AppIcon
import com.rarilabs.rarime.data.enums.toLocalizedString
import com.rarilabs.rarime.modules.recoveryMethod.RecoveryMethodDetailScreen
import com.rarilabs.rarime.ui.components.AppBottomSheet
import com.rarilabs.rarime.ui.components.AppIcon
import com.rarilabs.rarime.ui.components.ConfirmationDialog
import com.rarilabs.rarime.ui.components.PassportImage
import com.rarilabs.rarime.ui.components.rememberAppSheetState
import com.rarilabs.rarime.ui.theme.RarimeTheme
import com.rarilabs.rarime.util.Screen
import com.rarilabs.rarime.util.SendEmailUtil
import com.rarilabs.rarime.util.WalletUtil
import kotlinx.coroutines.launch

@Composable
fun ProfileScreen(
    appIcon: AppIcon, navigate: (String) -> Unit, viewModel: ProfileViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    var isFeedbackDialogShown by remember { mutableStateOf(false) }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult(), onResult = {
            isFeedbackDialogShown = false
        })

    val image = remember {
        viewModel.getImage()
    }

    val colorScheme by viewModel.colorScheme.collectAsState()

    val sheetRecoveryMethod = rememberAppSheetState()

    AppBottomSheet(
        state = sheetRecoveryMethod,
        backgroundColor = RarimeTheme.colors.backgroundPrimary,
        fullScreen = true,
        isHeaderEnabled = false,
        disablePullClose = true

    ) {
        RecoveryMethodDetailScreen(onClose = {sheetRecoveryMethod.hide()}, onCopy = {}) //Todo Changes
    }

    ProfileScreenContent(
        rarimoAddress = WalletUtil.formatAddress(viewModel.rarimoAddress),
        passportImage = image,
        navigate = navigate,
        colorScheme = colorScheme,
        appIcon = appIcon,
        onFeedbackConfirm = {
            val decryptedFile = viewModel.getDecryptedFeedbackFile()
            launcher.launch(SendEmailUtil.sendEmail(decryptedFile, context))
        },
        onClearConfirm = {
            viewModel.clearAllData(context)
        },
        onRecoveryMethod ={ sheetRecoveryMethod.show()})
}

@Composable
fun ProfileScreenContent(
    appIcon: AppIcon,
    rarimoAddress: String,
    passportImage: Bitmap?,
    navigate: (String) -> Unit,
    colorScheme: AppColorScheme,
    onFeedbackConfirm: suspend () -> Unit = {},
    onClearConfirm: suspend () -> Unit = {},
    onRecoveryMethod : () -> Unit = {}
) {
    val scope = rememberCoroutineScope()

    Column(
        verticalArrangement = Arrangement.spacedBy(20.dp),
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .background(RarimeTheme.colors.backgroundPrimary)
            .padding(20.dp)
    ) {
        Text(
            text = stringResource(R.string.profile),
            style = RarimeTheme.typography.subtitle4,
            color = RarimeTheme.colors.textPrimary,
        )

        Column(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Column(
                modifier = Modifier
                    .background(
                        RarimeTheme.colors.componentPrimary, RoundedCornerShape(20.dp)
                    )
                    .padding(20.dp)
            ) {
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            text = stringResource(R.string.account),
                            style = RarimeTheme.typography.buttonLarge,
                            color = RarimeTheme.colors.textPrimary
                        )
                        Text(
                            text = WalletUtil.formatAddress(
                                stringResource(
                                    R.string.user_address, rarimoAddress
                                )
                            ),
                            style = RarimeTheme.typography.body5,
                            color = RarimeTheme.colors.textSecondary
                        )
                    }
                    PassportImage(image = passportImage, size = 40.dp)
                }
            }
            Column(
                modifier = Modifier
                    .background(
                        RarimeTheme.colors.componentPrimary, RoundedCornerShape(20.dp)
                    )
                    .padding(16.dp)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(20.dp)) {
                    ProfileRow(
                        iconId = R.drawable.ic_user_shared_2_line,
                        title = stringResource(R.string.recovery_method),
                        onClick = { onRecoveryMethod() })
                    ProfileRow(
                        iconId = R.drawable.ic_shield_keyhole_line,
                        title = stringResource(R.string.auth_method),
                        onClick = { navigate(Screen.Main.Profile.AuthMethod.route) })
                }
            }
            Column(
                modifier = Modifier
                    .background(
                        RarimeTheme.colors.componentPrimary, RoundedCornerShape(20.dp)
                    )
                    .padding(16.dp)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(20.dp)) {
                    ProfileRow(
                        iconId = R.drawable.ic_sun_line,
                        title = stringResource(R.string.theme),
                        value = colorScheme.toLocalizedString(),
                        onClick = { navigate(Screen.Main.Profile.Theme.route) })
                    ProfileRow(
                        iconId = R.drawable.ic_rarime,
                        title = stringResource(R.string.app_icon),
                        value = appIcon.toLocalizedString(),
                        onClick = { navigate(Screen.Main.Profile.AppIcon.route) })

                }
            }
            Column(
                modifier = Modifier
                    .background(
                        RarimeTheme.colors.componentPrimary, RoundedCornerShape(20.dp)
                    )
                    .padding(16.dp)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(20.dp)) {
                    ProfileRow(
                        iconId = R.drawable.ic_question_line,
                        title = stringResource(R.string.privacy_policy),
                        onClick = { navigate(Screen.Main.Profile.Privacy.route) })
                    ProfileRow(
                        iconId = R.drawable.ic_flag_line,
                        title = stringResource(R.string.terms_of_use),
                        onClick = { navigate(Screen.Main.Profile.Terms.route) })
                    ProfileRow(
                        iconId = R.drawable.ic_chat,
                        title = stringResource(R.string.give_us_feedback),
                        onClick = {
                            scope.launch {
                                onFeedbackConfirm.invoke()
                            }
                        })
                }
            }

            Column(
                modifier = Modifier
                    .background(
                        RarimeTheme.colors.componentPrimary, RoundedCornerShape(20.dp)
                    )
                    .padding(16.dp)
            ) {
                var isDeleteAccountDialogShown by remember { mutableStateOf(false) }

                ProfileRow(
                    iconId = R.drawable.ic_trash_simple,
                    title = "Delete account",
                    onClick = { isDeleteAccountDialogShown = true },
                    contentColors = getProfileRowContentColors(
                        leadingIcon = RarimeTheme.colors.errorMain,
                        leadingIconBg = RarimeTheme.colors.errorLighter,
                        title = RarimeTheme.colors.errorMain,
                        value = RarimeTheme.colors.errorMain,
                        trailingIcon = Color.Transparent,
                    ),
                )

                if (isDeleteAccountDialogShown) {
                    ConfirmationDialog(
                        title = stringResource(R.string.delete_profile_title),
                        subtitle = stringResource(R.string.delete_profile_desc),
                        onConfirm = {
                            scope.launch {
                                onClearConfirm.invoke()
                            }
                        },
                        onCancel = { isDeleteAccountDialogShown = false },
                        cancelButtonText = stringResource(id = R.string.delete_profile_cancel_btn),
                        confirmButtonText = stringResource(id = R.string.delete_profile_confirm_btn),
                    )
                }
            }
            Text(
                text = stringResource(R.string.app_version, BuildConfig.VERSION_NAME),
                style = RarimeTheme.typography.body5,
                color = RarimeTheme.colors.textPlaceholder
            )
        }
    }
}

data class ProfileRowContentColors(
    val leadingIcon: Color,
    val leadingIconBg: Color,
    val title: Color,
    val value: Color,
    val trailingIcon: Color,
)

@Composable
fun getProfileRowContentColors(
    leadingIcon: Color = RarimeTheme.colors.textPrimary,
    leadingIconBg: Color = RarimeTheme.colors.componentPrimary,
    title: Color = RarimeTheme.colors.textPrimary,
    value: Color = RarimeTheme.colors.textSecondary,
    trailingIcon: Color = RarimeTheme.colors.textSecondary,
): ProfileRowContentColors {
    return ProfileRowContentColors(
        leadingIcon = leadingIcon,
        leadingIconBg = leadingIconBg,
        title = title,
        value = value,
        trailingIcon = trailingIcon,
    )
}

@Composable
private fun ProfileRow(
    @DrawableRes iconId: Int,
    title: String,
    value: String? = null,
    onClick: () -> Unit,

    contentColors: ProfileRowContentColors = getProfileRowContentColors(),
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
                tint = contentColors.leadingIcon,
                modifier = Modifier
                    .background(contentColors.leadingIconBg, CircleShape)
                    .padding(6.dp)
            )
            Text(
                text = title,
                style = RarimeTheme.typography.buttonMedium,
                color = contentColors.title
            )
        }
        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = value ?: "",
                style = RarimeTheme.typography.body4,
                color = contentColors.value
            )
            AppIcon(
                id = R.drawable.ic_arrow_right_s_line,
                size = 20.dp,
                tint = contentColors.trailingIcon,
            )
        }
    }
}

@Preview
@Composable
private fun ProfileScreenPreview() {
    ProfileScreenContent(
        rarimoAddress = "0xbF1823EF5Ca4484517F930c695b07544C2b43Efe",
        passportImage = null,
        navigate = {},
        colorScheme = AppColorScheme.LIGHT,
        appIcon = AppIcon.BLACK_AND_WHITE,
    )
}
