package com.example.intervalalarm.view.screens.new_alarm.states

import com.example.intervalalarm.model.database.AlarmEntity

data class NewDayScreenUiState(

    val title: String = "",
    val description: String = "",
    val schedule: String = "",
    val wheelPickerState: WheelPickerUiState = WheelPickerUiState(0,0,0),

    val showBackPressedDialog: Boolean = false

//    val notyet_label: Int = 1 TODO
)