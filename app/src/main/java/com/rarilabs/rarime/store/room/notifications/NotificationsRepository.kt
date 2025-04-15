package com.rarilabs.rarime.store.room.notifications

import com.rarilabs.rarime.store.room.notifications.models.NotificationEntityData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class NotificationsRepository @Inject constructor(private val notificationDao: NotificationsDao) {

    suspend fun getAllNotifications(): List<NotificationEntityData> {
        return withContext(Dispatchers.IO) {
            notificationDao.getAllNotifications()
        }
    }

    suspend fun insertNotifications(task: NotificationEntityData) {
        return withContext(Dispatchers.IO) {
            notificationDao.insertNotifications(task)
        }
    }

    suspend fun updateNotifications(task: NotificationEntityData) {
        return withContext(Dispatchers.IO) {
            notificationDao.updateNotifications(task)
        }
    }

    suspend fun deleteNotifications(task: NotificationEntityData) {
        return withContext(Dispatchers.IO) {
            notificationDao.deleteNotifications(task)
        }
    }

    suspend fun deleteAllNotifications() {
        withContext(Dispatchers.IO) {
            notificationDao.deleteAll()
        }
    }

    //Blocking

    fun getAllNotificationsSync(): List<NotificationEntityData> {
        return notificationDao.getAllNotificationsSync()
    }

    fun insertNotificationsSync(task: NotificationEntityData) {
        notificationDao.insertNotificationsSync(task)
    }

    fun updateNotificationsSync(task: NotificationEntityData) {
        notificationDao.updateNotificationsSync(task)
    }

    fun deleteNotificationsSync(task: NotificationEntityData) {
        notificationDao.deleteNotificationsSync(task)
    }
}