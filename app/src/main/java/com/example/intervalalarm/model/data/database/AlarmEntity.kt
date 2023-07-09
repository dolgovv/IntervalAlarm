package com.example.intervalalarm.model.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.intervalalarm.view.screens.home.states.AlarmStatus
import com.example.intervalalarm.view.screens.home.states.AlarmUiState
import java.util.*

@Entity(tableName = "alarms_table")
data class AlarmEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),

    val alarmCount: Int,
    val isActive: Boolean,
    val hours: Int = 0,
    val minutes: Int = 0,
    val seconds: Int = 0,

    val title: String,
    val description: String,
    val schedule: String
){
    fun toUiState(): AlarmUiState{
        return AlarmUiState(
            id = id,
            count = alarmCount,
            status = if (isActive) AlarmStatus.Enabled else if (schedule.isNotEmpty()) AlarmStatus.Scheduled else AlarmStatus.Disabled,
            hours, minutes, seconds, title, description, schedule
        )
    }
}