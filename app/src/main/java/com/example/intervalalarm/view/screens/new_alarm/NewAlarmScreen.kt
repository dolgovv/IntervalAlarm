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
import com.example.intervalalarm.model.database.AlarmEntity
import com.example.intervalalarm.ui.theme.IntervalAlarmTheme
import com.example.intervalalarm.view.screens.home.components.IntervalFloatButton
import com.example.intervalalarm.view.screens.home.states.AlarmUiState
import com.example.intervalalarm.view.screens.new_alarm.components.AdditionalInfoCard
import com.example.intervalalarm.view.screens.new_alarm.components.DialogType
import com.example.intervalalarm.view.screens.new_alarm.components.PreventDialog
import com.example.intervalalarm.view.screens.new_alarm.components.WheelIntervalPicker
import com.example.intervalalarm.view.screens.new_alarm.states.AddNewScreenUiState
import com.example.intervalalarm.viewmodel.MainViewModel

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun NewAlarmScreen(
    state: AddNewScreenUiState,
    list: List<AlarmUiState>,
    vm: MainViewModel,
    navController: NavController,
    onBackPressed: () -> Unit
) {
    val context = LocalContext.current

//    val list = vm.homeScreenUiState.collectAsState().value.allAlarms

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
            .padding(R.dimen.padding_medium.dp),
        verticalArrangement = Arrangement.Top
    ) {

        if (state.showBackPressedDialog) {
            PreventDialog(
                type = DialogType.Cancel,
                hideDialog = { vm.hideBackPressedNewAlarmDialog() },
                onCancel = { vm.hideBackPressedNewAlarmDialog() },
                context = context
            ) {
                vm.hideBackPressedNewAlarmDialog()
                vm.clearNewAlarm()
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
                    vm.clearNewAlarm()
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
                    vm.clearNewAlarm()
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
                updateHour = { vm.updateNewHour(it) },
                updateMinute = { vm.updateNewMinute(it) },
                updateSeconds = { vm.updateNewSecond(it) }
            )
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .height(150.dp)
                .padding(bottom = R.dimen.padding_large.dp),
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
                vm.updateDescription(it)
            },
            onScheduleChanged = {
                vm.updateSchedule(it)
            },
            focusManager,
            keyboardController,
            isEditable = true
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(R.dimen.padding_small.dp),
            verticalArrangement = Arrangement.Bottom,
            horizontalAlignment = Alignment.End
        ) {

            /** ADD NEW ALARM BUTTON */
            IntervalFloatButton(
                function = {
                    if (isIntervalValid) {
                        vm.addNewAlarm(
                            context,
                            AlarmEntity(
                                alarmCount = if (list.isNotEmpty()) list.last().count + 1 else 1,
                                isActive = state.schedule.isEmpty(),
                                hours = state.wheelPickerState.currentHour,
                                minutes = state.wheelPickerState.currentMinute,
                                seconds = state.wheelPickerState.currentSecond,
                                title = title.value.ifEmpty {
                                    "Alarm no. ${if (list.isNotEmpty()) list.last().count + 1 else 1}" },
                                description = state.description,
                                schedule = state.schedule
                            )
                        )
                        navController.popBackStack()
                        vm.clearNewAlarm()
                    } else if (state.wheelPickerState.currentSecond in 1..14) {
                        showLowSecondAmountDialog.value = true
                    }
                    else {
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