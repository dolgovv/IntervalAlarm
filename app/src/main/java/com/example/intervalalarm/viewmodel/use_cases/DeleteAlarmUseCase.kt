package com.example.intervalalarm.viewmodel.use_cases

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.example.intervalalarm.model.database.AlarmEntity
import com.example.intervalalarm.model.module.alarm_management.IntervalAlarmBroadcastReceiver
import com.example.intervalalarm.model.module.alarm_management.IntervalAlarmManager
import com.example.intervalalarm.model.repository.AlarmsRepository
import javax.inject.Inject

class DeleteAlarmUseCase @Inject constructor(private val repository: AlarmsRepository) {

    suspend operator fun invoke(context: Context, alarm: AlarmEntity){
        repository.deleteAlarm(alarm = alarm)
        val intent = Intent(context, IntervalAlarmBroadcastReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context, alarm.alarmCount, intent,
            PendingIntent.FLAG_IMMUTABLE
        )
        IntervalAlarmManager(context).cancelAlarm(pendingIntent)
    }
}