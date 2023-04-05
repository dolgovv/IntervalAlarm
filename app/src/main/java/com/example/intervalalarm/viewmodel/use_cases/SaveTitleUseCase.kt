package com.example.intervalalarm.viewmodel.use_cases

import android.util.Log
import com.example.intervalalarm.model.repository.AlarmsRepository
import javax.inject.Inject

class SaveTitleUseCase @Inject constructor(private val repository: AlarmsRepository) {
    suspend operator fun invoke(id: String, title: String) {
        Log.d("big problem resolve", "title $title saved")
        repository.updateTitle(id, title)
    }
}