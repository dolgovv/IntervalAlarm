package com.example.intervalalarm.model.data.repository

import androidx.annotation.WorkerThread
import com.example.intervalalarm.model.data.database.AlarmEntity
import com.example.intervalalarm.model.data.database.AlarmsDAO
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class AlarmsRepository(
    private val dao: AlarmsDAO,
    private val defaultDispatcher: CoroutineDispatcher = Dispatchers.Default
    ) {

    val allAlarms: Flow<List<AlarmEntity>> = dao.getAlarmsCounted()

    /** HOME SCREEN */
    @WorkerThread
    suspend fun triggerStatus(id: String, status: Boolean) = withContext(defaultDispatcher) {
        dao.triggerStatus(id, status)
    }

    @WorkerThread
    suspend fun clearSchedule(id: String) = withContext(defaultDispatcher) {
        dao.clearSchedule(id = id)
    }

    /** NEW ALARM SCREEN */
    @WorkerThread
    suspend fun addAlarm(alarm: AlarmEntity) = withContext(defaultDispatcher) {
        dao.addAlarm(alarm = alarm)
    }

    /** DETAILS SCREEN */
    @WorkerThread
    suspend fun deleteAlarm(alarm: AlarmEntity) = withContext(defaultDispatcher) {
        dao.deleteAlarm(alarm = alarm)
    }
    @WorkerThread
    suspend fun updateTitle(id: String, title: String) = withContext(defaultDispatcher) {
        dao.updateTitle(id = id, title = title)
    }
    @WorkerThread
    suspend fun updateDescription(id: String, description: String) = withContext(defaultDispatcher) {
        dao.updateDescription(id = id, description = description)
    }
    @WorkerThread
    suspend fun updateSchedule(id: String, schedule: String) = withContext(defaultDispatcher) {
        dao.updateSchedule(id, schedule = schedule)
    }
    @WorkerThread
    suspend fun saveHour(id: String, newHour: Int) = withContext(defaultDispatcher) {
        dao.saveHour(id = id, newHour = newHour)
    }
    @WorkerThread
    suspend fun saveMinute(id: String, newMinute: Int) = withContext(defaultDispatcher) {
        dao.saveMinute(id = id, newMinute = newMinute)
    }
    @WorkerThread
    suspend fun saveSecond(id: String, newSecond: Int) = withContext(defaultDispatcher) {
        dao.saveSecond(id = id, newSecond = newSecond)
    }
}

