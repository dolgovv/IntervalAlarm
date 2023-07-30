package com.example.intervalalarm.view.screens.details.states

import com.example.intervalalarm.view.screens.home.states.AlarmUiState
import com.example.intervalalarm.view.screens.new_alarm.states.WheelPickerUiState

data class DetailsScreenUiState(
    val chosenAlarm: AlarmUiState = AlarmUiState(),
    val isEditable: Boolean = false,
    val newTitle: String = "",
    val newDescription: String = "",
    val newSchedule: String = "",
    val newWheelPickerValues: WheelPickerUiState = WheelPickerUiState(),

    val showBackPressedDialog: Boolean = false
)