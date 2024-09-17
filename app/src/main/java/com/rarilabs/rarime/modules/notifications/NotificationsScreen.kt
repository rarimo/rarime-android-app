package com.rarilabs.rarime.modules.notifications

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxState
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import kotlinx.coroutines.launch
import com.rarilabs.rarime.BuildConfig
import com.rarilabs.rarime.R
import com.rarilabs.rarime.store.room.notifications.models.NotificationEntityData
import com.rarilabs.rarime.ui.components.AppIcon
import com.rarilabs.rarime.ui.components.HorizontalDivider
import com.rarilabs.rarime.ui.components.rememberAppSheetState
import com.rarilabs.rarime.ui.theme.RarimeTheme
import com.rarilabs.rarime.util.DateUtil
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId

@Composable
fun NotificationsScreen(
    onBack: () -> Unit,
    notificationsViewModel: NotificationsViewModel = hiltViewModel()
) {
    val notifications by notificationsViewModel.notificationsList.collectAsState()

    var selectedNotification: NotificationEntityData? by remember {
        mutableStateOf(null)
    }

    val state = rememberAppSheetState()


    selectedNotification?.let {
        NotificationItemAppSheet(it, state)
    }
    NotificationScreenContent(notifications,
        deleteNotification = { it -> notificationsViewModel.deleteNotifications(it) },
        readNotification = { it ->
            notificationsViewModel.readNotifications(it)
            selectedNotification = it
            state.show()
        },
        addNotification = { it -> notificationsViewModel.addNotifications(it) },
        onBack = { onBack.invoke() }
    )
}

@Composable
fun NotificationScreenContent(
    notificationsList: List<NotificationEntityData>,
    onBack: () -> Unit,
    readNotification: suspend (NotificationEntityData) -> Unit,
    deleteNotification: suspend (NotificationEntityData) -> Unit,
    addNotification: suspend (NotificationEntityData) -> Unit
) {

    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(RarimeTheme.colors.backgroundPrimary)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 20.dp, start = 20.dp, end = 20.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween

        ) {
            IconButton(onClick = { onBack.invoke() }) {
                AppIcon(
                    id = R.drawable.ic_arrow_left,
                    modifier = Modifier.width(20.dp),
                    tint = RarimeTheme.colors.textPrimary
                )
            }
            Text(
                text = "Notifications",
                style = RarimeTheme.typography.subtitle4,
                color = RarimeTheme.colors.textPrimary
            )

            if (BuildConfig.isTestnet) {
                IconButton(onClick = {
                    scope.launch {
                        addNotification.invoke(
                            NotificationEntityData(
                                header = "RMO listed on Binance",
                                description = "It is a long established fact that a reader will be distracted by the readable",
                                date = "1723466049",
                                isActive = true,
                                type = null,
                                data = null
                            )
                        )
                    }
                }) {
                    AppIcon(id = R.drawable.ic_plus, modifier = Modifier.width(20.dp))
                }
            } else {
                Spacer(modifier = Modifier.width(40.dp))
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (notificationsList.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(
                    text = stringResource(id = R.string.no_notifications),
                    style = RarimeTheme.typography.h6,
                    color = RarimeTheme.colors.textPrimary
                )
            }
        } else {
            LazyColumn {
                itemsIndexed(
                    items = notificationsList.sortedByDescending { it.date.toLong() },
                    key = { _, item -> item.notificationId }) { index, item ->
                    val isLast = (index < notificationsList.size - 1)
                    NotificationItem(item, isLast, onClick = {
                        scope.launch {
                            readNotification(item)
                        }
                    }, onDismiss = {
                        scope.launch {
                            deleteNotification(item)
                        }
                    })
                }

            }
        }


    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DismissBackground(dismissState: SwipeToDismissBoxState) {
    val color = when (dismissState.dismissDirection) {
        SwipeToDismissBoxValue.EndToStart -> RarimeTheme.colors.errorMain
        SwipeToDismissBoxValue.Settled -> Color.Transparent
        else -> Color.Transparent
    }
    Row(
        modifier = Modifier
            .fillMaxSize()
            .background(color)
            .padding(27.dp, 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Spacer(modifier = Modifier)
        AppIcon(
            id = R.drawable.ic_trash_simple, tint = RarimeTheme.colors.baseWhite
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationItem(
    item: NotificationEntityData,
    isLastItem: Boolean = false,
    onClick: (NotificationEntityData) -> Unit,
    onDismiss: (NotificationEntityData) -> Unit
) {
    val context = LocalContext.current
    val currentItem by rememberUpdatedState(item)

    val dismissState = rememberSwipeToDismissBoxState(confirmValueChange = {
        when (it) {
            SwipeToDismissBoxValue.StartToEnd -> false
            SwipeToDismissBoxValue.EndToStart -> {
                onDismiss(currentItem)
                true
            }

            SwipeToDismissBoxValue.Settled -> false
        }
    })

    SwipeToDismissBox(
        state = dismissState,
        backgroundContent = { DismissBackground(dismissState = dismissState) }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(RarimeTheme.colors.backgroundPrimary)
                .padding(horizontal = 20.dp)
                .clickable { onClick(currentItem) }
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = currentItem.header,
                    style = RarimeTheme.typography.body3,
                    color = RarimeTheme.colors.textPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )

                Spacer(modifier = Modifier.weight(1f))

                if (currentItem.isActive) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(RoundedCornerShape(20.dp))
                            .background(RarimeTheme.colors.primaryMain)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                }

                val instant = Instant.ofEpochMilli(currentItem.date.toLong())
                val timeStr = LocalDateTime.ofInstant(instant, ZoneId.systemDefault())


                Text(
                    text = DateUtil.getDurationString(
                        DateUtil.duration(timeStr),
                        context
                    ) + " " + stringResource(
                        id = R.string.time_ago
                    ),
                    style = RarimeTheme.typography.caption3,
                    color = if (currentItem.isActive) RarimeTheme.colors.primaryMain else RarimeTheme.colors.textSecondary
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = currentItem.description,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                style = RarimeTheme.typography.body3,
                color = RarimeTheme.colors.textSecondary
            )

            if (isLastItem) {
                Spacer(modifier = Modifier.height(16.dp))
                HorizontalDivider()
            }
        }
    }
}

@Preview
@Composable
private fun NotificationsScreenPreview() {
    val notificationList = listOf(
        NotificationEntityData(
            notificationId = 0,
            header = "RMO listed on Binance",
            description = "It is a long established fact that a reader will be distracted by the readable",
            date = "172147176",
            isActive = true,
            null,
            null

        ), NotificationEntityData(
            notificationId = 1,
            header = "RMO listed on Binance",
            description = "It is a long established fact that a reader will be distracted by the readable",
            date = "172347176",
            isActive = false,
            null,
            null

        ), NotificationEntityData(
            notificationId = 2,
            header = "RMO listed on Binance",
            description = "It is a long established fact that a reader will be distracted by the readable",
            date = "172347176",
            isActive = false,
            null,
            null

        ), NotificationEntityData(
            notificationId = 3,
            header = "RMO listed on Binance",
            description = "It is a long established fact that a reader will be distracted by the readable",
            date = "162347176",
            isActive = false,

            null,
            null
        )
    )

    NotificationScreenContent(listOf(), {}, {}, {}) {}
}