package com.example.intervalalarm.viewmodel.use_cases

import android.content.Context
import com.example.intervalalarm.model.alarm_functionality.IntervalAlarmManager
import com.example.intervalalarm.model.data.repository.AlarmsRepository
import com.example.intervalalarm.view.screens.home.states.AlarmStatus
import com.example.intervalalarm.view.screens.home.states.AlarmUiState
import javax.inject.Inject

class TriggerAlarmStatusUseCase @Inject constructor(private val repository: AlarmsRepository) {
    suspend operator fun invoke(context: Context, alarm: AlarmUiState) {

        if (alarm.status == AlarmStatus.Enabled) {
            repository.triggerStatus(context = context, id = alarm.id, alarmCount = alarm.count, status = false)
        } else {
            repository.triggerStatus(context = context, id = alarm.id, alarmCount = alarm.count, status = true)
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