package com.paulik8.maptracker.services.notification

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.paulik8.maptracker.R
import com.paulik8.maptracker.services.location.LocationService
import com.paulik8.maptracker.worker_main.WorkerMainActivity

class NotificationFirebaseService : FirebaseMessagingService() {


    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        val intent = Intent(applicationContext, WorkerMainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
        createNotificationChannel()
        val pendingIntent = PendingIntent.getActivity(applicationContext, NOTIFICATION_ID, intent, 0)
        val notificationManager: NotificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val notificationBuilder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.notification_small_icon)
            .setContentTitle(message.data["title"])
            .setContentText(message.data["message"])
            .setContentIntent(pendingIntent)
            .setDefaults(Notification.DEFAULT_SOUND)
            .setLights(Color.LTGRAY, 1, 1)
            .setAutoCancel(true)
            .setStyle(NotificationCompat.BigTextStyle()
                .bigText(message.data["message"]))
            .setPriority(NotificationCompat.PRIORITY_MAX)

        notificationManager.notify(NOTIFICATION_ID, notificationBuilder.build())

//        val locationService = LocationService(applicationContext).checkPermissions()

    }

    override fun onNewToken(newToken: String) {
        super.onNewToken(newToken)
        sendTokenToServer(newToken)
    }

    private fun sendTokenToServer(token: String) {

    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = getString(R.string.notification_channel_name)
            val descriptionText = getString(R.string.notification_channel_description)
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
                enableLights(true)
            }
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    companion object {
        private const val CHANNEL_ID = "101"
        private const val NOTIFICATION_ID = 777
        private const val CONTENT_TITLE = "NotificationTitle"
        private const val CONTENT_TEXT = "NotificationText"
    }

}