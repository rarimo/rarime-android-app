package com.rarilabs.rarime.modules.notifications

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import com.rarilabs.rarime.manager.NotificationManager
import com.rarilabs.rarime.store.room.notifications.models.NotificationEntityData
import javax.inject.Inject

@HiltViewModel
class NotificationsViewModel @Inject constructor(
    private val notificationsManager: NotificationManager
) : ViewModel() {

    val notificationsList = notificationsManager.notificationList

    val readNotifications = notificationsManager::readNotifications

    val deleteNotifications =
        notificationsManager::deleteNotifications

    val addNotifications =  notificationsManager::addNotification

}