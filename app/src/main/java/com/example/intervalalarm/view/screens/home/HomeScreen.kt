package com.example.intervalalarm.view.screens.home

import android.Manifest
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat.startActivity
import com.example.intervalalarm.model.permissions.IntervalPermissionManager
import com.example.intervalalarm.view.screens.home.components.AlarmList
import com.example.intervalalarm.view.common_components.IntervalFloatButton
import com.example.intervalalarm.view.screens.home.states.HomeScreenUiState
import com.example.intervalalarm.view.common_components.DialogType
import com.example.intervalalarm.view.common_components.PreventDialog
import com.example.intervalalarm.view.screens.home.states.AlarmUiState

@Composable
fun HomeScreen(
    state: HomeScreenUiState,
    openDetails: (Int) -> Unit,
    openAddNew: () -> Unit,
    triggerAlarm: (AlarmUiState) -> Unit,

    deleteAllAlarms: (Context) -> Unit
) {
    val context = LocalContext.current

    val list = state.allAlarms
    val enabledList = state.enabledAlarms
    val upcomingAlarm = state.upcomingAlarm
    val listState = rememberLazyListState()
    val isFolded = remember { derivedStateOf { listState.firstVisibleItemIndex > 1 } }

    val formattedInterval: String = when {
        (state.upcomingAlarm.hours > 0) -> "${state.upcomingAlarm.hours}h:${state.upcomingAlarm.minutes}m:${state.upcomingAlarm.seconds}s"
        (state.upcomingAlarm.hours == 0 && state.upcomingAlarm.minutes > 0) -> "${state.upcomingAlarm.minutes}m:${state.upcomingAlarm.seconds}s"
        else -> {
            "${state.upcomingAlarm.seconds}s"
        }
    }

    val showPermissionDialog = remember {
        mutableStateOf(false)
    }
    val isPermissionGranted = if (Build.VERSION.SDK_INT >= 33) IntervalPermissionManager().isPermissionGranted(
        context,
        Manifest.permission.POST_NOTIFICATIONS
    ) else true

    Column(
        modifier = Modifier.semantics { contentDescription = "Home Screen" }
            .fillMaxSize()
            .padding(12.dp), verticalArrangement = Arrangement.Top
    ) {
        Column(
            modifier = Modifier
                .animateContentSize(tween(100))
                .fillMaxWidth()
                .height(if (!isFolded.value) 250.dp else if (enabledList.isNotEmpty()) 80.dp else 40.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            AnimatedVisibility(
                visible = !isFolded.value, enter = slideInVertically()
            ) {
                Text(
                    text = if (enabledList.isNotEmpty()) {
                        "${upcomingAlarm.title} is incoming"
                    } else {
                        "No incoming alarms"
                    },
                    fontSize = (if (!isFolded.value) 40.sp else 28.sp),
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .padding(vertical = 22.dp)
                        .clickable { deleteAllAlarms(context) })
            }

            Text(text = if (enabledList.isNotEmpty()) "Interval is $formattedInterval" else "",
                fontSize = 28.sp,
                modifier = Modifier
                    .padding(vertical = 12.dp)
                    .clickable { deleteAllAlarms(context) })
        }

        AlarmList(
            list = list,
            listState = listState,
            openDetails = {
                openDetails(it)
            }) {
            state.allAlarms.find { certainAlarm ->
                certainAlarm.id == it
            }?.let { alarmState ->

                if (IntervalPermissionManager().isPermissionGranted(
                        context,
                        Manifest.permission.POST_NOTIFICATIONS
                    )
                ) {
                    triggerAlarm(alarmState)
                } else {
                    showPermissionDialog.value = true
                }
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize(),
        verticalArrangement = Arrangement.Bottom,
        horizontalAlignment = Alignment.End
    ) {

        if (showPermissionDialog.value) {
            PreventDialog(
                type = DialogType.PermissionRequired,
                hideDialog = { showPermissionDialog.value = false },
                onCancel = { showPermissionDialog.value = false }) {
                val intent = Intent()
                intent.action = "android.settings.APP_NOTIFICATION_SETTINGS"
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                intent.putExtra("android.provider.extra.APP_PACKAGE", context.packageName)
                startActivity(context, intent, null)
                showPermissionDialog.value = false
            }
        }

        IntervalFloatButton(
            function = {
                if (isPermissionGranted) {
                    openAddNew()
                } else { showPermissionDialog.value = true }
            }, hasIcon = true, isScheduled = false
        )
    }
}
