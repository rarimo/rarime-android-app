package com.rarilabs.rarime.modules.notifications

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.rarilabs.rarime.R
import com.rarilabs.rarime.modules.notifications.logic.parseRewardNotification
import com.rarilabs.rarime.modules.notifications.logic.resolveNotificationType
import com.rarilabs.rarime.modules.notifications.models.NotificationType
import com.rarilabs.rarime.store.room.notifications.models.NotificationEntityData
import com.rarilabs.rarime.ui.base.BaseIconButton
import com.rarilabs.rarime.ui.components.AppBottomSheet
import com.rarilabs.rarime.ui.components.AppSheetState
import com.rarilabs.rarime.ui.components.PrimaryButton
import com.rarilabs.rarime.ui.components.rememberAppSheetState
import com.rarilabs.rarime.ui.theme.RarimeTheme
import com.rarilabs.rarime.util.DateUtil
import com.rarilabs.rarime.util.ErrorHandler
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId

private enum class ClaimStatus {
    LOADING,
    CLAIMED,
    ALLOWED,
    ERROR,
}

@Composable
fun NotificationItemAppSheetContent(
    item: NotificationEntityData,
    state: AppSheetState = rememberAppSheetState(),
    getPointsReward: suspend (String) -> Unit,
    checkIsRewarded: suspend (String?) -> Boolean
) {

    val scope = rememberCoroutineScope()

    val notificationType = remember {
        resolveNotificationType(item.type.toString())
    }
    val context = LocalContext.current
    Column(
        modifier = Modifier
            .padding(start = 20.dp, end = 20.dp, top = 20.dp)
    ) {
        Row {
            Spacer(modifier = Modifier.weight(1f))
            BaseIconButton(
                onClick = { state.hide() },
                icon = R.drawable.ic_close,
                colors = ButtonDefaults.buttonColors(
                    containerColor = RarimeTheme.colors.componentPrimary,
                    contentColor = RarimeTheme.colors.textPrimary
                ),
            )
        }
        Text(
            text = item.header,
            style = RarimeTheme.typography.h6,
            color = RarimeTheme.colors.textPrimary
        )
        Spacer(modifier = Modifier.height(8.dp))
        val instant = Instant.ofEpochMilli(item.date.toLong())

        val timeStr = LocalDateTime.ofInstant(instant, ZoneId.systemDefault())

        Text(
            text = DateUtil.getDurationString(
                DateUtil.duration(timeStr),
                context
            ) + " " + stringResource(
                id = R.string.time_ago
            )
        )
        Spacer(modifier = Modifier.height(20.dp))
        Text(
            text = item.description,
            style = RarimeTheme.typography.body3,
            color = RarimeTheme.colors.textSecondary
        )
        Spacer(modifier = Modifier.weight(1f))
        if (notificationType == NotificationType.REWARD) {

            val eventData = parseRewardNotification(item)

            var status by remember {
                mutableStateOf(ClaimStatus.LOADING)
            }

            LaunchedEffect(Unit) {
                status = ClaimStatus.LOADING
                try {
                    val res = checkIsRewarded(eventData?.eventName)
                    status = if (res) {
                        ClaimStatus.CLAIMED
                    } else {
                        ClaimStatus.ALLOWED
                    }
                } catch (e: Exception) {
                    ErrorHandler.logError(
                        "checkIsRewarded",
                        "Error during checking rewardStatus",
                        e
                    )
                    status = ClaimStatus.ERROR
                }
            }

            val text = when (status) {
                ClaimStatus.LOADING -> stringResource(id = R.string.notifications_reward_loading)
                ClaimStatus.CLAIMED -> stringResource(id = R.string.notifications_reward_claimed)
                ClaimStatus.ALLOWED -> stringResource(id = R.string.notifications_allowed_claimed)
                ClaimStatus.ERROR -> stringResource(id = R.string.notifications_error)
            }

            PrimaryButton(
                modifier = Modifier.fillMaxWidth(),
                enabled = status == ClaimStatus.ALLOWED,
                text = text,
                onClick = {
                    scope.launch {
                        try {
                            status = ClaimStatus.LOADING
                            val res = getPointsReward(eventData!!.eventName)
                            res
                            status = ClaimStatus.CLAIMED
                        } catch (e: Exception) {
                            status = ClaimStatus.ERROR
                            ErrorHandler.logError("Claim notification error", "cant get points", e)
                        }
                    }
                })
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
fun NotificationItemAppSheet(
    item: NotificationEntityData,
    state: AppSheetState = rememberAppSheetState(),
    viewModel: NotificationAppSheetViewModel = hiltViewModel()
) {

    AppBottomSheet(
        state = state,
        fullScreen = true,
        isHeaderEnabled = false,
    ) {
        NotificationItemAppSheetContent(
            item, state,
            viewModel::claimRewardsEvent,
            viewModel::checkIfRewarded
        )
    }
}

@Preview
@Composable
fun NotificationItemAppSheetPreview() {

    val state = rememberAppSheetState(false)
    val notificationEntityData = NotificationEntityData(
        header = "RMO listed on Binance",
        description = "It is a long established fact that a reader will be distracted by the readable",
        date = "100000",
        isActive = true,
        type = "reward",
        data = null
    )

    Surface(modifier = Modifier.fillMaxSize()) {
        NotificationItemAppSheetContent(notificationEntityData, state, {}) { true }
    }
}