package com.example.intervalalarm.model.module.alarm_management

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.*

class IntervalAlarmManager(private val context: Context) {

    private val alarmManager: AlarmManager =
        context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    fun setAlarm(
        title: String,
        description: String,
        curCount: Int,
        hours: Int,
        minutes: Int,
        seconds: Int
    ){
        val interval: Long = (hours.toLong() * 1000 *  60 * 60) + minutes.toLong() * 1000 * 60 + seconds * 1000
        val intent = Intent(context, IntervalAlarmBroadcastReceiver::class.java)

        intent.putExtra("title", title)
        intent.putExtra("description", description)
        intent.putExtra("count", curCount)
        intent.putExtra("hours", hours)
        intent.putExtra("minutes", minutes)
        intent.putExtra("seconds", seconds)

        val pendingIntent: PendingIntent = PendingIntent.getBroadcast(context, curCount, intent,
        PendingIntent.FLAG_IMMUTABLE) //TODO check if its working fine
        alarmManager.cancel(pendingIntent)
        alarmManager.setExact(AlarmManager.RTC_WAKEUP,
            Calendar.getInstance().timeInMillis + interval, pendingIntent)
//        Log.d("problem resolve", "from manager seconds are ${seconds}")
        Log.d("big problem resolve", "from manager count is ${curCount}")
        Log.d("alarm management", "from manager: ${intent.extras?.get("count")}")
    }

    fun setScheduledAlarm(
        title: String,
        description: String,
        schedule: String,
        hours: Int,
        minutes: Int,
        seconds: Int,
        curCount: Int
    ){

        Log.d("date time format", "sch from manager is $schedule")
        val interval: Long = (hours.toLong() * 1000 *  60 * 60) + minutes.toLong() * 1000 * 60 + seconds * 1000
        val eke = LocalDateTime.parse(schedule, DateTimeFormatter.ISO_LOCAL_DATE_TIME).atZone(ZoneId.systemDefault()).toEpochSecond() * 1000
        val intent = Intent(context, IntervalAlarmBroadcastReceiver::class.java)

        intent.putExtra("title", title)
        intent.putExtra("description", description)
        intent.putExtra("count", curCount)
        intent.putExtra("hours", hours)
        intent.putExtra("minutes", minutes)
        intent.putExtra("seconds", seconds)
        intent.putExtra("schedule", schedule)

        val pendingIntent: PendingIntent = PendingIntent.getBroadcast(context, curCount, intent,
            PendingIntent.FLAG_IMMUTABLE) //TODO check if its working fine
        alarmManager.cancel(pendingIntent)
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, eke + interval, pendingIntent)
        Log.d("date time format", "$eke and $interval")
    }

    fun cancelAlarm(pendingIntent: PendingIntent) { alarmManager.cancel(pendingIntent) }
}