package com.example.intervalalarm.view.screens.home.states

import com.example.intervalalarm.model.database.AlarmEntity

data class AlarmUiState(
    val id: String = "",
    val count: Int = 1,
    val status: AlarmStatus = AlarmStatus.Disabled,
    val hours: Int = 0, //or LONG
    val minutes: Int = 0, //or LONG
    val seconds: Int = 0,
    val title: String = "",
    val description: String = "",
    val schedule: String = ""
){
    fun toEntity(): AlarmEntity {
        return AlarmEntity(
            id = id,
            alarmCount = count,
            isActive = status == AlarmStatus.Enabled,
            hours = hours,
            minutes = minutes,
            seconds = seconds,
            title = title,
            description = description,
            schedule = schedule
        )
    }
}

sealed class AlarmStatus {
    object Enabled : AlarmStatus()
    object Disabled : AlarmStatus()
    object Scheduled : AlarmStatus()
}