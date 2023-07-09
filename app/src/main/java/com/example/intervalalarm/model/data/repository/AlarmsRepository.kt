package com.example.intervalalarm.model.data.repository

import androidx.annotation.WorkerThread
import com.example.intervalalarm.model.data.database.AlarmEntity
import com.example.intervalalarm.model.data.database.AlarmsDAO
import kotlinx.coroutines.flow.Flow

class AlarmsRepository(private val dao: AlarmsDAO) {

    val allAlarms: Flow<List<AlarmEntity>> = dao.getAlarmsCounted()

    /** HOME SCREEN */
    @WorkerThread
    suspend fun triggerStatus(id: String, status: Boolean) {
        dao.triggerStatus(id, status)
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

