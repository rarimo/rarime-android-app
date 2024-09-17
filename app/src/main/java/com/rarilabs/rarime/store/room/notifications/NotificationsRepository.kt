package com.rarilabs.rarime.store.room.notifications

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import com.rarilabs.rarime.store.room.notifications.models.NotificationEntityData
import javax.inject.Inject

class NotificationsRepository @Inject constructor(private val taskDao: NotificationsDao) {

    suspend fun getAllNotifications(): List<NotificationEntityData> {
        return withContext(Dispatchers.IO) {
            taskDao.getAllNotifications()
        }
    }

    suspend fun insertNotifications(task: NotificationEntityData) {
        return withContext(Dispatchers.IO) {
            taskDao.insertNotifications(task)
        }
    }

    suspend fun updateNotifications(task: NotificationEntityData) {
        return withContext(Dispatchers.IO) {
            taskDao.updateNotifications(task)
        }
    }

    suspend fun deleteNotifications(task: NotificationEntityData) {
        return withContext(Dispatchers.IO) {
            taskDao.deleteNotifications(task)
        }
    }

    //Blocking

    fun getAllNotificationsSync(): List<NotificationEntityData> {
        return taskDao.getAllNotificationsSync()
    }

    fun insertNotificationsSync(task: NotificationEntityData) {
        taskDao.insertNotificationsSync(task)
    }

    fun updateNotificationsSync(task: NotificationEntityData) {
        taskDao.updateNotificationsSync(task)
    }

    fun deleteNotificationsSync(task: NotificationEntityData) {
        taskDao.deleteNotificationsSync(task)
    }
}