package com.example.intervalalarm.model.alarm_functionality.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.core.app.NotificationCompat
import com.example.intervalalarm.MainActivity
import com.example.intervalalarm.R
import com.example.intervalalarm.model.alarm_functionality.IntervalAlarmBroadcastReceiver

class AlarmNotificationService(
    private val context: Context
) {
    companion object {
        const val CHANNEL_RINGS_ID = "alarm_rings_channel"
        const val CHANNEL_INFO_ID = "reboot_channel"
        const val REBOOT_NOTIFICATION = 74183
        const val ACTIVITY_INTENT = 12345678
        const val TURN_OFF_INTENT = 98765321
    }

    private val notificationManager: NotificationManager =
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    private val activityIntent = Intent(context, MainActivity::class.java)
    private val turnOffIntent = Intent(context, IntervalAlarmBroadcastReceiver::class.java)

    fun showNotification(
        type: NotificationType,
        title: String?,
        description: String?,
        alarmCount: Int?,
        formattedInterval: String?
    ) {

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
                } else {
                    Log.d("package:mine", "NOTIFICATION FAILED TO PROCEED IN AlarmNotificationService")
                }
            }
        }
    }

    private fun rebootNotification() {
        val activityPendingIntent = PendingIntent.getActivity(
            context,
            REBOOT_NOTIFICATION,
            activityIntent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
        notificationManager.notify(
            REBOOT_NOTIFICATION,
            AlarmNotificationBuilder(context).getNotificationByType(
                NotificationType.RebootNotification,
                activityPendingIntent,
                null, null, null
            )
        )
    }

    private fun ringAlarmNotification(
        title: String,
        description: String,
        alarmCount: Int,
        formattedInterval: String
    ) {
        activityIntent.putExtra("from_notification", alarmCount)
        turnOffIntent.putExtra("turn_it_off", alarmCount)

        val activityPendingIntent = PendingIntent.getActivity(
            context,
            ACTIVITY_INTENT,
            activityIntent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val turnOffPendingIntent = PendingIntent.getBroadcast(
            context,
            TURN_OFF_INTENT,
            turnOffIntent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        notificationManager.notify(
            alarmCount,
            AlarmNotificationBuilder(context).getNotificationByType(
                NotificationType.RingAlarm,
                title = "$title is ringing!",
                contentText = "Current interval is $formattedInterval. $description",
                pendingIntent = activityPendingIntent,
                additionalPendingIntent = turnOffPendingIntent
            )
        )
    }

    fun createChannel(channel: NotificationChannel) {
        notificationManager.createNotificationChannel(channel)
    }

    fun deleteChannel(channel: NotificationChannel) {
        notificationManager.deleteNotificationChannel(channel.id)
    }
}

sealed class NotificationType {
    object RingAlarm : NotificationType()
    object RebootNotification : NotificationType()
}