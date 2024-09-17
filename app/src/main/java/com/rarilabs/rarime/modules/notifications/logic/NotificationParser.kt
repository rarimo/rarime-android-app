package com.rarilabs.rarime.modules.notifications.logic

import com.google.gson.Gson
import com.rarilabs.rarime.modules.notifications.models.NotificationRewardContent
import com.rarilabs.rarime.modules.notifications.models.NotificationType
import com.rarilabs.rarime.store.room.notifications.models.NotificationEntityData

fun resolveNotificationType(notificationTypeString: String): NotificationType {
    return when (notificationTypeString) {
        "reward" -> NotificationType.REWARD
        else -> NotificationType.INFO
    }
}

fun parseRewardNotification(notificationEntityData: NotificationEntityData): NotificationRewardContent {
    val response = Gson().fromJson(notificationEntityData.data, NotificationRewardContent::class.java)
    return response
}