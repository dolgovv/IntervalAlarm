package com.example.intervalalarm.viewmodel.use_cases

import com.example.intervalalarm.model.data.database.AlarmEntity
import com.example.intervalalarm.model.data.repository.AlarmsRepository
import com.example.intervalalarm.model.data.repository.AlarmsRepositoryDefault
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

class AddNewAlarmUseCase @Inject constructor(private val repository: AlarmsRepositoryDefault) {

    operator fun invoke(
        newAlarm: AlarmEntity,
        defaultDispatcher: CoroutineDispatcher = Dispatchers.Default
    ) = runBlocking(defaultDispatcher) {
        repository.addAlarm(newAlarm)
    }
}