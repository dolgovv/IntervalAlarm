package com.example.intervalalarm.viewmodel.use_cases

import com.example.intervalalarm.model.data.repository.AlarmsRepository
import javax.inject.Inject

class SaveTitleUseCase @Inject constructor(private val repository: AlarmsRepository) {
    suspend operator fun invoke(id: String, title: String) {
        repository.updateTitle(id, title)
    }
}