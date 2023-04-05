package com.example.intervalalarm.model.module.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.net.Uri
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.intervalalarm.MainActivity
import com.example.intervalalarm.R

class AlarmNotificationService(
    private val context: Context
) {
    companion object {
        const val CHANNEL_RINGS_ID = "alarm_rings_channel"
    }

    private val notificationManager: NotificationManager =
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

//    private val defRing = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
//    private val ringtoneManager = RingtoneManager.getRingtone(context, defRing)
    private val activityIntent = Intent(context, MainActivity::class.java)

    fun createChannel(channel: NotificationChannel) {
        notificationManager.createNotificationChannel(channel)
    }
    fun deleteChannel(channel: NotificationChannel){
        notificationManager.deleteNotificationChannel(channel.id)
    }

    fun showNotification(title: String, description: String, alarmCount: Int, formattedInterval: String) {

        activityIntent.putExtra("from_notification", alarmCount)
        val activityPendingIntent = PendingIntent.getActivity(
            context,
            alarmCount,
            activityIntent,
            PendingIntent.FLAG_IMMUTABLE
        )
        val notification = NotificationCompat.Builder(context, CHANNEL_RINGS_ID)
            .setContentTitle("$title is ringing!")
            .setContentText("Current interval is $formattedInterval. $description")
            .setSmallIcon(R.drawable.interval_alarm_logo_24)
            .setContentIntent(activityPendingIntent)
            .setSound(Uri.parse("android.resource://"+ context.packageName + "/" + R.raw.alarm_ringtone))
            .build()

        Log.d("notify management", "service notified")

        notificationManager.notify(alarmCount, notification)
    }
}