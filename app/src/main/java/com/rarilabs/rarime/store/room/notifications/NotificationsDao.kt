package com.rarilabs.rarime.store.room.notifications

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.rarilabs.rarime.store.room.notifications.models.NotificationEntityData

@Dao
interface NotificationsDao {

    @Query("SELECT * FROM notifications")
    suspend fun getAllNotifications(): List<NotificationEntityData>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotifications(task: NotificationEntityData)

    @Update
    suspend fun updateNotifications(task: NotificationEntityData)

    @Delete
    suspend fun deleteNotifications(task: NotificationEntityData)

    @Query("DELETE FROM notifications")
    suspend fun deleteAll()


    @Query("SELECT * FROM notifications")
    fun getAllNotificationsSync(): List<NotificationEntityData>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertNotificationsSync(task: NotificationEntityData)

    @Update
    fun updateNotificationsSync(task: NotificationEntityData)

    @Delete
    fun deleteNotificationsSync(task: NotificationEntityData)

    @Query("DELETE FROM notifications")
    suspend fun deleteAllSync()

}