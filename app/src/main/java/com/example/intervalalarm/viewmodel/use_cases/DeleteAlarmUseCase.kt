package com.example.intervalalarm.viewmodel.use_cases

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.example.intervalalarm.model.data.database.AlarmEntity
import com.example.intervalalarm.model.alarm_functionality.IntervalAlarmBroadcastReceiver
import com.example.intervalalarm.model.alarm_functionality.IntervalAlarmManager
import com.example.intervalalarm.model.data.repository.AlarmsRepository
import javax.inject.Inject

class DeleteAlarmUseCase @Inject constructor(private val repository: AlarmsRepository) {

    suspend operator fun invoke(context: Context, alarm: AlarmEntity){
        repository.deleteAlarm(context = context, alarm = alarm)
    }
}