package com.example.intervalalarm.model.alarm_functionality

import android.app.AlarmManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.Intent.ACTION_BOOT_COMPLETED
import android.util.Log
import com.example.intervalalarm.model.data.database.AlarmsDAO
import com.example.intervalalarm.model.alarm_functionality.notifications.AlarmNotificationService
import com.example.intervalalarm.model.alarm_functionality.notifications.NotificationType
import com.example.intervalalarm.model.data.repository.AlarmsRepository
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

@AndroidEntryPoint
class IntervalAlarmBroadcastReceiver : BroadcastReceiver() {

    @Inject
    lateinit var repository: AlarmsRepository

    override fun onReceive(context: Context, intent: Intent) {

        val title = intent.getStringExtra("title")
        val description = intent.getStringExtra("description")
        val schedule = intent.getStringExtra("schedule")
        val count = intent.getIntExtra("count", 1)
        val hours = intent.getIntExtra("hours", 0)
        val minutes = intent.getIntExtra("minutes", 0)
        val seconds = intent.getIntExtra("seconds", 0)
        val turnOffAlarmCount = intent.getIntExtra("turn_it_off", 0)

        val notificationService = AlarmNotificationService(context)
        val formattedInterval: String =
            if (hours > 0) "$hours" + "h:" + "$minutes" + "m:" + "$seconds" + "s"
            else if (minutes > 0) "$minutes" + "m:" + "$seconds" + "s" else "$seconds" + "s"


        when {

            title != null && description != null -> {

                if (description.isEmpty()) {

                    notificationService.showNotification(
                        type = NotificationType.RingAlarm,
                        title,
                        "No description",
                        count,
                        formattedInterval
                    )
                    IntervalAlarmManager(context = context).setAlarm(
                        title = title,
                        description = "No description",
                        hours = hours,
                        minutes = minutes,
                        seconds = seconds,
                        curCount = count
                    )
                } else {

                    notificationService.showNotification(
                        type = NotificationType.RingAlarm,
                        title,
                        description,
                        count,
                        formattedInterval
                    )

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

            !schedule.isNullOrEmpty() -> {

                runBlocking(Dispatchers.Default) {
                    repository.triggerStatusByCount(context, count, true)
                    repository.clearScheduleByCount(count)
                }
            }

            turnOffAlarmCount > 0 -> {
                runBlocking(Dispatchers.Default) {
                    repository.triggerStatusByCount(
                        context = context,
                        alarmCount = turnOffAlarmCount,
                        status = false
                    )
                }
            }

            intent.action == ACTION_BOOT_COMPLETED -> {

                runBlocking(Dispatchers.Default) {
                    launch {
                        repository.disableAllAlarms()
                        notificationService.showNotification(
                            type = NotificationType.RebootNotification,
                            null, null, null, null
                        )
                    }
                }
            }

            intent.action == AlarmManager.ACTION_SCHEDULE_EXACT_ALARM_PERMISSION_STATE_CHANGED -> {
              TODO("not necessary")
                Log.d("notification action button", "permission changed")
            }
        }
    }
}