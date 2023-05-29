package com.example.intervalalarm.view.screens.new_alarm.states

data class AddNewScreenUiState(

    val title: String = "",
    val description: String = "",
    val schedule: String = "",
    val wheelPickerState: WheelPickerUiState = WheelPickerUiState(0,0,0),

    val showBackPressedDialog: Boolean = false
)