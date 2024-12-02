package com.rarilabs.rarime.modules.notifications

import android.content.Context
import androidx.compose.foundation.layout.Arrangement
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
import com.rarilabs.rarime.modules.notifications.logic.parseUniversalNotification
import com.rarilabs.rarime.modules.notifications.logic.resolveNotificationType
import com.rarilabs.rarime.modules.notifications.models.NotificationType
import com.rarilabs.rarime.store.room.notifications.models.NotificationEntityData
import com.rarilabs.rarime.ui.base.BaseIconButton
import com.rarilabs.rarime.ui.base.ButtonSize
import com.rarilabs.rarime.ui.components.AppBottomSheet
import com.rarilabs.rarime.ui.components.AppSheetState
import com.rarilabs.rarime.ui.components.PrimaryButton
import com.rarilabs.rarime.ui.components.rememberAppSheetState
import com.rarilabs.rarime.ui.theme.RarimeTheme
import com.rarilabs.rarime.util.DateUtil
import com.rarilabs.rarime.util.ErrorHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId

private enum class ClaimStatus {
    LOADING, CLAIMED, ALLOWED, ERROR, NO_BALANCE
}

@Composable
fun NotificationItemAppSheetContent(
    item: NotificationEntityData,
    state: AppSheetState = rememberAppSheetState(),
    getPointsReward: suspend (String) -> Unit,
    checkIsRewarded: suspend (String?) -> Boolean,
    checkBalanceExist: suspend () -> Boolean
) {
    val scope = rememberCoroutineScope()
    val notificationType = remember { resolveNotificationType(item.type.toString()) }
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 20.dp)
    ) {
        HeaderSection(onClose = { state.hide() })
        Spacer(modifier = Modifier.height(16.dp))
        NotificationHeader(text = item.header)
        Spacer(modifier = Modifier.height(8.dp))
        NotificationTimestamp(timestamp = item.date, context = context)
        Spacer(modifier = Modifier.height(20.dp))
        NotificationDescription(text = item.description)
        Spacer(modifier = Modifier.weight(1f))
        NotificationActionSection(
            type = notificationType,
            item = item,
            getPointsReward = getPointsReward,
            checkIsRewarded = checkIsRewarded,
            checkBalanceExist = checkBalanceExist,
            scope = scope
        )
    }
}

@Composable
fun HeaderSection(onClose: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.End
    ) {
        BaseIconButton(
            onClick = onClose,
            icon = R.drawable.ic_close,
            colors = ButtonDefaults.buttonColors(
                containerColor = RarimeTheme.colors.componentPrimary,
                contentColor = RarimeTheme.colors.textPrimary
            )
        )
    }
}

@Composable
fun NotificationHeader(text: String) {
    Text(
        text = text,
        style = RarimeTheme.typography.h6,
        color = RarimeTheme.colors.textPrimary
    )
}

@Composable
fun NotificationTimestamp(timestamp: String, context: Context) {
    val instant = remember(timestamp) { Instant.ofEpochMilli(timestamp.toLong()) }
    val timeStr = remember(instant) {
        LocalDateTime.ofInstant(instant, ZoneId.systemDefault())
    }
    Text(
        text = "${DateUtil.getDurationString(DateUtil.duration(timeStr), context)} ${stringResource(id = R.string.time_ago)}",
        style = RarimeTheme.typography.body2,
        color = RarimeTheme.colors.textSecondary
    )
}

@Composable
fun NotificationDescription(text: String) {
    Text(
        text = text,
        style = RarimeTheme.typography.body3,
        color = RarimeTheme.colors.textSecondary
    )
}

@Composable
fun NotificationActionSection(
    type: NotificationType,
    item: NotificationEntityData,
    getPointsReward: suspend (String) -> Unit,
    checkIsRewarded: suspend (String?) -> Boolean,
    checkBalanceExist: suspend () -> Boolean,
    scope: CoroutineScope
) {
    when (type) {
        NotificationType.REWARD -> {
            val eventData = remember(item) { parseRewardNotification(item) }
            RewardButton(
                eventName = eventData?.eventName,
                getPointsReward = getPointsReward,
                checkIsRewarded = checkIsRewarded,
                scope = scope
            )
        }
        NotificationType.UNIVERSAL -> {
            val eventData = remember(item) { parseUniversalNotification(item) }
            if (!eventData?.eventName.isNullOrEmpty()) {
                UniversalButton(
                    eventName = eventData?.eventName,
                    getPointsReward = getPointsReward,
                    checkIsRewarded = checkIsRewarded,
                    checkBalanceExist = checkBalanceExist,
                    scope = scope
                )
            }
        }
        else -> {
            // Handle other notification types if necessary
        }
    }
}

