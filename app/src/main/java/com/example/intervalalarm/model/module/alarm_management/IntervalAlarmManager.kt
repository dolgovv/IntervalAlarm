package com.example.intervalalarm.model.module.alarm_management

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.widget.Toast
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.*

class IntervalAlarmManager(private val context: Context) {

    private val alarmManager: AlarmManager =
        context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    private val intent = Intent(context, IntervalAlarmBroadcastReceiver::class.java)

    fun setAlarm(
        title: String,
        description: String,
        curCount: Int,
        hours: Int,
        minutes: Int,
        seconds: Int
    ) {

        if (context.checkSelfPermission(android.Manifest.permission.SCHEDULE_EXACT_ALARM) == PackageManager.PERMISSION_GRANTED) {

            val interval: Long =
                (hours.toLong() * 1000 * 60 * 60) + minutes.toLong() * 1000 * 60 + seconds.toLong() * 1000

            intent.putExtra("title", title)
            intent.putExtra("description", description)
            intent.putExtra("count", curCount)
            intent.putExtra("hours", hours)
            intent.putExtra("minutes", minutes)
            intent.putExtra("seconds", seconds)


            val pendingIntent: PendingIntent = PendingIntent.getBroadcast(
                context, curCount, intent,
                PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
            )

            alarmManager.cancel(pendingIntent)
            alarmManager.setExact(
                AlarmManager.RTC_WAKEUP,
                Calendar.getInstance().timeInMillis + interval, pendingIntent
            )
        } else {
            Toast.makeText(context, "Exact alarms permission not provided!", Toast.LENGTH_SHORT)
                .show()
        }
    }

    fun setScheduledAlarm(
        title: String,
        description: String,
        schedule: String,
        curCount: Int,
        hours: Int,
        minutes: Int,
        seconds: Int
    ) {

        if (context.checkSelfPermission(android.Manifest.permission.SCHEDULE_EXACT_ALARM) == PackageManager.PERMISSION_GRANTED) {

            val interval: Long =
                (hours.toLong() * 1000 * 60 * 60) + minutes.toLong() * 1000 * 60 + seconds * 1000
            val scheduleInMillis =
                LocalDateTime.parse(schedule, DateTimeFormatter.ISO_LOCAL_DATE_TIME)
                    .atZone(ZoneId.systemDefault()).toEpochSecond() * 1000

            intent.putExtra("title", title)
            intent.putExtra("description", description)
            intent.putExtra("schedule", schedule)
            intent.putExtra("count", curCount)
            intent.putExtra("hours", hours)
            intent.putExtra("minutes", minutes)
            intent.putExtra("seconds", seconds)

            val pendingIntent: PendingIntent = PendingIntent.getBroadcast(
                context, curCount, intent,
                PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
            )
            alarmManager.cancel(pendingIntent)
            alarmManager.setExact(
                AlarmManager.RTC_WAKEUP,
                scheduleInMillis + interval,
                pendingIntent
            )

        } else {
            Toast.makeText(context, "Exact alarms permission is not provided!", Toast.LENGTH_SHORT)
                .show()
        }
    }

    fun cancelAlarm(pendingIntent: PendingIntent) {
        alarmManager.cancel(pendingIntent)
    }
}