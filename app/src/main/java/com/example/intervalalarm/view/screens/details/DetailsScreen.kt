package com.example.intervalalarm.view.screens.details

import android.content.res.Configuration
import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Edit
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
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.intervalalarm.R
import com.example.intervalalarm.model.module.alarm_management.IntervalAlarmManager
import com.example.intervalalarm.ui.theme.IntervalAlarmTheme
import com.example.intervalalarm.view.screens.details.states.DetailsScreenUiState
import com.example.intervalalarm.view.screens.home.states.AlarmUiState
import com.example.intervalalarm.view.screens.new_alarm.components.*
import com.example.intervalalarm.viewmodel.MainViewModel
import java.util.*

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun DetailsScreen(
    vm: MainViewModel,
    state: DetailsScreenUiState,
    navController: NavController,
    currentAlarm: AlarmUiState,
    onBackPressed: () -> Unit
) {
    val context = LocalContext.current

    BackHandler(state.isEditable) { onBackPressed() }

    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current

    val showDeleteDialog = remember { mutableStateOf(false) }
    val showSaveDialog = remember { mutableStateOf(false) }
    val showCancelDialog = remember { mutableStateOf(false) }

    val defaultTitleValue = "Alarm no. ${currentAlarm.count}"

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(12.dp), verticalArrangement = Arrangement.Top
    ) {

        /** DELETE DIALOG */
        if (showDeleteDialog.value) {
            PreventDialog(
                context = context,
                type = DialogType.DeleteAlarm,
                hideDialog = { showDeleteDialog.value = false },
                onCancel = { showDeleteDialog.value = false },
                onContinue = {
                    vm.deleteAlarm(context, state.chosenAlarm)
                    showDeleteDialog.value = false
                    navController.popBackStack()
                })
        }

        /** SAVE DIALOG */
        if (showSaveDialog.value) {
            PreventDialog(
                context = context,
                type = DialogType.SaveEdits,
                hideDialog = { showSaveDialog.value = false },
                onCancel = { showSaveDialog.value = false }) {
                if (state.chosenAlarm.schedule != state.newSchedule && state.newSchedule.isNotEmpty()) {
                    vm.saveEditedAlarm()
                    Log.d(
                        "date time format",
                        "${state.chosenAlarm.schedule} and new ${state.newSchedule}"
                    )
                    IntervalAlarmManager(context).setScheduledAlarm(
                        state.chosenAlarm.title,
                        state.chosenAlarm.description,
                        state.newSchedule,
                        state.chosenAlarm.hours,
                        state.chosenAlarm.minutes,
                        state.chosenAlarm.seconds,
                        state.chosenAlarm.count
                    )
                }
                vm.saveEditedAlarm()
                vm.triggerEditableDetails()
                showSaveDialog.value = false
            }
        }

        /** BACK PRESSED YET DIALOG */
        if (state.showBackPressedDialog) {
            PreventDialog(
                context = context,
                type = DialogType.Cancel,
                hideDialog = { vm.hideBackPressedDetailsDialog() },
                onCancel = { vm.hideBackPressedDetailsDialog() }) {
                vm.hideBackPressedDetailsDialog()
                vm.clearDetailsScreen()
                navController.popBackStack()
            }
        }

        /** CANCEL DIALOG */
        if (showCancelDialog.value) {
            PreventDialog(
                context = context,
                type = DialogType.Cancel,
                hideDialog = { showCancelDialog.value = false },
                onCancel = { showCancelDialog.value = false }) {
                vm.triggerEditableDetails()
                showCancelDialog.value = false
            }
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.3f),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxSize(), horizontalArrangement = Arrangement.Center
            ) {

                /** WHEEL PICKER */
                WheelIntervalPicker(
                    state = state.detailsWheelPicker,
                    updateHour = {
                        vm.updateDetailsWheelStateHour(it)
                    },
                    updateMinute = {
                        vm.updateDetailsWheelStateMinute(it)
                    },
                    updateSeconds = {
                        vm.updateDetailsWheelStateSecond(it)
                    },

                    defaultHour = state.chosenAlarm.hours,
                    defaultMinute = state.chosenAlarm.minutes,
                    defaultSecond = state.chosenAlarm.seconds,
                    status = if (state.isEditable) WheelPickerStatus.Enabled else WheelPickerStatus.Disabled
                )
            }
        }
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .height(150.dp)
                .padding(bottom = 22.dp)
                .padding(horizontal = 12.dp),
            verticalArrangement = Arrangement.Bottom,
            horizontalAlignment = Alignment.Start
        ) {

            if (state.isEditable) {

                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = if (state.newTitle != currentAlarm.title) state.newTitle
                    else if (currentAlarm.title == "Alarm no. ${currentAlarm.count}") "" else currentAlarm.title,
                    onValueChange = { vm.updateEditedTitle(it) },
                    label = { Text(text = "Title") },
                    singleLine = true,
                    keyboardActions = KeyboardActions(onDone = {
                        if (!focusManager.moveFocus(FocusDirection.Down)) {
                            focusManager.clearFocus()
                            keyboardController?.hide()
                        }
                    }),
                )
            } else {
                Text(text = currentAlarm.title.ifEmpty { defaultTitleValue }, fontSize = 36.sp)
            }
        }

        AdditionalInfoCard(
            description = if (state.isEditable) state.newDescription else if (currentAlarm.description.isEmpty()) "Empty description" else currentAlarm.description,
            schedule = currentAlarm.schedule,
            onDescriptionChanged = {
                vm.updateEditedDescription(it)
            },
            onScheduleChanged = {
                vm.updateEditedSchedule(it)
            },
            focusManager,
            keyboardController,
            isEnabled = state.isEditable
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        verticalArrangement = Arrangement.Bottom,
        horizontalAlignment = Alignment.End
    ) {

        /** FLOAT BUTTON */
        FloatingActionButton(
            backgroundColor = MaterialTheme.colors.primary,
            onClick = {
                Log.d(
                    "wheelllll",
                    "new hour is ${state.detailsWheelPicker.currentHour} and current is ${state.chosenAlarm.hours} from UI BUTTON"
                )

                if (state.isEditable) {
                    if (state.newTitle != currentAlarm.title || state.newDescription != currentAlarm.description || state.newSchedule != currentAlarm.schedule || state.detailsWheelPicker.currentHour != currentAlarm.hours || state.detailsWheelPicker.currentMinute != currentAlarm.minutes || state.detailsWheelPicker.currentSecond != currentAlarm.seconds) {
                        showSaveDialog.value = true

                    } else {
                        vm.triggerEditableDetails()
                    }
                } else {
                    vm.triggerEditableDetails()
                    vm.updateEditedTitle(currentAlarm.title)
                    vm.updateEditedDescription(currentAlarm.description)
                }
            },
        ) {
            if (state.isEditable) Icon(
                Icons.Filled.Done, contentDescription = "save alarm button"
            ) else Icon(Icons.Filled.Edit, contentDescription = "edit alarm button")
        }
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        verticalArrangement = Arrangement.Bottom,
        horizontalAlignment = Alignment.Start
    ) {
        FloatingActionButton(backgroundColor = MaterialTheme.colors.primary, onClick = {
            showDeleteDialog.value = true
        }) {
            Icon(Icons.Filled.Delete, contentDescription = "delete alarm button")
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
//            DetailsScreen(vm = viewModel(), navController = rememberNavController(), alarmCount = 1, )
        }
    }
}