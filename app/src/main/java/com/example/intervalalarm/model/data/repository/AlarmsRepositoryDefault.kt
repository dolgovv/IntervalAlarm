package com.example.intervalalarm.model.data.repository

import android.content.Context
import com.example.intervalalarm.model.data.database.AlarmEntity
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow

interface AlarmsRepositoryDefault {

    //HOME SCREEN

    fun getAllAlarms(): Flow<List<AlarmEntity>>

    suspend fun triggerStatus(context: Context, alarm: AlarmEntity)

    suspend fun triggerStatusByCount(context: Context, alarmCount: Int, status: Boolean)

    suspend fun clearSchedule(id: String)

    suspend fun clearScheduleByCount(count: Int)

    suspend fun disableAllAlarms()

    suspend fun haveEnabledAlarms(): Boolean

    /** NEW ALARM SCREEN */

    suspend fun addAlarm(alarm: AlarmEntity)

    /** DETAILS SCREEN */

    suspend fun deleteAlarm(context: Context, alarm: AlarmEntity)

    suspend fun updateTitle(id: String, title: String)

    suspend fun updateDescription(id: String, description: String)

    suspend fun updateSchedule(id: String, schedule: String)

    suspend fun saveHour(id: String, newHour: Int)

    suspend fun saveMinute(id: String, newMinute: Int)

    suspend fun saveSecond(id: String, newSecond: Int)
}