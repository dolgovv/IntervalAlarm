package com.example.intervalalarm.view.screens.details

import android.content.res.Configuration
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
import com.example.intervalalarm.view.common_components.AdditionalInfoCard
import com.example.intervalalarm.view.common_components.DialogType
import com.example.intervalalarm.view.common_components.PreventDialog
import com.example.intervalalarm.view.theme.IntervalAlarmTheme
import com.example.intervalalarm.view.screens.details.states.DetailsScreenUiState
import com.example.intervalalarm.view.common_components.IntervalFloatButton
import com.example.intervalalarm.view.common_components.WheelIntervalPicker
import com.example.intervalalarm.view.common_components.WheelPickerStatus
import com.example.intervalalarm.view.screens.home.states.AlarmStatus
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
    /** UTILITIES */
    val context = LocalContext.current

    BackHandler(state.isEditable) { onBackPressed() }

    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current

    val showDeleteDialog = remember { mutableStateOf(false) }
    val showSaveDialog = remember { mutableStateOf(false) }
    val showCancelDialog = remember { mutableStateOf(false) }
    val showLowSecondAmountDialog = remember { mutableStateOf(false) }

    val isAnyInfoChanged =
        ((state.newTitle != currentAlarm.title) || (state.newDescription != currentAlarm.description) || ((state.newSchedule != currentAlarm.schedule) && (state.newSchedule.isNotEmpty())) || (state.detailsWheelPicker.currentHour != currentAlarm.hours) || (state.detailsWheelPicker.currentMinute != currentAlarm.minutes) || (state.detailsWheelPicker.currentSecond != currentAlarm.seconds))

    val isIntervalValid =
        (state.detailsWheelPicker.currentHour != 0) || (state.detailsWheelPicker.currentMinute != 0) || (state.detailsWheelPicker.currentSecond > 14)


    /** COLORS */
    val detailsUiColor = when (state.chosenAlarm.status) {
        AlarmStatus.Enabled -> MaterialTheme.colors.primary
        AlarmStatus.Disabled -> MaterialTheme.colors.secondaryVariant
        AlarmStatus.Scheduled -> MaterialTheme.colors.secondaryVariant
    }

    val additionalInfoCardUiColor = if (state.isEditable) {
        when (state.chosenAlarm.status) {
            AlarmStatus.Enabled -> MaterialTheme.colors.primary
            AlarmStatus.Disabled -> MaterialTheme.colors.secondaryVariant
            AlarmStatus.Scheduled -> MaterialTheme.colors.secondaryVariant
        }
    } else {
        when (state.chosenAlarm.status) {
            AlarmStatus.Enabled -> MaterialTheme.colors.primaryVariant
            AlarmStatus.Disabled -> MaterialTheme.colors.secondary
            AlarmStatus.Scheduled -> MaterialTheme.colors.secondary
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(R.dimen.padding_medium.dp), verticalArrangement = Arrangement.Top
    ) {

        /** DELETE DIALOG */
        if (showDeleteDialog.value) {
            PreventDialog(context = context,
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
            PreventDialog(context = context,
                type = DialogType.SaveEdits,
                hideDialog = { showSaveDialog.value = false },
                onCancel = { showSaveDialog.value = false }) {

                vm.saveEditedAlarm(context)
                vm.triggerEditableDetails()
                showSaveDialog.value = false

            }
        }

        /** BACK PRESSED YET DIALOG */
        if (state.showBackPressedDialog) {
            PreventDialog(context = context,
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
            PreventDialog(context = context,
                type = DialogType.Cancel,
                hideDialog = { showCancelDialog.value = false },
                onCancel = { showCancelDialog.value = false }) {
                vm.triggerEditableDetails()
                showCancelDialog.value = false
            }
        }

        /** LOW SECONDS AMOUNT DIALOG */
        if (showLowSecondAmountDialog.value) {
            PreventDialog(context = context,
                type = DialogType.LowSecondAmount,
                hideDialog = { showLowSecondAmountDialog.value = false },
                onCancel = {
                    showLowSecondAmountDialog.value = false
                    navController.popBackStack()
                    vm.clearNewAlarm()
                },
                onContinue = { showLowSecondAmountDialog.value = false })
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
                    status = if (state.isEditable) WheelPickerStatus.Enabled else WheelPickerStatus.Disabled,
                    highlightedNumbersColor = detailsUiColor
                )
            }
        }
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .height(150.dp)
                .padding(bottom = R.dimen.padding_large.dp)
                .padding(horizontal = R.dimen.padding_large.dp),
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
                    colors = TextFieldDefaults.textFieldColors(
                        focusedIndicatorColor = detailsUiColor,
                        focusedLabelColor = detailsUiColor,

                        unfocusedIndicatorColor = detailsUiColor,
                        backgroundColor = detailsUiColor.copy(alpha = 0.2f)
                    ),
                    keyboardActions = KeyboardActions(onDone = {
                        if (!focusManager.moveFocus(FocusDirection.Down)) {
                            focusManager.clearFocus()
                            keyboardController?.hide()
                        }
                    }),
                )
            } else {
                Text(text = currentAlarm.title, fontSize = 36.sp)
            }
        }

        AdditionalInfoCard(
            description = if (state.isEditable) state.newDescription else if (currentAlarm.description.isEmpty()) "Empty description" else currentAlarm.description,
            schedule = if (state.isEditable) state.newSchedule.ifEmpty { currentAlarm.schedule } else currentAlarm.schedule,
            onDescriptionChanged = {
                vm.updateEditedDescription(it)
            },
            onScheduleChanged = {
                vm.updateEditedSchedule(it)
            },
            focusManager,
            keyboardController,
            isEditable = state.isEditable,
            backgroundColor = additionalInfoCardUiColor
        )
    }

    /** FLOAT BUTTONS */
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(R.dimen.padding_large.dp),
        verticalArrangement = Arrangement.Bottom,
        horizontalAlignment = Alignment.End
    ) {

        IntervalFloatButton(
            function = {
                if (state.isEditable) {

                    if (isAnyInfoChanged) {

                        if (isIntervalValid) {
                            showSaveDialog.value = true
                        } else {
                            showLowSecondAmountDialog.value = true
                        }
                    } else {
                        vm.triggerEditableDetails()
                    }
                } else {
                    vm.triggerEditableDetails()
                    vm.updateEditedTitle(currentAlarm.title)
                    vm.updateEditedDescription(currentAlarm.description)
                }
            },
            isScheduled = currentAlarm.schedule.isNotEmpty(),
            hasIcon = true,
            icon = if (state.isEditable) Icons.Filled.Done else Icons.Filled.Edit,
            color = detailsUiColor
        )
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(R.dimen.padding_large.dp),
        verticalArrangement = Arrangement.Bottom,
        horizontalAlignment = Alignment.Start
    ) {

        IntervalFloatButton(
            function = { showDeleteDialog.value = true },
            isScheduled = currentAlarm.schedule.isNotEmpty(),
            hasIcon = true,
            icon = Icons.Filled.Delete,
            color = detailsUiColor
        )
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

        }
    }
}