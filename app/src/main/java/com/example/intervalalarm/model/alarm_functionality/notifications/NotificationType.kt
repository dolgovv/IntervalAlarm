package com.example.intervalalarm.model.alarm_functionality.notifications

sealed class NotificationType {
    object RingAlarm : NotificationType()
    object RebootNotification : NotificationType()
    object ScheduledAlarmRings : NotificationType()
}