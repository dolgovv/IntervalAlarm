package com.example.intervalalarm.model.module.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.widget.Toast
import androidx.core.app.NotificationCompat
import com.example.intervalalarm.MainActivity
import com.example.intervalalarm.R

class AlarmNotificationService(
    private val context: Context
) {
    companion object {
        const val CHANNEL_RINGS_ID = "alarm_rings_channel"
        const val CHANNEL_INFO_ID = "reboot_channel"
        const val REBOOT_NOTIFICATION = 74183
    }

    private val notificationManager: NotificationManager =
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    private val activityIntent = Intent(context, MainActivity::class.java)

    fun createChannel(channel: NotificationChannel) {
        notificationManager.createNotificationChannel(channel)
    }

    fun deleteChannel(channel: NotificationChannel) {
        notificationManager.deleteNotificationChannel(channel.id)
    }

    fun showNotification(
        type: NotificationType,
        title: String?,
        description: String?,
        alarmCount: Int?,
        formattedInterval: String?
    ) {


        if (context.checkSelfPermission(android.Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {

            when (type) {

                NotificationType.RebootNotification -> {
                    rebootNotification()
                }

                NotificationType.RingAlarm -> {
                    if (title != null && description != null && alarmCount != null && formattedInterval != null) {

                        ringAlarmNotification(
                            title = title,
                            description = description,
                            alarmCount = alarmCount,
                            formattedInterval = formattedInterval
                        )
                    }
                }
            }

        } else {
            Toast.makeText(context, "Notification permission not provided!", Toast.LENGTH_SHORT)
                .show()
        }
    }

    private fun rebootNotification() {
        val activityPendingIntent = PendingIntent.getActivity(
            context,
            REBOOT_NOTIFICATION,
            activityIntent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
        val notification = NotificationCompat.Builder(context, CHANNEL_INFO_ID)
            .setContentTitle("Device reboot!")
            .setContentText("All of your alarms has been toggled off due to rebooting your device.")
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentIntent(activityPendingIntent)
            .build()
        notificationManager.notify(REBOOT_NOTIFICATION, notification)
    }

    private fun ringAlarmNotification(
        title: String,
        description: String,
        alarmCount: Int,
        formattedInterval: String
    ) {
        activityIntent.putExtra("from_notification", alarmCount)

        val activityPendingIntent = PendingIntent.getActivity(
            context,
            alarmCount,
            activityIntent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
        val notification = NotificationCompat.Builder(context, CHANNEL_RINGS_ID)
            .setContentTitle("$title is ringing!")
            .setContentText("Current interval is $formattedInterval. $description")
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentIntent(activityPendingIntent)
            .setSound(Uri.parse("android.resource://" + context.packageName + "/" + R.raw.alarm_ringtone))
            .build()

        notificationManager.notify(alarmCount, notification)
    }
}

sealed class NotificationType {
    object RingAlarm : NotificationType()
    object RebootNotification : NotificationType()
}