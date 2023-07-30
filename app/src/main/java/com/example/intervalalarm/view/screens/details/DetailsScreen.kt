package com.example.intervalalarm.view.screens.details

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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.intervalalarm.view.common_components.AdditionalInfoCard
import com.example.intervalalarm.view.common_components.DialogType
import com.example.intervalalarm.view.common_components.PreventDialog
import com.example.intervalalarm.view.screens.details.states.DetailsScreenUiState
import com.example.intervalalarm.view.common_components.IntervalFloatButton
import com.example.intervalalarm.view.common_components.WheelIntervalPicker
import com.example.intervalalarm.view.screens.home.states.AlarmStatus
import com.example.intervalalarm.view.screens.home.states.AlarmUiState
import java.util.*

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun DetailsScreen(
    state: DetailsScreenUiState,

    popBackStack: () -> Unit,
    updateEditedTitle: (String) -> Unit,
    updateEditedDescription: (String) -> Unit,
    updateEditedSchedule: (String) -> Unit,

    updateDetailsWheelStateHour: (Int) -> Unit,
    updateDetailsWheelStateMinute: (Int) -> Unit,
    updateDetailsWheelStateSecond: (Int) -> Unit,


    deleteAlarm: (AlarmUiState) -> Unit,
    triggerEditableDetails: () -> Unit,
    saveEditedAlarm: () -> Unit,
    clearDetailsScreen: () -> Unit,
    hideBackPressedDetailsDialog: () -> Unit,
    onBackPressed: () -> Unit
) {
    /** UTILITIES */
    BackHandler(state.isEditable) { onBackPressed() }

    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current

    val showDeleteDialog = remember { mutableStateOf(false) }
    val showSaveDialog = remember { mutableStateOf(false) }
    val showCancelDialog = remember { mutableStateOf(false) }
    val showLowSecondAmountDialog = remember { mutableStateOf(false) }

    val isAnyInfoChanged =
        (
                (state.newTitle != state.chosenAlarm.title)
                        || (state.newDescription != state.chosenAlarm.description)
                        || (
                        (state.newSchedule != state.chosenAlarm.schedule)
                                && (state.newSchedule.isNotEmpty())
                        )
                        || (state.newWheelPickerValues.currentHour != state.chosenAlarm.hours)
                        || (state.newWheelPickerValues.currentMinute != state.chosenAlarm.minutes)
                        || (state.newWheelPickerValues.currentSecond != state.chosenAlarm.seconds)
                )

    val isIntervalValid =
        (state.newWheelPickerValues.currentHour != 0) || (state.newWheelPickerValues.currentMinute != 0) || (state.newWheelPickerValues.currentSecond > 14)


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
            .padding(12.dp), verticalArrangement = Arrangement.Top
    ) {

        /** DELETE DIALOG */
        if (showDeleteDialog.value) {
            PreventDialog(
                type = DialogType.DeleteAlarm,
                hideDialog = { showDeleteDialog.value = false },
                onCancel = { showDeleteDialog.value = false },
                onContinue = {
                    deleteAlarm(state.chosenAlarm)
                    showDeleteDialog.value = false
                    popBackStack()
                    clearDetailsScreen()
                })
        }

        /** SAVE DIALOG */
        if (showSaveDialog.value) {
            PreventDialog(
                type = DialogType.SaveEdits,
                hideDialog = { showSaveDialog.value = false },
                onCancel = { showSaveDialog.value = false }) {

                saveEditedAlarm()
                triggerEditableDetails()
                showSaveDialog.value = false

            }
        }

        /** BACK PRESSED YET DIALOG */
        if (state.showBackPressedDialog) {
            PreventDialog(
                type = DialogType.Cancel,
                hideDialog = { hideBackPressedDetailsDialog() },
                onCancel = { hideBackPressedDetailsDialog() }) {
                hideBackPressedDetailsDialog()
                clearDetailsScreen()
                popBackStack()
            }
        }

        /** CANCEL DIALOG */
        if (showCancelDialog.value) {
            PreventDialog(
                type = DialogType.Cancel,
                hideDialog = { showCancelDialog.value = false },
                onCancel = { showCancelDialog.value = false }) {
                triggerEditableDetails()
                showCancelDialog.value = false
            }
        }

        /** LOW SECONDS AMOUNT DIALOG */
        if (showLowSecondAmountDialog.value) {
            PreventDialog(
                type = DialogType.LowSecondAmount,
                hideDialog = { showLowSecondAmountDialog.value = false },
                onCancel = {
                    showLowSecondAmountDialog.value = false
                    clearDetailsScreen()
                    popBackStack()
                },
                onContinue = { showLowSecondAmountDialog.value = false })
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.4f),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxSize(), horizontalArrangement = Arrangement.Center
            ) {

                /** WHEEL PICKER */
                WheelIntervalPicker(

                    defaultHour = state.chosenAlarm.hours,
                    defaultMinute = state.chosenAlarm.minutes,
                    defaultSecond = state.chosenAlarm.seconds,

                    isEnabled = state.isEditable,
                    highlightedNumbersColor = if (state.isEditable) detailsUiColor else Color.Gray,

                    updateHour = {
                        updateDetailsWheelStateHour(it)
                    },
                    updateMinute = {
                        updateDetailsWheelStateMinute(it)
                    },
                    updateSecond = {
                        updateDetailsWheelStateSecond(it)
                    },
                )
            }
        }
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .height(150.dp)
                .padding(bottom = 22.dp)
                .padding(horizontal = 22.dp),
            verticalArrangement = Arrangement.Bottom,
            horizontalAlignment = Alignment.Start
        ) {

            if (state.isEditable) {

                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = if (state.newTitle != state.chosenAlarm.title) state.newTitle
                    else if (state.chosenAlarm.title == "Alarm no. ${state.chosenAlarm.count}") "" else state.chosenAlarm.title,
                    onValueChange = { updateEditedTitle(it) },
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
                Text(text = state.chosenAlarm.title, fontSize = 36.sp)
            }
        }

        AdditionalInfoCard(
            description = if (state.isEditable) state.newDescription else if (state.chosenAlarm.description.isEmpty()) "Empty description" else state.chosenAlarm.description,
            schedule = if (state.isEditable) state.newSchedule.ifEmpty { state.chosenAlarm.schedule } else state.chosenAlarm.schedule,
            onDescriptionChanged = {
                updateEditedDescription(it)
            },
            onScheduleChanged = {
                updateEditedSchedule(it)
            },
            focusManager,
            keyboardController,
            isEditable = state.isEditable,
            backgroundColor = additionalInfoCardUiColor
        )
    }

    /** FLOAT BUTTONS */
    Row(
        modifier = Modifier
            .fillMaxSize(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Bottom
    ) {

        IntervalFloatButton(
            function = { showDeleteDialog.value = true },
            isScheduled = state.chosenAlarm.schedule.isNotEmpty(),
            hasIcon = true,
            icon = Icons.Filled.Delete,
            color = detailsUiColor
        )

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
                        triggerEditableDetails()
                    }
                } else {
                    triggerEditableDetails()
                    updateEditedTitle(state.chosenAlarm.title)
                    updateEditedDescription(state.chosenAlarm.description)
                }
            },
            isScheduled = state.chosenAlarm.schedule.isNotEmpty(),
            hasIcon = true,
            icon = if (state.isEditable) Icons.Filled.Done else Icons.Filled.Edit,
            color = detailsUiColor
        )
    }
}