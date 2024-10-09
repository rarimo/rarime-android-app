package com.rarilabs.rarime.store.room.notifications.models

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "notifications")
data class NotificationEntityData(
    @PrimaryKey(autoGenerate = true)
    val notificationId: Long = 0,
    val header: String,
    val description: String,
    val date: String,
    var isActive: Boolean,
    var type: String?,
    var data: String?
)
