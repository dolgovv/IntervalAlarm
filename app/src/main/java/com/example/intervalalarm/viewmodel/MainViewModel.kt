package com.example.intervalalarm.viewmodel

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.AudioAttributes
import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.intervalalarm.R
import com.example.intervalalarm.model.database.AlarmEntity
import com.example.intervalalarm.model.module.alarm_management.IntervalAlarmBroadcastReceiver
import com.example.intervalalarm.model.module.alarm_management.IntervalAlarmManager
import com.example.intervalalarm.model.module.notifications.AlarmNotificationService
import com.example.intervalalarm.model.module.timer.CurrentAlarmTimer
import com.example.intervalalarm.model.repository.AlarmsRepository
import com.example.intervalalarm.view.screens.details.states.DetailsScreenUiState
import com.example.intervalalarm.view.screens.home.states.AlarmStatus
import com.example.intervalalarm.view.screens.home.states.AlarmUiState
import com.example.intervalalarm.view.screens.home.states.HomeScreenUiState
import com.example.intervalalarm.view.screens.new_alarm.states.NewDayScreenUiState
import com.example.intervalalarm.view.screens.new_alarm.states.WheelPickerUiState
import com.example.intervalalarm.viewmodel.use_cases.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val repository: AlarmsRepository,

    private val addNewAlarmUseCase: AddNewAlarmUseCase,
    private val triggerAlarmStatusUseCase: TriggerAlarmStatusUseCase,
    private val deleteAlarmUseCase: DeleteAlarmUseCase,
    private val saveTitleUseCase: SaveTitleUseCase,
    private val saveDescriptionUseCase: SaveDescriptionUseCase,
    private val saveScheduleUseCase: SaveScheduleUseCase
) : ViewModel() {

    init {
        viewModelScope.launch {
            updateList()
        }
    }

    /** EXPERIMENTAL onBackPressed HANDLING */
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
    fun hideBackPressedNewAlarmDialog() = viewModelScope.launch {
        _addNewAlarmUiState.update {
            it.copy(showBackPressedDialog    = false)
        }
    }


    /** HOME SCREEN */
    private val _homeScreenUiState = MutableStateFlow(HomeScreenUiState())
    val homeScreenUiState: StateFlow<HomeScreenUiState> = _homeScreenUiState.asStateFlow()

    fun addNewAlarm(context: Context, newAlarm: AlarmEntity) = viewModelScope.launch {
        addNewAlarmUseCase(newAlarm)
        setIntervalAlarm(
            context,
            newAlarm.title,
            newAlarm.description,
            newAlarm.hours,
            newAlarm.minutes,
            newAlarm.seconds,
            newAlarm.alarmCount,
            newAlarm.schedule,
        )
        Log.d("problem resolve", newAlarm.title)
        updateList()
    }

    fun triggerAlarm(
        context: Context, alarm: AlarmUiState
    ) {
        viewModelScope.launch {
            triggerAlarmStatusUseCase(context, alarm)
            clearSchedule(alarm.id)
            updateList()
        }
    }

    private fun setIntervalAlarm(
        context: Context,
        title: String,
        description: String,
        hours: Int,
        minutes: Int,
        seconds: Int,
        count: Int,
        schedule: String
    ) {

        val totalTitle = title.ifEmpty { "Alarm $count" }
        val totalDescription = description.ifEmpty { "" }

        if (schedule.isEmpty()) {
            IntervalAlarmManager(context).setAlarm(
                totalTitle, totalDescription, count, hours, minutes, seconds
            )
        } else {
            IntervalAlarmManager(context).setScheduledAlarm(
                totalTitle, totalDescription, schedule, hours, minutes, seconds, count
            )
        }
    }

    private fun clearSchedule(id: String) =
        viewModelScope.launch { repository.clearSchedule(id = id) }

    private suspend fun updateList() {
        repository.allAlarms.collect { alarms ->

            _homeScreenUiState.update { currentState ->
                currentState.copy(
                    allAlarms = getAllAlarms(alarms),
                    enabledAlarms = getEnabledAlarms(alarms),
                    upcomingAlarm = getUpcomingAlarm(_homeScreenUiState.value)
                )
            }
        }
    }

    private fun getAllAlarms(alarms: List<AlarmEntity>): List<AlarmUiState> {
        return alarms.map { it.toUiState() }
    }

    private fun getEnabledAlarms(alarms: List<AlarmEntity>): List<AlarmUiState> {
        return alarms.filter { it.isActive }.map { it.toUiState() }
    }

    private fun getUpcomingAlarm(state: HomeScreenUiState): AlarmUiState {
        return if (state.enabledAlarms.isNotEmpty()) {
            state.enabledAlarms.minByOrNull {
                it.seconds * 1000
                + it.minutes * 1000 * 60
                + it.hours * 1000 * 60 * 60
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
                it.copy(isEditable = !it.isEditable)
            }
        }
    }

    fun deleteAlarm(context: Context, alarm: AlarmUiState) = viewModelScope.launch {
        deleteAlarmUseCase(context, alarm = alarm.toEntity())
        updateList()
        Log.d(
            "big problem resolve", "${_homeScreenUiState.value.allAlarms.size}"
        )
    }

    fun saveEditedAlarm() = viewModelScope.launch {

        val id = detailsScreenUiState.value.chosenAlarm.id

        val isTitleNew =
            detailsScreenUiState.value.chosenAlarm.title != detailsScreenUiState.value.newTitle

        val isDescriptionNew =
            detailsScreenUiState.value.chosenAlarm.description != detailsScreenUiState.value.newDescription

        val isScheduleNew =
            detailsScreenUiState.value.chosenAlarm.schedule != detailsScreenUiState.value.newSchedule

        val isHourNew =
            detailsScreenUiState.value.detailsWheelPicker.currentHour != detailsScreenUiState.value.chosenAlarm.hours

        val isMinuteNew =
            detailsScreenUiState.value.detailsWheelPicker.currentMinute != detailsScreenUiState.value.chosenAlarm.minutes

        val isSecondNew =
            detailsScreenUiState.value.detailsWheelPicker.currentSecond != detailsScreenUiState.value.chosenAlarm.seconds

        if (isTitleNew) {
            saveTitleUseCase(
                id, title = detailsScreenUiState.value.newTitle
            )
        }

        if (isDescriptionNew) {
            saveDescriptionUseCase(
                id, description = detailsScreenUiState.value.newDescription
            )
        }

        if (isScheduleNew) {
            saveScheduleUseCase(
                id, newSchedule = detailsScreenUiState.value.newSchedule
            )
            Log.d("date time format", "saved from vm. now is ${detailsScreenUiState.value.chosenAlarm.schedule}")
        }

        if (isHourNew) {
            saveDetailsHour(
                id, detailsScreenUiState.value.detailsWheelPicker.currentHour
            )
        }

        if (isMinuteNew) {
            saveDetailsMinute(
                id, detailsScreenUiState.value.detailsWheelPicker.currentMinute
            )
        }

        if (isSecondNew) {
            saveDetailsSecond(
                id, detailsScreenUiState.value.detailsWheelPicker.currentSecond
            )
        }
    }

    //HOURS
    fun updateDetailsWheelStateHour(hour: Int) = viewModelScope.launch {
        updateWheelHour(hour)
        _detailsScreenUiState.update { it.copy(detailsWheelPicker = _wheelUiState.value) }
    }

    private fun saveDetailsHour(id: String, newHour: Int) = viewModelScope.launch {
        repository.saveHour(id = id, newHour = newHour)
    }

    //MINUTES
    fun updateDetailsWheelStateMinute(minute: Int) = viewModelScope.launch {
        updateWheelMinute(minute)
        _detailsScreenUiState.update { it.copy(detailsWheelPicker = wheelUiState.value) }
    }

    private fun saveDetailsMinute(id: String, newMinute: Int) = viewModelScope.launch {
        repository.saveMinute(id = id, newMinute = newMinute)
    }

    //SECONDS
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
            delay(500)
            _detailsScreenUiState.update {
                it.copy(
                    chosenAlarm = AlarmUiState(
                        "", 1, AlarmStatus.Disabled, 1, 1, 1, "null alarm", "", ""
                    ), isEditable = false, newTitle = "", newDescription = "", newSchedule = ""
                )
            }
        }
    }

    fun updateDetailsScreen(currentAlarm: AlarmUiState) =
        viewModelScope.launch {
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


    /** NEW ALARM SCREEN */
    private val _addNewAlarmUiState = MutableStateFlow(NewDayScreenUiState())
    val addNewAlarmUiState = _addNewAlarmUiState.asStateFlow()

    fun clearNewAlarm() = viewModelScope.launch {
        _addNewAlarmUiState.update {
            it.copy(
                description = "", title = "", schedule = "", wheelPickerState = WheelPickerUiState()
            )
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
        Log.d(
            "scheduled alarm maintenance", "schedule updated"
        )
    }

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
}