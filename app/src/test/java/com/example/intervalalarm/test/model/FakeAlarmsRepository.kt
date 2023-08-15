package com.example.intervalalarm.test.model

import android.content.Context
import com.example.intervalalarm.model.data.database.AlarmEntity
import com.example.intervalalarm.model.data.repository.AlarmsRepositoryDefault
import com.example.intervalalarm.view.screens.home.states.AlarmStatus
import com.example.intervalalarm.view.screens.home.states.AlarmUiState
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class FakeAlarmsRepository : AlarmsRepositoryDefault {

    val fakeDb = mutableListOf<AlarmEntity>()

    override fun getAllAlarms(): Flow<List<AlarmEntity>> {
        return flow { emit(fakeDb) }
    }

    override suspend fun triggerStatus(
        context: Context,
        alarm: AlarmEntity
    ) {
        fakeDb[alarm.alarmCount - 1] = AlarmUiState(
            alarm.id,
            alarm.alarmCount,
            status = if (!alarm.isActive) AlarmStatus.Enabled else AlarmStatus.Disabled
        ).toEntity()
    }

    override suspend fun triggerStatusByCount(context: Context, alarmCount: Int, status: Boolean) {
        TODO("Not yet implemented")
    }

    override suspend fun clearSchedule(id: String) {
        TODO("Not yet implemented")
    }

    override suspend fun clearScheduleByCount(count: Int) {
        TODO("Not yet implemented")
    }

    override suspend fun disableAllAlarms() {
        TODO("Not yet implemented")
    }

    override suspend fun haveEnabledAlarms(): Boolean {
        TODO("Not yet implemented")
    }

    override suspend fun addAlarm(alarm: AlarmEntity) {
        fakeDb.add(alarm)
    }

    override suspend fun deleteAlarm(context: Context, alarm: AlarmEntity) {
        fakeDb.remove(alarm)
    }

    override suspend fun updateTitle(id: String, title: String) {
        TODO("Not yet implemented")
    }

    override suspend fun updateDescription(id: String, description: String) {
        TODO("Not yet implemented")
    }

    override suspend fun updateSchedule(id: String, schedule: String) {
        TODO("Not yet implemented")
    }

    override suspend fun saveHour(id: String, newHour: Int) {
        TODO("Not yet implemented")
    }

    override suspend fun saveMinute(id: String, newMinute: Int) {
        TODO("Not yet implemented")
    }

    override suspend fun saveSecond(id: String, newSecond: Int) {
        TODO("Not yet implemented")
    }

}