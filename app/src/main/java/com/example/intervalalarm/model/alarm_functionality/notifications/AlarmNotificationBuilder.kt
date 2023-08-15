package com.example.intervalalarm.model.alarm_functionality.notifications

import android.app.Notification
import android.app.PendingIntent
import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.intervalalarm.R

class AlarmNotificationBuilder(private val context: Context) : NotificationCompat.Builder(context) {

    fun getNotificationByType(
        type: NotificationType,
        pendingIntent: PendingIntent,
        title: String?,
        contentText: String?,
        additionalPendingIntent: PendingIntent?

    ): Notification {

        Log.d("notification action button", "got notification from custom builder")
        when (type) {

            NotificationType.RingAlarm -> {
                return NotificationCompat.Builder(
                    context,
                    AlarmNotificationService.CHANNEL_RINGS_ID
                )
                    .setContentTitle(title)
                    .setContentText(contentText)
                    .setContentIntent(pendingIntent)
                    .addAction(
                        androidx.core.R.drawable.notification_action_background,
                        "Turn off",
                        additionalPendingIntent
                    )
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setSound(Uri.parse("android.resource://" + context.packageName + "/" + R.raw.alarm_ringtone))
                    .setTimeoutAfter(60 * 60 * 1000)
                    .setAutoCancel(true)
                    .build()
            }

            NotificationType.RebootNotification -> {

                return NotificationCompat.Builder(context, AlarmNotificationService.CHANNEL_INFO_ID)
                    .setContentTitle("Device reboot!")
                    .setContentText("All of your alarms has been toggled off due to rebooting your device.")
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setTimeoutAfter(60 * 60 * 1000)
                    .setContentIntent(pendingIntent)
                    .build()
            }

            NotificationType.ScheduledAlarmRings -> {
                return NotificationCompat.Builder(
                    context,
                    AlarmNotificationService.CHANNEL_RINGS_ID
                )
                    .setContentTitle(title)
                    .setContentText(contentText)
                    .setContentIntent(pendingIntent)
                    .addAction(
                        androidx.core.R.drawable.notification_action_background,
                        "Turn off",
                        additionalPendingIntent
                    )
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setSound(Uri.parse("android.resource://" + context.packageName + "/" + R.raw.alarm_ringtone))
                    .setTimeoutAfter(60 * 60 * 1000)
                    .setAutoCancel(true)
                    .build()
            }
        }
    }
}
