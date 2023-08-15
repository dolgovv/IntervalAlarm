package com.example.intervalalarm.viewmodel.use_cases

import android.content.Context
import com.example.intervalalarm.model.data.database.AlarmEntity
import com.example.intervalalarm.model.data.repository.AlarmsRepository
import com.example.intervalalarm.model.data.repository.AlarmsRepositoryDefault
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

class DeleteAlarmUseCase @Inject constructor(private val repository: AlarmsRepositoryDefault) {

    operator fun invoke(
        context: Context,
        alarm: AlarmEntity,
        defaultDispatcher: CoroutineDispatcher = Dispatchers.Default
    ) = runBlocking(defaultDispatcher) {
        repository.deleteAlarm(context = context, alarm = alarm)
    }
}