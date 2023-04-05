package com.example.intervalalarm.view.screens.details.states

import com.example.intervalalarm.view.screens.home.states.AlarmStatus
import com.example.intervalalarm.view.screens.home.states.AlarmUiState
import com.example.intervalalarm.view.screens.new_alarm.states.WheelPickerUiState

data class DetailsScreenUiState(
    val chosenAlarm: AlarmUiState = AlarmUiState("",1, AlarmStatus.Disabled, 1,1,1," ", "", ""),
    val isEditable: Boolean = false,
    val newTitle: String = "",
    val newDescription: String = "",
    val newSchedule: String = "",
    val detailsWheelPicker: WheelPickerUiState = WheelPickerUiState(),

    val showBackPressedDialog: Boolean = false
)