@file:OptIn(ExperimentalSharedTransitionApi::class)

package com.rarilabs.rarime.modules.home.v3.ui.expanded

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.isSystemInDarkTheme
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.rarilabs.rarime.R
import com.rarilabs.rarime.api.points.models.ReferralCodeStatuses
import com.rarilabs.rarime.data.enums.AppColorScheme
import com.rarilabs.rarime.modules.earn.EarnViewModel
import com.rarilabs.rarime.modules.earn.InviteOthersContent
import com.rarilabs.rarime.modules.earn.TaskCard
import com.rarilabs.rarime.modules.home.v3.model.ANIMATION_DURATION_MS
import com.rarilabs.rarime.modules.home.v3.model.BaseWidgetProps
import com.rarilabs.rarime.modules.home.v3.model.HomeSharedKeys
import com.rarilabs.rarime.modules.home.v3.model.WidgetType
import com.rarilabs.rarime.modules.home.v3.ui.components.BaseExpandedWidget
import com.rarilabs.rarime.modules.home.v3.ui.components.BaseWidgetTitle
import com.rarilabs.rarime.modules.main.ScreenInsets
import com.rarilabs.rarime.ui.components.AppBottomSheet
import com.rarilabs.rarime.ui.components.AppIcon
import com.rarilabs.rarime.ui.components.HorizontalDivider
import com.rarilabs.rarime.ui.components.rememberAppSheetState
import com.rarilabs.rarime.ui.theme.RarimeTheme
import com.rarilabs.rarime.util.PrevireSharedAnimationProvider

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun EarnExpandedWidget(
    modifier: Modifier = Modifier,
    expandedWidgetProps: BaseWidgetProps.Expanded,
    innerPaddings: Map<ScreenInsets, Number>,
    viewModel: EarnViewModel = hiltViewModel(),
    navigate: (String) -> Unit
) {
    val inviteOthers = rememberAppSheetState()
    val colorScheme by viewModel.colorScheme.collectAsState()
    val pointsBalances by viewModel.pointsAsset.collectAsState()
    val referralCodes by remember {
        derivedStateOf {
            pointsBalances?.balanceDetails?.attributes?.referral_codes ?: emptyList()
        }
    }
    val isVerifiedPointsBalance by remember {
        derivedStateOf {
            pointsBalances?.balanceDetails?.attributes?.is_verified ?: false
        }
    }
    val pointsBalance by remember {
        derivedStateOf {
            pointsBalances?.balanceDetails?.attributes?.amount ?: 0L
        }
    }
    val maxValueOfReferrals by remember {
        derivedStateOf {
            referralCodes.size
        }
    }
    val currentValueOfReferrals by remember {
        derivedStateOf {
            referralCodes.count { it.status != ReferralCodeStatuses.ACTIVE.value }
        }
    }
    AppBottomSheet(
        state = inviteOthers,
        backgroundColor = RarimeTheme.colors.backgroundSurface1,
        isHeaderEnabled = false,
        fullScreen = false,
    ) {
        InviteOthersContent(
            modifier = Modifier.scrollable(
                state = rememberScrollState(),
                orientation = Orientation.Vertical
            ),
            onClose = { inviteOthers.hide() },
            isVerifiedPointsBalance = isVerifiedPointsBalance,
            referralCodes = referralCodes,
        )
    }

    EarnExpandedWidgetContent(
        widgetProps = expandedWidgetProps,
        modifier = modifier,
        innerPaddings = innerPaddings,
        onClick = {
            inviteOthers.show()
        },
        colorScheme = colorScheme,
        pointsBalance = pointsBalance,
        maxValueOfReferrals = maxValueOfReferrals,
        currentValueOfReferrals = currentValueOfReferrals,
    )
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun EarnExpandedWidgetContent(
    modifier: Modifier = Modifier,
    widgetProps: BaseWidgetProps.Expanded,
    innerPaddings: Map<ScreenInsets, Number>,
    onClick: () -> Unit,
    colorScheme: AppColorScheme,
    pointsBalance: Long,
    maxValueOfReferrals: Int,
    currentValueOfReferrals: Int
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
                    ),
                header = {
                    Header(
                        layoutId = layoutId,
                        onCollapse = onCollapse,
                        sharedTransitionScope = sharedTransitionScope,
                        animatedVisibilityScope = animatedVisibilityScope,
                        innerPaddings = innerPaddings,
                        balance = pointsBalance
                    )
                },
                body = {
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
                },
                footer = {
                    Footer(
                        countOfTask = 1,
                        onClick = onClick,
                        maxValueOfRefferals = maxValueOfReferrals,
                        currentValueOfRefferals = currentValueOfReferrals
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
    balance: Long
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
            //horizontalArrangement = Arrangement.End
        ) {
            Box(
                modifier = Modifier
                    .padding(20.dp)
                    .clip(RoundedCornerShape(40.dp))
                    .background(color = RarimeTheme.colors.componentPrimary),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    modifier = Modifier.padding(vertical = 10.dp, horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Reserved: ",
                        style = RarimeTheme.typography.subtitle6,
                        color = RarimeTheme.colors.textPrimary,

                        )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = balance.toString(),
                        style = RarimeTheme.typography.h6,
                        color = RarimeTheme.colors.textPrimary,
                        modifier = Modifier
                    )
                    AppIcon(
                        id = (R.drawable.ic_rarimo),
                        size = 17.dp,
                        tint = RarimeTheme.colors.textPrimary,
                    )
                }


            }
            Spacer(modifier = Modifier.weight(1f))
            IconButton(
                onClick = onCollapse,
                modifier = Modifier
                    .padding(20.dp)
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(color = RarimeTheme.colors.componentPrimary)
            ) {
                AppIcon(
                    id = R.drawable.ic_close,
                    tint = RarimeTheme.colors.textPrimary,
                )
            }
        }
    }
}


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
                        RarimeTheme.colors.backgroundSurface1,
                        shape = RoundedCornerShape(20.dp, 20.dp, 0.dp, 0.dp)
                    )
                    .padding(20.dp)

            ) {


                BaseWidgetTitle(
                    title = stringResource(R.string.earn),
                    titleStyle = RarimeTheme.typography.h1.copy(color = RarimeTheme.colors.invertedDark),
                    accentTitle = stringResource(R.string.rmo),
                    titleModifier =
                        Modifier.sharedBounds(
                            rememberSharedContentState(HomeSharedKeys.title(layoutId)),
                            animatedVisibilityScope = animatedVisibilityScope,
                            boundsTransform = { _, _ -> tween(durationMillis = ANIMATION_DURATION_MS) },
                            resizeMode = SharedTransitionScope.ResizeMode.ScaleToBounds()
                        ),
                    accentTitleStyle = RarimeTheme.typography.additional1.copy(brush = RarimeTheme.colors.gradient13),
                    accentTitleModifier =
                        Modifier.sharedBounds(
                            rememberSharedContentState(
                                HomeSharedKeys.accentTitle(
                                    layoutId
                                )
                            ),
                            animatedVisibilityScope = animatedVisibilityScope,
                            boundsTransform = { _, _ -> tween(durationMillis = ANIMATION_DURATION_MS) },
                            resizeMode = SharedTransitionScope.ResizeMode.ScaleToBounds()
                        ),
                    caption = stringResource(R.string.earn_expanded_widget_caption),
                    captionStyle = RarimeTheme.typography.body4.copy(color = RarimeTheme.colors.textSecondary),
                    captionModifier = Modifier.sharedBounds(
                        rememberSharedContentState(
                            HomeSharedKeys.caption(
                                layoutId
                            )
                        ),
                        animatedVisibilityScope = animatedVisibilityScope,
                        boundsTransform = { _, _ -> tween(durationMillis = ANIMATION_DURATION_MS) },
                        resizeMode = SharedTransitionScope.ResizeMode.ScaleToBounds()
                    ),
                )

            }

        }

    }


}

