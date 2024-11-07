package com.rarilabs.rarime.modules.rewards.components

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.absolutePadding
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.rarilabs.rarime.R
import com.rarilabs.rarime.api.points.models.BaseEvents
import com.rarilabs.rarime.api.points.models.PointsEventData
import com.rarilabs.rarime.data.enums.PassportStatus
import com.rarilabs.rarime.modules.rewards.view_models.CONST_MOCKED_EVENTS_LIST
import com.rarilabs.rarime.ui.base.ButtonSize
import com.rarilabs.rarime.ui.components.AppBottomSheet
import com.rarilabs.rarime.ui.components.AppIcon
import com.rarilabs.rarime.ui.components.AppSheetState
import com.rarilabs.rarime.ui.components.AppSkeleton
import com.rarilabs.rarime.ui.components.HorizontalDivider
import com.rarilabs.rarime.ui.components.PrimaryButton
import com.rarilabs.rarime.ui.components.rememberAppSheetState
import com.rarilabs.rarime.ui.theme.RarimeTheme
import com.rarilabs.rarime.util.DateUtil
import com.rarilabs.rarime.util.Screen
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

val ICONS_BY_BASE_EVENT_MAP = mapOf(
    BaseEvents.PASSPORT_SCAN.value to R.drawable.ic_user_focus,
    BaseEvents.REFERRAL_COMMON.value to R.drawable.ic_users,
    BaseEvents.REFERRAL_SPECIFIC.value to R.drawable.ic_user,
    BaseEvents.BE_REFERRED.value to R.drawable.ic_user,
    BaseEvents.FREE_WEEKLY.value to R.drawable.ic_star_four,
    BaseEvents.EARLY_TEST.value to R.drawable.ic_gift
)

@Composable
fun ActiveTaskItem(
    modifier: Modifier = Modifier,
    navigate: (String) -> Unit,
    pointEvent: PointsEventData,
    passportStatus: PassportStatus,
    refreshEvents: suspend () -> Unit,
    claimEvent: suspend (String) -> Unit
) {

    val eventIcon =
        ICONS_BY_BASE_EVENT_MAP[pointEvent.attributes.meta.static.name] ?: R.drawable.ic_users

    val sheetState = rememberAppSheetState()

    fun isEventEnabled(): Boolean {
        if (passportStatus == PassportStatus.NOT_ALLOWED) {
            return false
        }
        if (pointEvent.attributes.meta.static.name == BaseEvents.EARLY_TEST.value) {
            return true
        }
        if (passportStatus == PassportStatus.WAITLIST) {
            return false
        }

        return true
    }

    fun handleBaseEvents() {
        when (pointEvent.attributes.meta.static.name) {
            BaseEvents.PASSPORT_SCAN.value -> {
                when (passportStatus) {
                    PassportStatus.ALLOWED -> {
                        navigate(Screen.Claim.Reserve.route)
                    }

                    else -> {
                        navigate(Screen.Main.Home.route)
                    }
                }
            }

            BaseEvents.EARLY_TEST.value -> {
                sheetState.show()
            }

            else -> {
                navigate(
                    Screen.Main.Rewards.RewardsEventsItem.route.replace(
                        "{item_id}",
                        pointEvent.id,
                    )
                )
            }
        }
    }

    val isEventEnabled = remember {
        isEventEnabled()
    }

    Row(
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .clickable {
                if (isEventEnabled) {
                    handleBaseEvents()
                }
            }
            .alpha(if (isEventEnabled) 1f else 0.25f)
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .clip(RoundedCornerShape(100.dp))
                .width(40.dp)
                .height(40.dp)
                .background(RarimeTheme.colors.baseBlack)
        ) {
            AppIcon(
                id = eventIcon,
                tint = RarimeTheme.colors.baseWhite
            )
        }

        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .absolutePadding(right = 8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Text(
                    text = pointEvent.attributes.meta.static.title,
                    style = RarimeTheme.typography.subtitle4,
                    color = RarimeTheme.colors.textPrimary,
                )

                Text(
                    text = pointEvent.attributes.meta.static.shortDescription,
                    style = RarimeTheme.typography.body4,
                    color = RarimeTheme.colors.textSecondary,
                )
            }

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
//                if (pointsToken?.balanceDetails != null && pointsToken.balanceDetails!!.attributes.is_verified == true) {
//                    RewardAmountPreview(amount = pointEvent.attributes.meta.static.reward)
//                }
                RewardAmountPreview(amount = pointEvent.attributes.meta.static.reward)

                AppIcon(
                    id = R.drawable.ic_caret_right,
                    tint = RarimeTheme.colors.textSecondary
                )
            }
        }



        AppBottomSheet(
            modifier = Modifier.fillMaxHeight(0.5f),
            state = sheetState,
            isHeaderEnabled = false,
        ) {
            EventClaimScreen(pointEvent = pointEvent, appSheetState = sheetState, onClaim = {
                claimEvent(it)
                delay(2000L)
                refreshEvents()
            })
        }
    }
}

