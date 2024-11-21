package com.rarilabs.rarime.modules.notifications.models

import com.google.gson.annotations.SerializedName

enum class NotificationType {
    REWARD,
    INFO,
    UNIVERSAL
}

data class NotificationRewardContent(
    @SerializedName("event_name") val eventName: String,
)

//Some universal notification can be rewardable
data class NotificationUniversalContent(
    @SerializedName("event_type") val eventName: String
)
