package com.example.intervalalarm.viewmodel.use_cases

import android.util.Log
import com.example.intervalalarm.model.repository.AlarmsRepository
import javax.inject.Inject

class SaveDescriptionUseCase @Inject constructor(private val repository: AlarmsRepository) {
    suspend operator fun invoke(id: String, description: String) {
        repository.updateDescription(id, description)
    }
}