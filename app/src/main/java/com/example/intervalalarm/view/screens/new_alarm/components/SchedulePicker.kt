package com.example.intervalalarm.view.screens.new_alarm.components

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.window.Dialog

@Composable
fun SchedulePicker(context: Context, initHour: Int, initMinute: Int){
    val time = remember { mutableStateOf("") }
    val timePickerDialog = TimePickerDialog(
        context,
        {_, hour : Int, minute: Int ->
            time.value = "$hour:$minute"
        }, initHour, initMinute, false
    )
    OutlinedButton(onClick = {
        timePickerDialog.show()
    }) {
        Text(text = "Open Time Picker")
    }
}