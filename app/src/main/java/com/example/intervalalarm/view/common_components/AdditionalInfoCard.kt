package com.example.intervalalarm.view.common_components

import android.app.TimePickerDialog
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.SoftwareKeyboardController
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import java.time.LocalDateTime
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
    isEditable: Boolean = true,
    backgroundColor: Color = MaterialTheme.colors.primary
) {
    val context = LocalContext.current

    val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")

    val showPastTimeDialog = remember { mutableStateOf(false) }

    var chosenDate = LocalDateTime.now()
    val timePickerDialog = TimePickerDialog(
        context,
        { _, hour: Int, minute: Int ->

            if (hour > chosenDate.hour || (hour >= chosenDate.hour && minute > chosenDate.minute)) {

                chosenDate = LocalDateTime.of(
                    chosenDate.year,
                    chosenDate.month,
                    chosenDate.dayOfMonth,
                    hour,
                    minute
                )
                onScheduleChanged(chosenDate.toString())
            } else {
                showPastTimeDialog.value = true
            }
        }, chosenDate.hour, chosenDate.minute, false
    )

    if (showPastTimeDialog.value) {
        PreventDialog(
            type = DialogType.PassedTimeChosen,
            hideDialog = { showPastTimeDialog.value = false },
            onCancel = { showPastTimeDialog.value = false }) {
            showPastTimeDialog.value = false
        }
    }

    Column(
        modifier = Modifier.semantics { contentDescription = "Additional Info Card: Window" },
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.Start
    ) {

        /** DESCRIPTION PICKER */
        TextField(
            modifier = Modifier
                .semantics {
                    contentDescription = "Prevent Dialog: Description Field"
                }
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
                backgroundColor = backgroundColor,
                focusedIndicatorColor = backgroundColor,
                focusedLabelColor = backgroundColor
            ),
            placeholder = { if (isEditable) Text(text = "Description") },
            value = description,
            onValueChange = {
                if (it.contains("\n")) {
                    keyboardController?.hide()
                } else {
                    onDescriptionChanged(it)
                }
            },
            enabled = isEditable,
            readOnly = !isEditable
        )

        /** SCHEDULE PICKER */
        TextField(
            modifier = Modifier
                .semantics { contentDescription = "Prevent Dialog: Schedule Field" }
                .clickable(enabled = isEditable) {
                    timePickerDialog.show()
                }
                .height(50.dp)
                .fillMaxWidth(),
            colors = TextFieldDefaults.textFieldColors(
                backgroundColor = backgroundColor,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                disabledIndicatorColor = Color.Transparent
            ),
            enabled = false,
            readOnly = true,
            shape = MaterialTheme.shapes.small.copy(
                bottomStart = CornerSize(22.dp),
                bottomEnd = CornerSize(22.dp)
            ),
            label = {
                Text(
                    text = if (schedule.isEmpty()) {
                        "Empty schedule"
                    } else {
                        "Rings at " + LocalDateTime.parse(
                            schedule,
                            DateTimeFormatter.ISO_LOCAL_DATE_TIME
                        ).format(timeFormatter).toString()
                    }
                )
            },
            value = "", onValueChange = { })
    }
}