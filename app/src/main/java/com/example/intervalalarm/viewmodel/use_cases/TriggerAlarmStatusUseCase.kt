package com.example.intervalalarm.viewmodel.use_cases

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.example.intervalalarm.model.module.alarm_management.IntervalAlarmBroadcastReceiver
import com.example.intervalalarm.model.module.alarm_management.IntervalAlarmManager
import com.example.intervalalarm.model.repository.AlarmsRepository
import com.example.intervalalarm.view.screens.home.states.AlarmStatus
import com.example.intervalalarm.view.screens.home.states.AlarmUiState
import javax.inject.Inject

class TriggerAlarmStatusUseCase @Inject constructor(private val repository: AlarmsRepository) {
    suspend operator fun invoke(context: Context, alarm: AlarmUiState) {
        val intent = Intent(context, IntervalAlarmBroadcastReceiver::class.java)

        val pendingIntent = PendingIntent.getBroadcast(
            context, alarm.count, intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        if (alarm.status == AlarmStatus.Enabled) {
            repository.triggerStatus(id = alarm.id, status = false)
            IntervalAlarmManager(context).cancelAlarm(pendingIntent)
        } else {
            repository.triggerStatus(id = alarm.id, status = true)
            IntervalAlarmManager(context).setAlarm(
                alarm.title,
                alarm.description,
                alarm.count,
                alarm.hours,
                alarm.minutes,
                alarm.seconds
            )
        }
    }
}