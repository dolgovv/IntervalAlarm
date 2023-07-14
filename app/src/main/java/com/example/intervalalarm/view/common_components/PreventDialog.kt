package com.example.intervalalarm.view.common_components

import android.content.Context
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.example.intervalalarm.R

@Composable
fun PreventDialog(
    context: Context,
    type: DialogType,
    hideDialog: () -> Unit,
    onCancel: () -> Unit,
    onContinue: () -> Unit,
) {
    val dismissButtonColor = MaterialTheme.colors.secondary
    val confirmButtonColor = MaterialTheme.colors.secondaryVariant

    val title: String = when (type) {
        DialogType.LowSecondAmount -> context
            .getString(R.string.low_second_amount_dialog_title)

        DialogType.ZeroInterval -> context
            .getString(R.string.zero_interval_dialog_title)

        DialogType.DeleteAlarm -> context
            .getString(R.string.delete_alarm_dialog_title)

        DialogType.SaveEdits -> context
            .getString(R.string.save_edited_alarm_dialog_title)

        DialogType.Cancel -> context
            .getString(R.string.cancel_edits_dialog_title)

        DialogType.PassedTimeChosen -> context
            .getString(R.string.passed_time_chosen_dialog_title)

        DialogType.PermissionRequired -> context
            .getString(R.string.permission_required_dialog_title)
    }

    val message: String = when (type) {
        DialogType.LowSecondAmount -> context
            .getString(R.string.low_second_amount_dialog_message)

        DialogType.ZeroInterval -> context
            .getString(R.string.zero_interval_dialog_message)

        DialogType.DeleteAlarm -> context
            .getString(R.string.delete_alarm_dialog_message)

        DialogType.SaveEdits -> context
            .getString(R.string.save_edited_alarm_dialog_message)

        DialogType.Cancel -> context
            .getString(R.string.cancel_edits_dialog_message)

        DialogType.PassedTimeChosen -> context
            .getString(R.string.passed_time_chosen_dialog_message)

        DialogType.PermissionRequired -> context
            .getString(R.string.permission_required_dialog_message)
    }

    AlertDialog(
        modifier = Modifier.semantics { contentDescription = "Prevent Dialog: Window" },
        shape = RoundedCornerShape(14.dp),
        onDismissRequest = { hideDialog() },
        confirmButton = {
            Button(
                modifier = Modifier.semantics { contentDescription = "Prevent Dialog: Continue" },
                shape = RoundedCornerShape(50.dp),
                colors = ButtonDefaults.buttonColors(backgroundColor = confirmButtonColor),
                onClick = {
                    onContinue()
                }) {
                Text(
                    text = if (
                        type == DialogType.PassedTimeChosen
                        || type == DialogType.DeleteAlarm
                        || type == DialogType.LowSecondAmount
                        || type == DialogType.ZeroInterval
                        || type == DialogType.PermissionRequired
                    ) "Ok" else "Yes"
                )
            }
        },
        dismissButton = {
            Button(
                modifier = Modifier.semantics { contentDescription = "Prevent Dialog: Cancel" },
                shape = RoundedCornerShape(50.dp),
                colors = ButtonDefaults.buttonColors(backgroundColor = dismissButtonColor),
                onClick = {
                    onCancel()
                    hideDialog()
                }) {
                Text(text = if (type == DialogType.Cancel) "Continue editing" else "Cancel")
            }
        },
        title = { Text(text = title) },
        text = { Text(text = message) },
    )
}

sealed class DialogType {
    object LowSecondAmount : DialogType()
    object ZeroInterval : DialogType()
    object DeleteAlarm : DialogType()
    object SaveEdits : DialogType()
    object Cancel : DialogType()
    object PassedTimeChosen : DialogType()
    object PermissionRequired : DialogType()
}