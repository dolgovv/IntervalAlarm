package com.example.intervalalarm.viewmodel

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.intervalalarm.model.data.database.AlarmEntity
import com.example.intervalalarm.model.alarm_functionality.IntervalAlarmBroadcastReceiver
import com.example.intervalalarm.model.alarm_functionality.IntervalAlarmManager
import com.example.intervalalarm.model.permissions.IntervalPermissionManager
import com.example.intervalalarm.model.data.repository.AlarmsRepository
import com.example.intervalalarm.view.screens.details.states.DetailsScreenUiState
import com.example.intervalalarm.view.screens.home.states.AlarmStatus
import com.example.intervalalarm.view.screens.home.states.AlarmUiState
import com.example.intervalalarm.view.screens.home.states.HomeScreenUiState
import com.example.intervalalarm.view.screens.new_alarm.states.AddNewScreenUiState
import com.example.intervalalarm.view.screens.new_alarm.states.WheelPickerUiState
import com.example.intervalalarm.viewmodel.use_cases.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val repository: AlarmsRepository,

    private val addNewAlarmUseCase: AddNewAlarmUseCase,
    private val triggerAlarmStatusUseCase: TriggerAlarmStatusUseCase,
    private val deleteAlarmUseCase: DeleteAlarmUseCase,
    private val saveTitleUseCase: SaveTitleUseCase,
    private val saveDescriptionUseCase: SaveDescriptionUseCase,
    private val saveScheduleUseCase: SaveScheduleUseCase,
) : ViewModel() {

    init {
        viewModelScope.launch {
            updateList()
//            TODO("start all the alarms if not active yet (should be active actually)")
        }
    }


    /** HOME SCREEN */
    private val _homeScreenUiState = MutableStateFlow(HomeScreenUiState())
    val homeScreenUiState: StateFlow<HomeScreenUiState> = _homeScreenUiState.asStateFlow()

    fun triggerAlarm(
        context: Context,
        alarm: AlarmUiState,
        infoToast: () -> Unit
    ) {
        viewModelScope.launch {
            triggerAlarmStatusUseCase(context, alarm)
            if (alarm.status == AlarmStatus.Scheduled) {
                clearSchedule(alarm.id)
                infoToast()
            }
        }
    }

    private fun setIntervalAlarm(
        context: Context, alarm: AlarmUiState
    ) {

        if (alarm.schedule.isEmpty()) {
            IntervalAlarmManager(context).setAlarm(
                alarm.title,
                alarm.description,
                alarm.count,
                alarm.hours,
                alarm.minutes,
                alarm.seconds
            )
        } else {
            viewModelScope.launch {
                IntervalAlarmManager(context).setScheduledAlarm(
                    alarm.title,
                    alarm.description,
                    alarm.schedule,
                    alarm.count,
                    alarm.hours,
                    alarm.minutes,
                    alarm.seconds
                )
            }
        }
    }

    private fun clearSchedule(id: String) =
        viewModelScope.launch { repository.clearSchedule(id = id) }

    private fun updateList() = viewModelScope.launch {
        repository.allAlarms.collect { alarms ->

            _homeScreenUiState.update { currentState ->
                currentState.copy(
                    allAlarms = getAllAlarms(alarms), enabledAlarms = getEnabledAlarms(alarms)
                )
            }
            _homeScreenUiState.update {
                it.copy(
                    upcomingAlarm = getUpcomingAlarm(_homeScreenUiState.value)
                )
            }
        }
    }

    private fun getAllAlarms(alarms: List<AlarmEntity>): List<AlarmUiState> {
        return alarms.map { it.toUiState() }
    }

    private fun getEnabledAlarms(alarms: List<AlarmEntity>): List<AlarmUiState> {
        return if (alarms.none { it.isActive }) alarms.filter { it.schedule.isNotEmpty() }
            .map { it.toUiState() } else alarms.filter { it.isActive }.map { it.toUiState() }
    }

    private fun getUpcomingAlarm(state: HomeScreenUiState): AlarmUiState {

        return if (state.enabledAlarms.isNotEmpty()) {
            state.enabledAlarms.minByOrNull {
                (it.seconds * 1000) + (it.minutes * 1000 * 60) + (it.hours * 1000 * 60 * 60)
            }!!
        } else {
            AlarmUiState()
        }
    }

    /** DETAILS SCREEN */
    private val _detailsScreenUiState = MutableStateFlow(DetailsScreenUiState())
    val detailsScreenUiState = _detailsScreenUiState.asStateFlow()

    fun triggerEditableDetails() {
        viewModelScope.launch {
            _detailsScreenUiState.update {
                it.copy(
                    isEditable = !it.isEditable,
//                    newSchedule = "", newDescription = ""
                    detailsWheelPicker = WheelPickerUiState(
                        _detailsScreenUiState.value.chosenAlarm.hours,
                        _detailsScreenUiState.value.chosenAlarm.minutes,
                        _detailsScreenUiState.value.chosenAlarm.seconds,
                    )
                )
            }
        }
    }

    fun deleteAlarm(context: Context, alarm: AlarmUiState) = viewModelScope.launch {
        deleteAlarmUseCase(context, alarm = alarm.toEntity())
    }

    fun saveEditedAlarm(context: Context) {

        val intent = Intent(context, IntervalAlarmBroadcastReceiver::class.java)
        val pI = PendingIntent.getBroadcast(
            context,
            detailsScreenUiState.value.chosenAlarm.count,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val id = detailsScreenUiState.value.chosenAlarm.id

        val newTitle = _detailsScreenUiState.asStateFlow().value.newTitle
        val newDescription = _detailsScreenUiState.asStateFlow().value.newDescription
        val newSchedule = _detailsScreenUiState.asStateFlow().value.newSchedule

        val wheelState = _detailsScreenUiState.asStateFlow().value.detailsWheelPicker

        val isTitleNew =
            detailsScreenUiState.value.chosenAlarm.title != detailsScreenUiState.value.newTitle

        val isDescriptionNew =
            detailsScreenUiState.value.chosenAlarm.description != detailsScreenUiState.value.newDescription

        val isScheduleNew =
            (detailsScreenUiState.value.chosenAlarm.schedule != detailsScreenUiState.value.newSchedule) && (detailsScreenUiState.value.newSchedule.isNotEmpty())

        val isHourNew =
            detailsScreenUiState.value.detailsWheelPicker.currentHour != detailsScreenUiState.value.chosenAlarm.hours

        val isMinuteNew =
            detailsScreenUiState.value.detailsWheelPicker.currentMinute != detailsScreenUiState.value.chosenAlarm.minutes

        val isSecondNew =
            detailsScreenUiState.value.detailsWheelPicker.currentSecond != detailsScreenUiState.value.chosenAlarm.seconds


        runBlocking {

            if (isTitleNew) {
                launch {

                    IntervalAlarmManager(context).cancelAlarm(pI)
                    saveTitleUseCase(
                        id, title = newTitle
                    )
                    setIntervalAlarm(
                        context,
                        alarm = detailsScreenUiState.value.chosenAlarm.copy(title = newTitle)
                    )
                }.join()
            }

            if (isDescriptionNew) {
                launch {

                    saveDescriptionUseCase(
                        id, description = newDescription
                    )
                    IntervalAlarmManager(context).cancelAlarm(pI)
                    setIntervalAlarm(
                        context,
                        alarm = detailsScreenUiState.value.chosenAlarm.copy(description = newDescription)
                    )
                }.join()
            }

            if (isScheduleNew) {
                launch {

                    if (detailsScreenUiState.value.chosenAlarm.status == AlarmStatus.Enabled) {
                        triggerAlarmStatusUseCase(
                            context, alarm = detailsScreenUiState.value.chosenAlarm
                        )
                    }
                    IntervalAlarmManager(context).cancelAlarm(pI)
                    saveScheduleUseCase(id, newSchedule = newSchedule)
                    setIntervalAlarm(
                        context,
                        alarm = detailsScreenUiState.value.chosenAlarm.copy(schedule = newSchedule)
                    )
                    _detailsScreenUiState.update { detScreenState ->
                        detScreenState.copy(
                            chosenAlarm = detScreenState.chosenAlarm.copy(status = AlarmStatus.Scheduled)
                        )
                    }

                }.join()
            }

            if (isHourNew) {
                launch {

                    IntervalAlarmManager(context).cancelAlarm(pI)
                    saveDetailsHour(
                        id, wheelState.currentHour
                    )
                    setIntervalAlarm(
                        context,
                        alarm = detailsScreenUiState.value.chosenAlarm.copy(hours = detailsScreenUiState.value.detailsWheelPicker.currentHour)
                    )
                }.join()
            }

            if (isMinuteNew) {
                launch {

                    IntervalAlarmManager(context).cancelAlarm(pI)
                    saveDetailsMinute(
                        id, wheelState.currentMinute
                    )
                    setIntervalAlarm(
                        context,
                        alarm = detailsScreenUiState.value.chosenAlarm.copy(minutes = detailsScreenUiState.value.detailsWheelPicker.currentMinute)
                    )
                }.join()
            }

            if (isSecondNew) {
                launch {

                    IntervalAlarmManager(context).cancelAlarm(pI)
                    saveDetailsSecond(
                        id, wheelState.currentSecond
                    )
                    setIntervalAlarm(
                        context,
                        alarm = detailsScreenUiState.value.chosenAlarm.copy(seconds = detailsScreenUiState.value.detailsWheelPicker.currentSecond)
                    )
                }.join()
            }
        }
    }

    // WHEEL

    fun updateDetailsWheelStateHour(hour: Int) = viewModelScope.launch {
        updateWheelHour(hour)
        _detailsScreenUiState.update { it.copy(detailsWheelPicker = _wheelUiState.value) }
    }

    private fun saveDetailsHour(id: String, newHour: Int) = viewModelScope.launch {
        repository.saveHour(id = id, newHour = newHour)
    }

    fun updateDetailsWheelStateMinute(minute: Int) = viewModelScope.launch {
        updateWheelMinute(minute)
        _detailsScreenUiState.update { it.copy(detailsWheelPicker = wheelUiState.value) }
    }

    private fun saveDetailsMinute(id: String, newMinute: Int) = viewModelScope.launch {
        repository.saveMinute(id = id, newMinute = newMinute)
    }

    fun updateDetailsWheelStateSecond(second: Int) = viewModelScope.launch {
        updateWheelSecond(second)
        _detailsScreenUiState.update {
            it.copy(detailsWheelPicker = wheelUiState.value)
        }
    }

    private fun saveDetailsSecond(id: String, newSecond: Int) = viewModelScope.launch {
        repository.saveSecond(id = id, newSecond = newSecond)
    }

    fun updateEditedTitle(newTitle: String) {
        viewModelScope.launch {
            _detailsScreenUiState.update {
                it.copy(newTitle = newTitle)
            }
        }
    }

    fun updateEditedDescription(newDescription: String) = viewModelScope.launch {
        _detailsScreenUiState.update {
            it.copy(newDescription = newDescription)
        }
    }

    fun updateEditedSchedule(newSchedule: String) = viewModelScope.launch {
        _detailsScreenUiState.update {
            it.copy(newSchedule = newSchedule)
        }
    }

    fun clearDetailsScreen() {
        viewModelScope.launch {
            _detailsScreenUiState.update {
                it.copy(
                    isEditable = false
                )
            }
        }
    }

    fun updateDetailsScreen(currentAlarm: AlarmUiState) = viewModelScope.launch {
        _detailsScreenUiState.update {
            it.copy(
                chosenAlarm = currentAlarm
            )
        }
        updateList()
    }

    fun deleteAllAlarms(context: Context) = viewModelScope.launch {
        _homeScreenUiState.value.allAlarms.map {
            deleteAlarm(context, alarm = it)
        }
        updateList()
    }

    fun showBackPressedDetailsDialog() = viewModelScope.launch {
        _detailsScreenUiState.update {
            it.copy(showBackPressedDialog = true)
        }
    }

    fun hideBackPressedDetailsDialog() = viewModelScope.launch {
        _detailsScreenUiState.update {
            it.copy(showBackPressedDialog = false)
        }
    }

    fun showBackPressedNewAlarmDialog() = viewModelScope.launch {
        _addNewAlarmUiState.update {
            it.copy(showBackPressedDialog = true)
        }
    }

    /** NEW ALARM SCREEN */
    private val _addNewAlarmUiState = MutableStateFlow(AddNewScreenUiState())
    val addNewAlarmUiState = _addNewAlarmUiState.asStateFlow()

    fun addNewAlarm(context: Context, newAlarm: AlarmEntity) = viewModelScope.launch {
        addNewAlarmUseCase(newAlarm)
        setIntervalAlarm(
            context, alarm = newAlarm.toUiState()
        )
    }

    // WHEEL
    fun updateNewHour(hour: Int) = viewModelScope.launch {
        updateWheelHour(hour)
        _addNewAlarmUiState.update {
            it.copy(wheelPickerState = _wheelUiState.value)
        }
    }

    fun updateNewMinute(minute: Int) = viewModelScope.launch {
        updateWheelMinute(minute)
        _addNewAlarmUiState.update {
            it.copy(wheelPickerState = _wheelUiState.value)
        }
    }

    fun updateNewSecond(second: Int) = viewModelScope.launch {
        updateWheelSecond(second)
        _addNewAlarmUiState.update {
            it.copy(wheelPickerState = _wheelUiState.value)
        }
    }

    // UTILITIES
    fun clearNewAlarm() = viewModelScope.launch {
        _addNewAlarmUiState.update {
            it.copy(
                description = "", title = "", schedule = "", wheelPickerState = WheelPickerUiState()
            )
        }
    }

    fun hideBackPressedNewAlarmDialog() = viewModelScope.launch {
        _addNewAlarmUiState.update {
            it.copy(showBackPressedDialog = false)
        }
    }

    fun updateDescription(description: String) = viewModelScope.launch {
        _addNewAlarmUiState.update {
            it.copy(description = description)
        }
    }

    fun updateSchedule(schedule: String) = viewModelScope.launch {
        _addNewAlarmUiState.update {
            it.copy(schedule = schedule)
        }
    }

    /** WHEEL PICKER HANDLER */
    private val _wheelUiState = MutableStateFlow(WheelPickerUiState())
    private val wheelUiState = _wheelUiState.asStateFlow()

    private fun updateWheelHour(hour: Int) {
        _wheelUiState.update {
            it.copy(currentHour = hour)
        }
    }

    private fun updateWheelMinute(minute: Int) {
        _wheelUiState.update {
            it.copy(currentMinute = minute)
        }
    }

    private fun updateWheelSecond(second: Int) {
        _wheelUiState.update {
            it.copy(currentSecond = second)
        }
    }

}