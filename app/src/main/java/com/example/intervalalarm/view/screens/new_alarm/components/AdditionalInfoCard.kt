package com.example.intervalalarm.view.screens.new_alarm.components

import android.app.TimePickerDialog
import android.content.res.Configuration
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CornerBasedShape
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.SoftwareKeyboardController
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.intervalalarm.ui.theme.IntervalAlarmTheme
import com.example.intervalalarm.view.screens.new_alarm.NewAlarmScreen
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun AdditionalInfoCard(
    description: String,
    schedule: String,
    onDescriptionChanged: (String) -> Unit,
    onScheduleChanged: (String) -> Unit,
    focusManager: FocusManager,
    keyboardController: SoftwareKeyboardController?,
    isEnabled: Boolean = true
) {
    val context = LocalContext.current

    val newSchedule = remember { mutableStateOf("") }
    val testFormatter = DateTimeFormatter.ofPattern("HH:mm")

    val showPastTimeDialog = remember { mutableStateOf(false) }

    var chosenDate = LocalDateTime.now()
    val timePickerDialog = TimePickerDialog(
        context,
        { _, hour: Int, minute: Int ->

            if (hour > chosenDate.hour  || (hour >= chosenDate.hour && minute > chosenDate.minute)) {

                chosenDate = LocalDateTime.of(
                    chosenDate.year,
                    chosenDate.month,
                    chosenDate.dayOfMonth,
                    hour,
                    minute
                )
                newSchedule.value = chosenDate.format(testFormatter)
                onScheduleChanged(chosenDate.toString())
            } else {
                showPastTimeDialog.value = true
            }
        }, chosenDate.hour, chosenDate.minute, false
    )

    if (showPastTimeDialog.value) {
        PreventDialog(context = context,
            type = DialogType.PassedTimeChosen,
            hideDialog = { showPastTimeDialog.value = false },
            onCancel = { showPastTimeDialog.value = false }) {
            showPastTimeDialog.value = false
        }
    }
    Column(
        modifier = Modifier,
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.Start
    ) {
        TextField(
            modifier = Modifier
                .clip(MaterialTheme.shapes.medium)
                .fillMaxWidth(),
            maxLines = 15,
            keyboardActions = KeyboardActions(onDone = {
                if (!focusManager.moveFocus(FocusDirection.Down)) {
                    focusManager.clearFocus()
                    keyboardController?.hide()
                }
            }),
            shape = MaterialTheme.shapes.small.copy(
                topStart = CornerSize(22.dp),
                topEnd = CornerSize(22.dp)
            ),
            colors = TextFieldDefaults.textFieldColors(
                backgroundColor = if (isEnabled) Color(MaterialTheme.colors.primary.value) else Color(
                    MaterialTheme.colors.primary.copy(0.5f).value
                ),
            ),
//            label = { if (isEnabled) Text(text = "Description") },
            placeholder = { if (isEnabled) Text(text = "Description") },
            value = description,
            onValueChange = {
                if (it.contains("\n")) {
                    keyboardController?.hide()
                } else {
                    onDescriptionChanged(it)
                }
            },
            enabled = isEnabled,
            readOnly = !isEnabled
        )

        TextField(
            modifier = Modifier
                .clickable {
                    if (isEnabled) {
                        timePickerDialog.show()
                    }
                }
                .height(50.dp)
                .fillMaxWidth(),
            colors = TextFieldDefaults.textFieldColors(
                backgroundColor = if (isEnabled) Color(MaterialTheme.colors.primary.value) else Color(
                    MaterialTheme.colors.primary.copy(0.5f).value
                ),
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                disabledIndicatorColor = Color.Transparent
            ),
            enabled = false,
            readOnly = !isEnabled,
            shape = MaterialTheme.shapes.small.copy(
                bottomStart = CornerSize(22.dp),
                bottomEnd = CornerSize(22.dp)
            ),
            label = {
                Text(
                    text = if (schedule.isEmpty() && newSchedule.value.isEmpty()) {
                        "Empty schedule"
                    } else {
                        "Rings at ${
                            newSchedule.value.ifEmpty {
                                LocalDateTime.parse(schedule, DateTimeFormatter.ISO_LOCAL_DATE_TIME)
                                    .format(testFormatter)
                            }
                        }"
                    }
                )
            },
            value = "", onValueChange = { })
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun lightPrev() {
    IntervalAlarmTheme {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(5.dp)
        ) {
//            AdditionalInfoCard("", {}, {})
        }
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Preview(showBackground = true)
@Composable
fun darkPrev() {
    IntervalAlarmTheme {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(5.dp)
        ) {
//            AdditionalInfoCard("", {}, {})
        }
    }
}