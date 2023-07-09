package com.example.intervalalarm.viewmodel.use_cases

import com.example.intervalalarm.model.data.database.AlarmEntity
import com.example.intervalalarm.model.data.repository.AlarmsRepository
import javax.inject.Inject

class AddNewAlarmUseCase @Inject constructor(private val repository: AlarmsRepository) {

    suspend operator fun invoke(newAlarm: AlarmEntity){
        repository.addAlarm(newAlarm)
    }
}