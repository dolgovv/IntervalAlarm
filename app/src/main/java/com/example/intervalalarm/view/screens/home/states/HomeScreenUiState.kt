package com.example.intervalalarm.view.screens.home.states

data class HomeScreenUiState(
    val allAlarms: List<AlarmUiState> = listOf(),
    val enabledAlarms: List<AlarmUiState> = listOf(),
    val upcomingAlarm: AlarmUiState = AlarmUiState(
        "", 0, AlarmStatus.Disabled,
        0, 0, 0, "", "", ""
    )
)