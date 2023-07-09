package com.example.intervalalarm.view.screens.home

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.widget.Toast
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat.checkSelfPermission
import androidx.core.content.ContextCompat.startActivity
import androidx.navigation.NavController
import com.example.intervalalarm.R
import com.example.intervalalarm.model.permissions.IntervalPermissionManager
import com.example.intervalalarm.view.screens.home.components.AlarmList
import com.example.intervalalarm.view.common_components.IntervalFloatButton
import com.example.intervalalarm.view.screens.home.states.HomeScreenUiState
import com.example.intervalalarm.model.navigation.Screens
import com.example.intervalalarm.view.common_components.DialogType
import com.example.intervalalarm.view.common_components.PreventDialog
import com.example.intervalalarm.viewmodel.MainViewModel

@Composable
fun HomeScreen(
    vm: MainViewModel, navController: NavController,
    state: HomeScreenUiState
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

    val isPermissionsGranted =
        if (Build.VERSION.SDK_INT >= 33) {

            checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
                    && checkSelfPermission(
                context,
                Manifest.permission.SCHEDULE_EXACT_ALARM
            ) == PackageManager.PERMISSION_GRANTED

        } else if (Build.VERSION.SDK_INT >= 31) {
            checkSelfPermission(
                context,
                Manifest.permission.SCHEDULE_EXACT_ALARM
            ) == PackageManager.PERMISSION_GRANTED

        } else {
            true
        }

    val showPermissionDialog = remember {
        mutableStateOf(false)
    }
    val isPerm = if (Build.VERSION.SDK_INT >= 33) IntervalPermissionManager().isPermissionGranted(
        context,
        Manifest.permission.POST_NOTIFICATIONS
    ) else true

    Column(
        modifier = Modifier
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
                        .clickable { vm.deleteAllAlarms(context) })
            }

            Text(text = if (enabledList.isNotEmpty()) "Interval is $formattedInterval" else "",
                fontSize = 28.sp,
                modifier = Modifier
                    .padding(vertical = 12.dp)
                    .clickable { vm.deleteAllAlarms(context) })
        }

        AlarmList(list = list,
            listState = listState,
            openDetails = { navController.navigate(Screens.DetailsScreen.withArgs(it)) }) {
            state.allAlarms.find { certainAlarm ->
                certainAlarm.id == it
            }?.let { alarmState ->

                vm.triggerAlarm(context = context, alarmState, infoToast = {
                    Toast.makeText(
                        context,
                        "Schedule cleared!",
                        Toast.LENGTH_SHORT
                    ).show()
                },
                    showPermissionDialog = { showPermissionDialog.value = true })
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(22.dp),
        verticalArrangement = Arrangement.Bottom,
        horizontalAlignment = Alignment.End
    ) {

        if (showPermissionDialog.value) {
            PreventDialog(
                context = context,
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
                if (isPerm) {
                    navController.navigate(Screens.NewAlarmScreen.route)
                } else {
                    showPermissionDialog.value = true
                }
            }, hasIcon = true, isScheduled = false
        )
    }
}
