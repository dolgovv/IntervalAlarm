package com.example.intervalalarm.viewmodel.use_cases

data class AlarmsUseCases(
    val addNewUseCase: AddNewAlarmUseCase,
    val deleteUseCase: DeleteAlarmUseCase,
    val saveDescriptionUseCase: SaveDescriptionUseCase,
    val saveScheduleUseCase: SaveScheduleUseCase,
    val saveTitleUseCase: SaveTitleUseCase,
    val triggerAlarmStatusUseCase: TriggerAlarmStatusUseCase
)