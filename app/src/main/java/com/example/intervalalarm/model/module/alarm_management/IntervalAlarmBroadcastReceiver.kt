package com.example.intervalalarm.model.module.alarm_management

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.example.intervalalarm.model.module.notifications.AlarmNotificationService
import com.example.intervalalarm.viewmodel.MainViewModel
import java.util.Calendar

class IntervalAlarmBroadcastReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val title = intent.getStringExtra("title")
        val description = intent.getStringExtra("description")
        val schedule = intent.getStringExtra("schedule")
        val count = intent.getIntExtra("count", 1)
        val hours = intent.getIntExtra("hours", 0)
        val minutes = intent.getIntExtra("minutes", 0)
        val seconds = intent.getIntExtra("seconds", 11)

        val notificationService = AlarmNotificationService(context)
        Log.d("problem resolve", "from receiver seconds are ${seconds}")
        Log.d("problem resolve", "from receiver schedule are ${schedule}")
        Log.d("problem resolve", "from receiver title are ${title}")
        Log.d("problem resolve", "from receiver description are ${description}")
        val formattedInterval: String =
            if (hours > 0 && minutes > 0) "$hours" + "h:" + "$minutes" + "m:" + "$seconds" + "s" else if (minutes > 0) "$minutes" + "m:" + "$seconds" + "s" else "$seconds" + "s"


        /**
         *
         *
         * */
        if (title != null && description != null) {
            if (schedule == null) {
                notificationService.showNotification("Alarm $count", "", count, formattedInterval)

                IntervalAlarmManager(context = context).setAlarm(
                    title = title,
                    description = description,
                    hours = hours,
                    minutes = minutes,
                    seconds = seconds,
                    curCount = count
                )
            } else {
                notificationService.showNotification(title, description, count, formattedInterval)

                IntervalAlarmManager(context = context).setAlarm(
                    title = title,
                    description = description,
                    hours = hours,
                    minutes = minutes,
                    seconds = seconds,
                    curCount = count
                )
            }
        }

//        if (title != null && description != null) {
//
//            if (schedule == null) {
//                notificationService.showNotification("Alarm $count", "", count, formattedInterval)
//
//                IntervalAlarmManager(context = context).setAlarm(
//                    title = title,
//                    description = description,
//                    hours = hours,
//                    minutes = minutes,
//                    seconds = seconds,
//                    curCount = count
//                )
//            } else {
//                notificationService.showNotification(title, description, count, formattedInterval)
//
//                IntervalAlarmManager(context = context).setAlarm(
//                    title = title,
//                    description = description,
//                    hours = hours,
//                    minutes = minutes,
//                    seconds = seconds,
//                    curCount = count
//                )
//            }

//        } else {
//            if (schedule == null) {
//                notificationService.showNotification("Alarm $count", "", count, formattedInterval)
//
//                IntervalAlarmManager(context = context).setAlarm(
//                    title = "Alarm $count xdtcfvghbjhnjkmlk",
//                    description = "",
//                    hours = hours,
//                    minutes = minutes,
//                    seconds = seconds,
//                    curCount = count
//                )
//            } else {
//                notificationService.showNotification("Alarm $count", "", count, formattedInterval)
//
//                IntervalAlarmManager(context = context).setAlarm(
//                    title = "Alarm $count",
//                    description = "",
//                    hours = hours,
//                    minutes = minutes,
//                    seconds = seconds,
//                    curCount = count
//                )
//            }
//            Log.d("scheduled alarm maintenance", "title or description == null")
//        }


        if (Intent.ACTION_BOOT_COMPLETED == intent.action) {


//            IntervalAlarmManager(context = context).cancelAlarm(
//                PendingIntent.getBroadcast(
//                    context, count, intent,
//                    PendingIntent.FLAG_IMMUTABLE
//                )
//            )
//            IntervalAlarmManager(context = context).setAlarm(title, description, hours, minutes, seconds, count)
        } else {
            Log.d("alarm management", "")
        }
    }
}