@Composable
fun ActiveTaskItemSkeleton() {
    Row(
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        AppSkeleton(
            modifier = Modifier
                .width(40.dp)
                .height(40.dp)
        )

        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                AppSkeleton(
                    modifier = Modifier
                        .width(100.dp)
                        .height(14.dp)
                )
                AppSkeleton(
                    modifier = Modifier
                        .width(140.dp)
                        .height(12.dp)
                )
            }

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                AppSkeleton(
                    modifier = Modifier
                        .width(50.dp)
                        .height(14.dp)
                )
            }
        }
    }
}

@Composable
fun EventClaimScreen(
    modifier: Modifier = Modifier,
    pointEvent: PointsEventData,
    appSheetState: AppSheetState = rememberAppSheetState(),
    onClaim: suspend (String) -> Unit
) {

    val coroutineScope = rememberCoroutineScope()


    var isLoading by remember {
        mutableStateOf(false)
    }

    Column(
        modifier = Modifier
            .fillMaxHeight()
            .background(RarimeTheme.colors.backgroundPrimary),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 20.dp, end = 20.dp, top = 36.dp),
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(0.75f),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = pointEvent.attributes.meta.static.title,
                        style = RarimeTheme.typography.subtitle2,
                        color = RarimeTheme.colors.textPrimary,
                    )

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        RewardAmountPreview(amount = pointEvent.attributes.meta.static.reward)

                        pointEvent.attributes.meta.static.expiresAt?.let {
                            Text(
                                text = DateUtil.formatDateString(it),
                                style = RarimeTheme.typography.caption2,
                                color = RarimeTheme.colors.textSecondary,
                            )
                        }
                    }
                }

                Column(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Image(
                        painter = pointEvent.attributes.meta.static.logo?.let {
                            rememberAsyncImagePainter(it)
                        } ?: painterResource(id = R.drawable.event_stub),
                        contentDescription = "Limited time event",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .width(64.dp)
                            .height(64.dp)
                            .clip(RoundedCornerShape(8.dp)),
                    )
                }
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 24.dp))

            Column(
                modifier = Modifier
                    .verticalScroll(rememberScrollState())
            ) {
                Text(
                    style = RarimeTheme.typography.body3,
                    text = pointEvent.attributes.meta.static.description,
                    color = RarimeTheme.colors.textSecondary
                )
            }
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            HorizontalDivider()

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp, horizontal = 20.dp)
            ) {
                PrimaryButton(
                    modifier = Modifier.fillMaxWidth(),
                    size = ButtonSize.Large,
                    enabled = !isLoading,
                    text = if (!isLoading) stringResource(id = R.string.reserv_btn) else stringResource(
                        id = R.string.wait_btn
                    ),
                    onClick = {
                        isLoading = true
                        try {
                            coroutineScope.launch {
                                onClaim(pointEvent.attributes.meta.static.name)
                                appSheetState.hide()
                            }

                        } catch (e: Exception) {
                            Log.i("Hello", e.toString())
                            isLoading = false
                        }
                    }
                )
            }
        }
    }
}

@Preview
@Composable
private fun EventClaimScreenPreview() {
    EventClaimScreen(pointEvent = CONST_MOCKED_EVENTS_LIST[1]) {}
}

@Preview
@Composable
private fun TimeEventsListPreview() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(RarimeTheme.colors.backgroundPrimary)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        ActiveTaskItem(
            navigate = {},
            pointEvent = CONST_MOCKED_EVENTS_LIST[0],
            passportStatus = PassportStatus.ALLOWED,
            claimEvent = {}, refreshEvents = {}
        )
        ActiveTaskItem(
            navigate = {},
            pointEvent = CONST_MOCKED_EVENTS_LIST[1],
            passportStatus = PassportStatus.ALLOWED,
            claimEvent = {}, refreshEvents = {}
        )
        ActiveTaskItemSkeleton()
    }
}