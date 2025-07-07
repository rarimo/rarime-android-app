package com.rarilabs.rarime.modules.home.v3.ui.expanded

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.api.services.drive.DriveScopes
import com.rarilabs.rarime.R
import com.rarilabs.rarime.data.enums.AppColorScheme
import com.rarilabs.rarime.modules.home.v3.model.ANIMATION_DURATION_MS
import com.rarilabs.rarime.modules.home.v3.model.BaseWidgetProps
import com.rarilabs.rarime.modules.home.v3.model.HomeSharedKeys
import com.rarilabs.rarime.modules.home.v3.model.WidgetType
import com.rarilabs.rarime.modules.home.v3.ui.components.BaseExpandedWidget
import com.rarilabs.rarime.modules.home.v3.ui.components.BaseWidgetTitle
import com.rarilabs.rarime.modules.main.LocalMainViewModel
import com.rarilabs.rarime.modules.main.ScreenInsets
import com.rarilabs.rarime.modules.recoveryMethod.RecoveryMethodDetailScreen
import com.rarilabs.rarime.modules.recoveryMethod.RecoveryMethodViewModel
import com.rarilabs.rarime.ui.base.ButtonSize
import com.rarilabs.rarime.ui.components.AppBottomSheet
import com.rarilabs.rarime.ui.components.AppIcon
import com.rarilabs.rarime.ui.components.HorizontalDivider
import com.rarilabs.rarime.ui.components.PrimaryButton
import com.rarilabs.rarime.ui.components.SnackbarSeverity
import com.rarilabs.rarime.ui.components.getSnackbarDefaultShowOptions
import com.rarilabs.rarime.ui.components.rememberAppSheetState
import com.rarilabs.rarime.ui.theme.AppTheme
import com.rarilabs.rarime.ui.theme.RarimeTheme
import com.rarilabs.rarime.util.PrevireSharedAnimationProvider
import kotlinx.coroutines.launch

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun RecoveryMethodExpandedWidget(
    modifier: Modifier = Modifier,
    expandedWidgetProps: BaseWidgetProps.Expanded,
    innerPaddings: Map<ScreenInsets, Number>,
    viewModel: RecoveryMethodViewModel = hiltViewModel(),
    navigate: (String) -> Unit
) {
    val colorScheme by viewModel.colorScheme.collectAsState()
    val mainViewModel = LocalMainViewModel.current

    val privateKey by viewModel.privateKey.collectAsState()
    val sheetRecoveryMethod = rememberAppSheetState()
    val isInit by viewModel.isInit.collectAsState()

    val isDriveBtnEnabled by viewModel.isDriveButtonEnabled.collectAsState()

    val context = LocalContext.current

    val driveState by viewModel.driveState.collectAsState()


    val scope = rememberCoroutineScope()

    val signInErrorOptions = getSnackbarDefaultShowOptions(
        severity = SnackbarSeverity.Error, message = stringResource(
            R.string.drive_error_cant_sign_in_google_identity_account
        )
    )
    val backupErrorOptions = getSnackbarDefaultShowOptions(
        severity = SnackbarSeverity.Error, message = stringResource(
            R.string.drive_error_cant_back_up_your_private_key
        )
    )

    val googleSignInClient = remember {
        GoogleSignIn.getClient(
            context, GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .requestScopes(com.google.android.gms.common.api.Scope(DriveScopes.DRIVE_APPDATA))
                .build()
        )
    }

    val signInResultLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            viewModel.handleSignInResult(task) {
                scope.launch {
                    sheetRecoveryMethod.hide()
                    mainViewModel.showSnackbar(signInErrorOptions)
                }
            }
        }

    AppBottomSheet(
        state = sheetRecoveryMethod,
        backgroundColor = RarimeTheme.colors.backgroundPrimary,
        fullScreen = true,
        isHeaderEnabled = false,
        disablePullClose = false,
    ) {
        RecoveryMethodDetailScreen(
            driveState = driveState,
            onClose = { sheetRecoveryMethod.hide() },
            privateKey = privateKey!!,
            backupPrivateKey = {
                try {
                    viewModel.backupPrivateKey()
                } catch (e: Exception) {
                    sheetRecoveryMethod.hide()
                    scope.launch {
                        sheetRecoveryMethod.hide()
                        mainViewModel.showSnackbar(backupErrorOptions)
                    }
                }
            },
            isSwitchEnabled = isDriveBtnEnabled,
            deleteBackup = viewModel::deleteBackup,
            signIn = {
                signInResultLauncher.launch(
                    googleSignInClient.signInIntent
                )
            },
            isInit = isInit,
        )
    }

    RecoveryMethodExpandedWidgetContent(
        widgetProps = expandedWidgetProps,
        modifier = modifier,
        innerPaddings = innerPaddings,
        colorScheme = colorScheme,
        onClick = {
            sheetRecoveryMethod.show()
        }
    )


}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun RecoveryMethodExpandedWidgetContent(
    modifier: Modifier = Modifier,
    widgetProps: BaseWidgetProps.Expanded,
    innerPaddings: Map<ScreenInsets, Number>,
    colorScheme: AppColorScheme,
    onClick: () -> Unit
) {

    with(widgetProps) {
        with(sharedTransitionScope) {
            BaseExpandedWidget(
                modifier = modifier
                    .sharedElement(
                        state = rememberSharedContentState(HomeSharedKeys.background(layoutId)),
                        animatedVisibilityScope = animatedVisibilityScope,
                        boundsTransform = { _, _ -> tween(ANIMATION_DURATION_MS) })
                    .padding(
                        bottom = innerPaddings[ScreenInsets.BOTTOM]!!.toInt().dp
                    ), header = {
                    Header(
                        layoutId = layoutId,
                        onCollapse = onCollapse,
                        sharedTransitionScope = sharedTransitionScope,
                        animatedVisibilityScope = animatedVisibilityScope,
                        innerPaddings = innerPaddings
                    )
                }, footer = {

                    Footer(
                        layoutId = layoutId,
                        sharedTransitionScope = sharedTransitionScope,
                        animatedVisibilityScope = animatedVisibilityScope,
                        onClick = onClick
                    )

                }, body = {
                    Body(
                        layoutId = layoutId,
                        sharedTransitionScope = sharedTransitionScope,
                        animatedVisibilityScope = animatedVisibilityScope,

                        )
                }, columnModifier = Modifier, background = {
                    Background(
                        layoutId = layoutId,
                        sharedTransitionScope = sharedTransitionScope,
                        animatedVisibilityScope = animatedVisibilityScope,
                        colorScheme = colorScheme
                    )
                })
        }
    }
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
private fun Header(
    layoutId: Int,
    onCollapse: () -> Unit,
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedVisibilityScope,
    innerPaddings: Map<ScreenInsets, Number>,
) {
    with(sharedTransitionScope) {
        Row(
            modifier = Modifier
                .sharedBounds(
                    rememberSharedContentState(HomeSharedKeys.header(layoutId)),
                    animatedVisibilityScope = animatedVisibilityScope,
                    enter = fadeIn(),
                    exit = fadeOut(),
                    resizeMode = SharedTransitionScope.ResizeMode.ScaleToBounds()
                )
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .padding(top = innerPaddings[ScreenInsets.TOP]!!.toInt().dp),
            horizontalArrangement = Arrangement.End
        ) {

            IconButton(
                onClick = onCollapse,
                modifier = Modifier
                    .padding(20.dp)
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(color = RarimeTheme.colors.componentPrimary)
            ) {
                AppIcon(
                    id = R.drawable.ic_close_fill,
                    tint = RarimeTheme.colors.textPrimary,
                )
            }
        }
    }
}


@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
private fun Footer(
    layoutId: Int,
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedVisibilityScope,
    onClick: () -> Unit
) {

    with(sharedTransitionScope) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .sharedBounds(
                    rememberSharedContentState(HomeSharedKeys.footer(layoutId)),
                    animatedVisibilityScope = animatedVisibilityScope,
                    boundsTransform = { _, _ -> tween(durationMillis = ANIMATION_DURATION_MS) },
                    resizeMode = SharedTransitionScope.ResizeMode.ScaleToBounds()
                )
                .background(RarimeTheme.colors.backgroundPrimary)
                .padding(bottom = 20.dp, start = 20.dp, end = 20.dp)

        ) {
            HorizontalDivider()
            Spacer(Modifier.height(24.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {

                PrimaryButton(
                    text = stringResource(R.string.recovery_method_expanded_card_button_label),
                    onClick = onClick,
                    size = ButtonSize.Large,
                    modifier = Modifier.fillMaxWidth()
                )

            }
        }
    }
}


@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
private fun Body(
    layoutId: Int,
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedVisibilityScope,
) {


    with(sharedTransitionScope) {
        Column(
            modifier = Modifier
                .sharedBounds(
                    rememberSharedContentState(HomeSharedKeys.content(layoutId)),
                    animatedVisibilityScope = animatedVisibilityScope,
                    enter = fadeIn(),
                    exit = fadeOut(),
                    resizeMode = SharedTransitionScope.ResizeMode.RemeasureToBounds
                )

                .fillMaxSize()
        ) {

            Spacer(modifier = Modifier.weight(1f))

            Column(
                modifier = Modifier
                    .background(
                        RarimeTheme.colors.backgroundPrimary,
                        shape = RoundedCornerShape(20.dp, 20.dp, 0.dp, 0.dp)
                    )
                    .padding(20.dp)
                    .fillMaxWidth()

            ) {

                BaseWidgetTitle(
                    title = stringResource(R.string.recovery_method_expanded_card_title),
                    accentTitle = stringResource(R.string.recovery_method_expanded_card_accent_title),
                    titleStyle = RarimeTheme.typography.h1.copy(RarimeTheme.colors.textPrimary),
                    accentTitleStyle = RarimeTheme.typography.additional1.copy(brush = RarimeTheme.colors.gradient11),
                    caption = stringResource(R.string.recovery_method_expanded_card_caption),
                    captionStyle = RarimeTheme.typography.body4.copy(color = RarimeTheme.colors.textSecondary),
                    titleModifier = Modifier.sharedBounds(
                        rememberSharedContentState(HomeSharedKeys.title(layoutId)),
                        animatedVisibilityScope = animatedVisibilityScope,
                        boundsTransform = { _, _ -> tween(ANIMATION_DURATION_MS) },
                        resizeMode = SharedTransitionScope.ResizeMode.ScaleToBounds()
                    ),
                    accentTitleModifier = Modifier.sharedBounds(
                        rememberSharedContentState(HomeSharedKeys.accentTitle(layoutId)),
                        animatedVisibilityScope = animatedVisibilityScope,
                        boundsTransform = { _, _ -> tween(ANIMATION_DURATION_MS) },
                        resizeMode = SharedTransitionScope.ResizeMode.ScaleToBounds()
                    ),
                    captionModifier = Modifier.sharedBounds(
                        rememberSharedContentState(HomeSharedKeys.caption(layoutId)),
                        animatedVisibilityScope = animatedVisibilityScope,
                        boundsTransform = { _, _ -> tween(ANIMATION_DURATION_MS) },
                        resizeMode = SharedTransitionScope.ResizeMode.ScaleToBounds()
                    )
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = stringResource(R.string.recovery_method_expanded_card_desciption),
                    style = RarimeTheme.typography.body4.copy(color = RarimeTheme.colors.textSecondary)
                )
                Spacer(modifier = Modifier.height(24.dp))
            }
        }

    }

}


@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
private fun Background(
    layoutId: Int,
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedVisibilityScope,
    colorScheme: AppColorScheme
) {
    val isDark = when (colorScheme) {
        AppColorScheme.SYSTEM -> isSystemInDarkTheme()
        AppColorScheme.DARK -> true
        AppColorScheme.LIGHT -> false
    }

    val backgroundRes = remember(isDark) {
        if (isDark) R.drawable.ic_recovery_method_collapsed_card_background_dark
        else R.drawable.ic_recovery_method_collapsed_card_background_light
    }


    with(sharedTransitionScope) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(RarimeTheme.colors.backgroundPrimary)
        ) {
            Image(
                painter = painterResource(backgroundRes),
                contentDescription = null,
                contentScale = ContentScale.FillWidth,
                modifier = Modifier
                    .fillMaxWidth()
                    .sharedBounds(
                        rememberSharedContentState(
                            HomeSharedKeys.image(
                                layoutId
                            )
                        ),
                        animatedVisibilityScope = animatedVisibilityScope,
                        boundsTransform = { _, _ -> tween(durationMillis = ANIMATION_DURATION_MS) },
                        resizeMode = SharedTransitionScope.ResizeMode.ScaleToBounds()
                    )
                    .clip(RoundedCornerShape(20.dp))
            )
        }
    }
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Preview(showBackground = true, uiMode = android.content.res.Configuration.UI_MODE_NIGHT_NO)
@Composable
fun RecoveryMethodExpandedWidgetPreviewLightMode() {
    AppTheme {
        PrevireSharedAnimationProvider { sts, avs ->
            RecoveryMethodExpandedWidgetContent(
                widgetProps = BaseWidgetProps.Expanded(
                    onCollapse = {},
                    layoutId = WidgetType.RECOVERY_METHOD.layoutId,
                    animatedVisibilityScope = avs,
                    sharedTransitionScope = sts
                ),
                modifier = Modifier.height(820.dp),
                innerPaddings = mapOf(ScreenInsets.TOP to 0, ScreenInsets.BOTTOM to 0),
                colorScheme = AppColorScheme.LIGHT,
                onClick = {}
            )
        }
    }
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Preview(showBackground = true, uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES)
@Composable
fun RecoveryMethodExpandedWidgetPreviewDarkMode() {
    AppTheme {
        PrevireSharedAnimationProvider { sts, avs ->
            RecoveryMethodExpandedWidgetContent(
                widgetProps = BaseWidgetProps.Expanded(
                    onCollapse = {},
                    layoutId = WidgetType.RECOVERY_METHOD.layoutId,
                    animatedVisibilityScope = avs,
                    sharedTransitionScope = sts
                ),
                modifier = Modifier.height(820.dp),
                innerPaddings = mapOf(ScreenInsets.TOP to 0, ScreenInsets.BOTTOM to 0),
                colorScheme = AppColorScheme.DARK,
                onClick = {}
            )
        }
    }
}