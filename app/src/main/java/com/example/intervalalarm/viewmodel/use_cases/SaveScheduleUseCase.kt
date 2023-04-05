package com.example.intervalalarm.viewmodel.use_cases

import com.example.intervalalarm.model.repository.AlarmsRepository
import javax.inject.Inject

class SaveScheduleUseCase @Inject constructor(private val repository: AlarmsRepository) {
    suspend operator fun invoke(id: String, newSchedule: String) {
        repository.updateSchedule(id, schedule = newSchedule)
    }
}