@Composable
private fun Footer(
    countOfTask: Int,
    onClick: () -> Unit,
    maxValueOfRefferals: Int,
    currentValueOfRefferals: Int
) {
    Column(modifier = Modifier.background(color = RarimeTheme.colors.backgroundSurface1)) {


        HorizontalDivider(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 24.dp, horizontal = 20.dp)
        )
        Column(modifier = Modifier.padding(start = 20.dp, end = 20.dp, bottom = 24.dp)) {
            Text(
                text = (countOfTask.toString() + stringResource(R.string.earn_expanded_widget_active_task_title)),
                style = RarimeTheme.typography.overline2,
                color = RarimeTheme.colors.textSecondary,
                modifier = Modifier.padding(bottom = 24.dp)
            )
            TaskCard(
                taskIconId = R.drawable.ic_user_add_line,
                rewardInRMO = 15,
                title = stringResource(R.string.earn_title_of_task),
                onClick = onClick,
                description = stringResource(R.string.earn_invite_task_card_description),
                currentVal = currentValueOfRefferals,
                maxVal = maxValueOfRefferals,
            )

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
        if (isDark) R.drawable.ic_bg_earn_widget_dark
        else R.drawable.ic_bg_earn_widget_light
    }


    with(sharedTransitionScope) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(color = RarimeTheme.colors.backgroundPrimary)
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

@Preview(showBackground = true)
@Composable
fun EarnExpandedWidgetPreview() {
    PrevireSharedAnimationProvider { sts, avs ->
        EarnExpandedWidgetContent(
            widgetProps = BaseWidgetProps.Expanded(
                onCollapse = {},
                layoutId = WidgetType.EARN.layoutId,
                animatedVisibilityScope = avs,
                sharedTransitionScope = sts
            ),
            modifier = Modifier.height(820.dp),
            innerPaddings = mapOf(ScreenInsets.TOP to 0, ScreenInsets.BOTTOM to 0),
            onClick = {},
            colorScheme = AppColorScheme.LIGHT,
            pointsBalance = 1L,
            maxValueOfReferrals = 5,
            currentValueOfReferrals = 2,
        )
    }
}






