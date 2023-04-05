package com.example.intervalalarm.model.repository

import android.util.Log
import androidx.annotation.WorkerThread
import com.example.intervalalarm.model.database.AlarmEntity
import com.example.intervalalarm.model.database.AlarmsDAO
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.withContext

class AlarmsRepository(private val dao: AlarmsDAO) {

    val allAlarms: Flow<List<AlarmEntity>> = dao.getAlarmsCounted()
    val enabledAlarms: Flow<List<AlarmEntity>> = dao.getEnabledAlarms()

    /** HOME SCREEN */
    @WorkerThread
    suspend fun triggerStatus(id: String, status: Boolean) = withContext(Dispatchers.IO) {
        dao.triggerStatus(id, status)
    }

    @WorkerThread
    suspend fun deleteAll() {
        dao.deleteAll()
    }

    /** NEW ALARM SCREEN */
    @WorkerThread
    suspend fun addAlarm(alarm: AlarmEntity) {
        dao.addAlarm(alarm = alarm)
    }

    /** DETAILS SCREEN */
    @WorkerThread
    suspend fun deleteAlarm(alarm: AlarmEntity) {
        dao.deleteAlarm(alarm = alarm)
    }
    @WorkerThread
    suspend fun updateTitle(id: String, title: String){
        dao.updateTitle(id = id, title = title)
    }
    @WorkerThread
    suspend fun updateDescription(id: String, description: String){
        dao.updateDescription(id = id, description = description)
    }
    @WorkerThread
    suspend fun updateSchedule(id: String, schedule: String) {
        dao.updateSchedule(id, schedule = schedule)
    }

    @WorkerThread
    suspend fun clearSchedule(id: String){
        dao.clearSchedule(id = id)
    }
    @WorkerThread
    suspend fun saveHour(id: String, newHour: Int){
        dao.saveHour(id = id, newHour = newHour)
    }
    @WorkerThread
    suspend fun saveMinute(id: String, newMinute: Int){
        dao.saveMinute(id = id, newMinute = newMinute)
    }
    @WorkerThread
    suspend fun saveSecond(id: String, newSecond: Int){
        dao.saveSecond(id = id, newSecond = newSecond)
    }
}

