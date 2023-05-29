package com.example.intervalalarm.viewmodel.use_cases

import com.example.intervalalarm.model.database.AlarmEntity
import com.example.intervalalarm.model.repository.AlarmsRepository
import javax.inject.Inject

class AddNewAlarmUseCase @Inject constructor(private val repository: AlarmsRepository) {

    suspend operator fun invoke(newAlarm: AlarmEntity){
        repository.addAlarm(newAlarm)
    }
}