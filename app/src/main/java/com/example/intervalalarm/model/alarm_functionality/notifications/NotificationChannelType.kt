package com.example.intervalalarm.model.alarm_functionality.notifications

sealed class NotificationChannelType {
    object InfoChannel : NotificationChannelType()
    object AlarmChannel : NotificationChannelType()
}