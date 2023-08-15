package com.example.intervalalarm.viewmodel.use_cases

import android.content.Context
import com.example.intervalalarm.model.alarm_functionality.IntervalAlarmManager
import com.example.intervalalarm.model.data.repository.AlarmsRepository
import com.example.intervalalarm.model.data.repository.AlarmsRepositoryDefault
import com.example.intervalalarm.view.screens.home.states.AlarmStatus
import com.example.intervalalarm.view.screens.home.states.AlarmUiState
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import javax.inject.Inject

class TriggerAlarmStatusUseCase @Inject constructor(
    private val repository: AlarmsRepositoryDefault
) {
    operator fun invoke(
        context: Context,
        alarm: AlarmUiState,
        defaultDispatcher: CoroutineDispatcher = Dispatchers.Default
    ) = runBlocking(defaultDispatcher) {
        repository.triggerStatus(
            context = context,
            alarm = alarm.toEntity()
        )
    }
}