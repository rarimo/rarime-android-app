package com.rarilabs.rarime.manager

import com.rarilabs.rarime.store.room.notifications.NotificationsRepository
import com.rarilabs.rarime.store.room.notifications.models.NotificationEntityData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.runBlocking
import javax.inject.Inject


class NotificationManager @Inject constructor(
    private val notificationsRepository: NotificationsRepository,
    private val passportManager: PassportManager
) {
    private val _notificationsList = MutableStateFlow<List<NotificationEntityData>>(emptyList())


    val notificationList: StateFlow<List<NotificationEntityData>>
        get() = _notificationsList.asStateFlow()

    suspend fun readNotifications(notificationEntityData: NotificationEntityData) {
        if (notificationEntityData.isActive) {
            val updatedNotification = notificationEntityData.copy(isActive = false)
            notificationsRepository.updateNotifications(updatedNotification)
            _notificationsList.value = notificationsRepository.getAllNotifications()
        }
    }

    suspend fun deleteNotifications(notificationEntityData: NotificationEntityData) {
        notificationsRepository.deleteNotifications(notificationEntityData)
        _notificationsList.value = notificationsRepository.getAllNotifications()
    }

    suspend fun addNotification(notificationEntityData: NotificationEntityData) {
        notificationsRepository.insertNotifications(notificationEntityData)
        _notificationsList.value = notificationsRepository.getAllNotifications()
    }

    suspend fun loadNotifications() {
        _notificationsList.value = notificationsRepository.getAllNotifications()
    }

    fun getNationality(): String? =
        passportManager.passport.value?.personDetails?.nationality

    fun resolvePassportCircuitName(): String? =
        passportManager.passport.value?.getRegisterIdentityCircuitType()?.buildName()

    fun getPassportStatus() = passportManager.passportStatus.value

    //BLocking
    fun readNotificationsSync(notificationEntityData: NotificationEntityData) {
        runBlocking {
            if (notificationEntityData.isActive) {
                val updatedNotification = notificationEntityData.copy(isActive = false)
                notificationsRepository.updateNotifications(updatedNotification)
                _notificationsList.value = notificationsRepository.getAllNotifications()
            }
        }
    }

    fun deleteNotificationsSync(notificationEntityData: NotificationEntityData) {
        runBlocking {
            notificationsRepository.deleteNotifications(notificationEntityData)
            _notificationsList.value = notificationsRepository.getAllNotifications()
        }
    }

    fun addNotificationSync(notificationEntityData: NotificationEntityData) {
        runBlocking {
            notificationsRepository.insertNotifications(notificationEntityData)
            _notificationsList.value = notificationsRepository.getAllNotifications()
        }
    }

    fun loadNotificationsSync() {
        runBlocking {
            _notificationsList.value = notificationsRepository.getAllNotifications()
        }
    }
}