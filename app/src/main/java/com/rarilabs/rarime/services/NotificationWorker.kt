package com.rarilabs.rarime.services

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import com.rarilabs.rarime.manager.NotificationManager
import com.rarilabs.rarime.store.room.notifications.models.NotificationEntityData
import java.util.Calendar

// TODO: Doesn't work with hilt

internal const val NotificationWorkerName = "ShareDatabaseWork"

@HiltWorker
class NotificationWorker @AssistedInject constructor(
    @Assisted val context: Context,
    @Assisted val workerParams: WorkerParameters,
    private val notificationManager: NotificationManager
) : CoroutineWorker(context, workerParams) {
    override suspend fun doWork(): Result {
        return withContext(Dispatchers.IO) {
            val title = inputData.getString("title") ?: return@withContext Result.failure()
            val description = inputData.getString("description") ?: return@withContext Result.failure()

            // Create the notification data entity
            val notificationEntityData = NotificationEntityData(
                header = title,
                description = description,
                date = Calendar.getInstance().timeInMillis.toString(),
                isActive = true,
                type = null,
                data = null
            )
            try {

                notificationManager.addNotification(notificationEntityData)
            }catch (e: Exception) {
                Log.i("CoroutineWorker", "smt wrong with worker", e)
                Result.failure()
            }

            Result.success()
        }

    }

    companion object {
        fun enqueueUniqueWork(context: Context, notificationEntityData: NotificationEntityData) =
            WorkManager.getInstance(context).apply {
                enqueueUniqueWork(
                    NotificationWorkerName,
                    ExistingWorkPolicy.APPEND,
                    startupDatabaseShare(notificationEntityData)
                )
            }

        private fun startupDatabaseShare(notificationEntityData: NotificationEntityData) = OneTimeWorkRequestBuilder<NotificationWorker>()
            .setInputData(
                workDataOf(
                    "title" to (notificationEntityData.header),
                    "description" to (notificationEntityData.description)
                )
            )
            .build()
    }
}