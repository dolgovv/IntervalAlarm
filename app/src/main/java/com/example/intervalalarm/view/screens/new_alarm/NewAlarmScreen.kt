package com.example.intervalalarm.view.screens.new_alarm

import android.content.res.Configuration
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.intervalalarm.R
import com.example.intervalalarm.model.data.database.AlarmEntity
import com.example.intervalalarm.view.theme.IntervalAlarmTheme
import com.example.intervalalarm.view.common_components.IntervalFloatButton
import com.example.intervalalarm.view.screens.home.states.AlarmUiState
import com.example.intervalalarm.view.common_components.AdditionalInfoCard
import com.example.intervalalarm.view.common_components.DialogType
import com.example.intervalalarm.view.common_components.PreventDialog
import com.example.intervalalarm.view.common_components.WheelIntervalPicker
import com.example.intervalalarm.view.screens.new_alarm.states.AddNewScreenUiState
import com.example.intervalalarm.viewmodel.MainViewModel

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun NewAlarmScreen(
    state: AddNewScreenUiState,
//    list: List<AlarmUiState>,
    alarmsLastIndex: Int,
    navController: NavController,

    updateDescription: (String) -> Unit,
    updateSchedule: (String) -> Unit,

    updateNewHour: (Int) -> Unit,
    updateNewMinute: (Int) -> Unit,
    updateNewSecond: (Int) -> Unit,

    addNewAlarm: (AlarmEntity) -> Unit,
    hideBackPressedNewAlarmDialog: () -> Unit,
    clearNewAlarm: () -> Unit,
    onBackPressed: () -> Unit
) {
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current

    BackHandler(true) { onBackPressed() }

    val title = remember { mutableStateOf("") }

    val showZeroIntervalDialog = remember { mutableStateOf(false) }
    val showLowSecondAmountDialog = remember { mutableStateOf(false) }

    val isIntervalValid = ((state.wheelPickerState.currentHour != 0)
            || (state.wheelPickerState.currentMinute != 0)
            || (state.wheelPickerState.currentSecond > 14))


    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(12.dp),
        verticalArrangement = Arrangement.Top
    ) {

        if (state.showBackPressedDialog) {
            PreventDialog(
                type = DialogType.Cancel,
                hideDialog = { hideBackPressedNewAlarmDialog() },
                onCancel = { hideBackPressedNewAlarmDialog() },
                context = context
            ) {
                hideBackPressedNewAlarmDialog()
                clearNewAlarm()
                navController.popBackStack()
            }
        }

        /** ZERO INTERVAL DIALOG */
        if (showZeroIntervalDialog.value) {
            PreventDialog(
                context = context,
                type = DialogType.ZeroInterval,
                hideDialog = { showZeroIntervalDialog.value = false },
                onCancel = {
                    showZeroIntervalDialog.value = false
                    navController.popBackStack()
                    clearNewAlarm()
                },
                onContinue = { showZeroIntervalDialog.value = false }
            )
        }

        /** LOW SECOND AMOUNT DIALOG */
        if (showLowSecondAmountDialog.value) {
            PreventDialog(context = context,
                type = DialogType.LowSecondAmount,
                hideDialog = { showLowSecondAmountDialog.value = false },
                onCancel = {
                    showLowSecondAmountDialog.value = false
                    navController.popBackStack()
                    clearNewAlarm()
                },
                onContinue = { showLowSecondAmountDialog.value = false }
            )
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.3f),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            /** WHEEL PICKER */
            WheelIntervalPicker(
                state = state.wheelPickerState,
                updateHour = { updateNewHour(it) },
                updateMinute = { updateNewMinute(it) },
                updateSeconds = { updateNewSecond(it) }
            )
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .height(150.dp)
                .padding(bottom = 22.dp),
            verticalArrangement = Arrangement.Bottom,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            /** TITLE TEXT FIELD */
            OutlinedTextField(
                modifier = Modifier
                    .fillMaxWidth(),
                value = title.value, onValueChange = { title.value = it },
                label = { Text(text = "Title") },
                singleLine = true,
                keyboardActions = KeyboardActions(onDone = {
                    if (!focusManager.moveFocus(FocusDirection.Down)) {
                        focusManager.clearFocus()
                        keyboardController?.hide()
                    }
                }),
            )
        }

        /** DESCRIPTION AND SCHEDULE PICKER */
        AdditionalInfoCard(
            description = state.description,
            schedule = state.schedule,
            onDescriptionChanged = {
                updateDescription(it)
            },
            onScheduleChanged = {
                updateSchedule(it)
            },
            focusManager,
            keyboardController,
            isEditable = true
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(6.dp),
            verticalArrangement = Arrangement.Bottom,
            horizontalAlignment = Alignment.End
        ) {

            /** ADD NEW ALARM BUTTON */
            IntervalFloatButton(
                function = {
                    if (isIntervalValid) {
                        addNewAlarm(
                            AlarmEntity(
                                alarmCount = alarmsLastIndex,
//                                if (list.isNotEmpty()) list.last().count + 1 else 1,
                                isActive = state.schedule.isEmpty(),
                                hours = state.wheelPickerState.currentHour,
                                minutes = state.wheelPickerState.currentMinute,
                                seconds = state.wheelPickerState.currentSecond,
                                title = title.value.ifEmpty {
                                    "Alarm no. $alarmsLastIndex"
                                },
                                description = state.description,
                                schedule = state.schedule
                            )
                        )
                        navController.popBackStack()
                        clearNewAlarm()
                    } else if (state.wheelPickerState.currentSecond in 1..14) {
                        showLowSecondAmountDialog.value = true
                    } else {
                        showZeroIntervalDialog.value = true
                    }
                },
                hasIcon = false,
                isScheduled = state.schedule.isNotEmpty()
            )
        }
    }
}


@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview(showBackground = true)
@Composable
fun DefaultPreview2() {
    IntervalAlarmTheme {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            //NewAlarmScreen(viewModel())
        }
    }
}