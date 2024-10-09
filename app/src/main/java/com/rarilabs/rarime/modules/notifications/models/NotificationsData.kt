package com.rarilabs.rarime.modules.notifications.models

import com.google.gson.annotations.SerializedName

enum class NotificationType {
    REWARD,
    INFO
}

data class NotificationRewardContent(
    @SerializedName("event_name") val eventName: String,
)
