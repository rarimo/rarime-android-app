package com.rarilabs.rarime.services

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.RingtoneManager
import android.net.Uri
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.hilt.work.HiltWorkerFactory
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import dagger.hilt.android.AndroidEntryPoint
import com.rarilabs.rarime.BaseConfig
import com.rarilabs.rarime.MainActivity
import com.rarilabs.rarime.R
import com.rarilabs.rarime.store.room.notifications.models.NotificationEntityData
import java.io.IOException
import java.net.URL
import java.util.Calendar
import javax.inject.Inject
import kotlin.random.Random

@AndroidEntryPoint
class NotificationService :
    FirebaseMessagingService() {
    private var TAG = "MyFirebaseMesaggingService"

    @Inject
    lateinit var notificationManager: com.rarilabs.rarime.manager.NotificationManager

    @Inject
    lateinit var workerFactory: HiltWorkerFactory
    override fun onMessageReceived(message: RemoteMessage) {

        if (message.data.isNotEmpty()) {
            val data = message.data
            Log.d(TAG, "Message Data payload: $data")
            val title = data["title"] ?: message.notification?.title ?: "Notification"

            val description =
                data["description"] ?: message.notification?.body ?: "Notification Description"
            val messageType = data["type"]
            val messageData = data["content"]

            val notificationEntityData = NotificationEntityData(
                notificationId = Random.nextLong(),
                header = title,
                description = description,
                date = Calendar.getInstance().timeInMillis.toString(),
                isActive = true,
                type = messageType,
                data = messageData
            )
            notificationManager.addNotificationSync(notificationEntityData)

            sendNotification(
                description,
                title,
                null
            )
        } else if (message.notification != null) {
            val notificationEntityData = NotificationEntityData(
                notificationId = Random.nextLong(),
                header = message.notification?.title ?: "Notification",
                description = message.notification?.body ?: "Notification Description",
                date = Calendar.getInstance().timeInMillis.toString(),
                isActive = true,
                null,
                null
            )

            // TODO: Doesn't work on Background
            notificationManager.addNotificationSync(notificationEntityData)

            sendNotification(
                message.notification?.body,
                message.notification?.title,
                message.notification?.imageUrl
            )
        }
    }

    override fun onNewToken(token: String) {
        Log.d(TAG, "Refreshed token: $token")
        subscribeToTopic(BaseConfig.GLOBAL_NOTIFICATION_TOPIC)
    }

    private fun sendNotification(messageBody: String?, title: String?, imgUrl: Uri?) {
        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val pendingIntent = PendingIntent.getActivity(
            this, 0 /* Request code */, intent, PendingIntent.FLAG_IMMUTABLE
        )

        var bmp: Bitmap? = null
        if (imgUrl != null) {
            try {
                val `in` = URL(imgUrl.toString()).openStream()
                bmp = BitmapFactory.decodeStream(`in`)
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }

        val channelId = getString(R.string.default_notification_channel_id)
        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

        val notificationBuilder: NotificationCompat.Builder =
            NotificationCompat.Builder(this, channelId)
                .setSmallIcon(R.mipmap.ic_launcher) // Use the default app icon
                .setContentTitle(title)
                .setContentText(messageBody)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent)
                .setPriority(NotificationCompat.PRIORITY_HIGH)

        // Use BigPictureStyle only if the bitmap is not null
        if (bmp != null) {
            notificationBuilder.setStyle(NotificationCompat.BigPictureStyle().bigPicture(bmp))
        }

        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager

        // Since Android Oreo, a notification channel is needed.
        val channel = NotificationChannel(
            channelId, "Channel human readable title", NotificationManager.IMPORTANCE_DEFAULT
        )
        notificationManager.createNotificationChannel(channel)

        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build())
    }

    companion object {

        fun getToken(): String? {
            return FirebaseMessaging.getInstance().token.result
        }

        fun subscribeToRewardableTopic() {
            subscribeToTopic(BaseConfig.REWARD_NOTIFICATION_TOPIC)
        }

        private fun subscribeToTopic(topic: String) {
            Log.d("Subscribed to topic", topic)
            FirebaseMessaging.getInstance().subscribeToTopic(topic).addOnCompleteListener { task ->
                var msg = "Subscribed to topic: $topic"
                if (!task.isSuccessful) {
                    msg = "Subscription to topic failed"
                }
                Log.d("TAG", msg)
            }
        }
    }
}