@Composable
fun RewardButton(
    eventName: String?,
    getPointsReward: suspend (String) -> Unit,
    checkIsRewarded: suspend (String?) -> Boolean,
    scope: CoroutineScope
) {
    var status by remember { mutableStateOf(ClaimStatus.LOADING) }

    LaunchedEffect(eventName) {
        if (eventName != null) {
            status = try {
                if (checkIsRewarded(eventName)) {
                    ClaimStatus.CLAIMED
                } else {
                    ClaimStatus.ALLOWED
                }
            } catch (e: Exception) {
                ErrorHandler.logError("checkIsRewarded", "Error during checking rewardStatus", e)
                ClaimStatus.ERROR
            }
        } else {
            status = ClaimStatus.ERROR
        }
    }

    val buttonText = when (status) {
        ClaimStatus.LOADING -> stringResource(id = R.string.notifications_reward_loading)
        ClaimStatus.CLAIMED -> stringResource(id = R.string.notifications_reward_claimed)
        ClaimStatus.ALLOWED -> stringResource(id = R.string.notifications_allowed_claimed)
        ClaimStatus.ERROR -> stringResource(id = R.string.notifications_error)
        ClaimStatus.NO_BALANCE -> stringResource(id = R.string.notifications_no_active_balance)
    }

    PrimaryButton(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 32.dp),
        size = ButtonSize.Large,
        enabled = status == ClaimStatus.ALLOWED,
        text = buttonText,
        onClick = {
            eventName?.let { name ->
                scope.launch {
                    try {
                        status = ClaimStatus.LOADING
                        getPointsReward(name)
                        status = ClaimStatus.CLAIMED
                    } catch (e: Exception) {
                        status = ClaimStatus.ERROR
                        ErrorHandler.logError("Claim notification error", "Can't get points", e)
                    }
                }
            }
        }
    )
}

@Composable
fun UniversalButton(
    eventName: String?,
    getPointsReward: suspend (String) -> Unit,
    checkIsRewarded: suspend (String?) -> Boolean,
    checkBalanceExist: suspend () -> Boolean,
    scope: CoroutineScope
) {
    var status by remember { mutableStateOf(ClaimStatus.LOADING) }

    LaunchedEffect(eventName) {
        if (eventName != null) {
            status = try {
                if (checkBalanceExist()) {
                    ClaimStatus.NO_BALANCE
                } else {
                    if (checkIsRewarded(eventName)) {
                        ClaimStatus.CLAIMED
                    } else {
                        ClaimStatus.ALLOWED
                    }
                }
            } catch (e: Exception) {
                ErrorHandler.logError("checkIsRewarded", "Error during checking rewardStatus", e)
                ClaimStatus.ERROR
            }
        } else {
            status = ClaimStatus.ERROR
        }
    }

    val buttonText = when (status) {
        ClaimStatus.LOADING -> stringResource(id = R.string.notifications_reward_loading)
        ClaimStatus.CLAIMED -> stringResource(id = R.string.notifications_reward_claimed)
        ClaimStatus.ALLOWED -> stringResource(id = R.string.notifications_allowed_claimed)
        ClaimStatus.ERROR -> stringResource(id = R.string.notifications_error)
        ClaimStatus.NO_BALANCE -> stringResource(id = R.string.notifications_no_active_balance)
    }

    PrimaryButton(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 32.dp),
        enabled = status == ClaimStatus.ALLOWED,
        text = buttonText,
        onClick = {
            eventName?.let { name ->
                scope.launch {
                    try {
                        status = ClaimStatus.LOADING
                        getPointsReward(name)
                        status = ClaimStatus.CLAIMED
                    } catch (e: Exception) {
                        status = ClaimStatus.ERROR
                        ErrorHandler.logError("Claim notification error", "Can't get points", e)
                    }
                }
            }
        }
    )
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
            item,
            state,
            viewModel::claimRewardsEvent,
            viewModel::checkIfRewarded,
            viewModel::balanceExist
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
        NotificationItemAppSheetContent(notificationEntityData, state, {}, { true }) { true }
    }
}