package com.example.intervalalarm.view.screens.new_alarm

import android.content.res.Configuration
import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.intervalalarm.model.database.AlarmEntity
import com.example.intervalalarm.ui.theme.IntervalAlarmTheme
import com.example.intervalalarm.view.screens.home.components.AddNewAlarmButton
import com.example.intervalalarm.view.screens.navigation.Screens
import com.example.intervalalarm.view.screens.new_alarm.components.AdditionalInfoCard
import com.example.intervalalarm.view.screens.new_alarm.components.DialogType
import com.example.intervalalarm.view.screens.new_alarm.components.PreventDialog
import com.example.intervalalarm.view.screens.new_alarm.components.WheelIntervalPicker
import com.example.intervalalarm.viewmodel.MainViewModel
import kotlin.random.Random

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun NewAlarmScreen(
    vm: MainViewModel,
    navController: NavController,
    onBackPressed: () -> Unit
) {
    val context = LocalContext.current

    val state = vm.addNewAlarmUiState.collectAsState()
    val list = vm.homeScreenUiState.collectAsState().value.allAlarms

    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current
    BackHandler(true) { onBackPressed() }

    val title = remember { mutableStateOf("") }

    val showZeroIntervalDialog = remember { mutableStateOf(false) }
    val showLowSecondAmountDialog = remember { mutableStateOf(false) }

    val addingNewRationale = remember {
        derivedStateOf {
            state.value.wheelPickerState.currentHour != 0
                    || state.value.wheelPickerState.currentMinute != 0
                    || state.value.wheelPickerState.currentSecond > 14
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(12.dp),
        verticalArrangement = Arrangement.Top
    ) {

        if (state.value.showBackPressedDialog) {
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
                state = state.value.wheelPickerState,
                updateHour = { vm.updateNewHour(it) },
                updateMinute = { vm.updateNewMinute(it) },
                updateSeconds = { vm.updateNewSecond(it) }
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
            description = state.value.description,
            schedule = state.value.schedule,
            onDescriptionChanged = {
                vm.updateDescription(it)
            },
            onScheduleChanged = {
                vm.updateSchedule(it)
            },
            focusManager,
            keyboardController,
            isEnabled = true
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            verticalArrangement = Arrangement.Bottom,
            horizontalAlignment = Alignment.End
        ) {

            /** ADD NEW ALARM BUTTON */
            AddNewAlarmButton(
                addNew = {
                    if (addingNewRationale.value) {
                        vm.addNewAlarm(
                            context,
                            AlarmEntity(
                                alarmCount = if (list.isNotEmpty()) list.last().count + 1 else 1,
                                isActive = state.value.schedule.isEmpty(),
                                hours = state.value.wheelPickerState.currentHour,
                                minutes = state.value.wheelPickerState.currentMinute,
                                seconds = state.value.wheelPickerState.currentSecond,
                                title = title.value.ifEmpty {
                                    "Alarm no. ${if (list.isNotEmpty()) list.last().count + 1 else 1}" },
                                description = state.value.description,
                                schedule = state.value.schedule
                            )
                        )
                        navController.popBackStack()
                        vm.clearNewAlarm()
                    } else if (state.value.wheelPickerState.currentSecond in 1..9) {
                        showLowSecondAmountDialog.value = true
                    } else {
                        showZeroIntervalDialog.value = true
                    }
                },
                hasIcon = false,
                isScheduled = state.value.schedule.isNotEmpty()
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