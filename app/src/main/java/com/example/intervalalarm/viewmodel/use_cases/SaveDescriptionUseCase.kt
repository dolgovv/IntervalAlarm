package com.example.intervalalarm.viewmodel.use_cases

import com.example.intervalalarm.model.data.repository.AlarmsRepository
import com.example.intervalalarm.model.data.repository.AlarmsRepositoryDefault
import javax.inject.Inject

class SaveDescriptionUseCase @Inject constructor(private val repository: AlarmsRepositoryDefault) {
    suspend operator fun invoke(id: String, description: String) {
        repository.updateDescription(id, description)
    }
}