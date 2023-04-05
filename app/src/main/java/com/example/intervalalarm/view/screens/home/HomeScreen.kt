package com.example.intervalalarm.view.screens.home

import android.os.CountDownTimer
import android.util.Log
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.intervalalarm.model.module.timer.CurrentAlarmTimer
import com.example.intervalalarm.view.screens.home.components.AddNewAlarmButton
import com.example.intervalalarm.view.screens.home.components.AlarmList
import com.example.intervalalarm.view.screens.home.states.AlarmStatus
import com.example.intervalalarm.view.screens.home.states.HomeScreenUiState
import com.example.intervalalarm.view.screens.navigation.Screens
import com.example.intervalalarm.view.screens.new_alarm.components.DialogType
import com.example.intervalalarm.view.screens.new_alarm.components.PreventDialog
import com.example.intervalalarm.viewmodel.MainViewModel

@Composable
fun HomeScreen(
    vm: MainViewModel, navController: NavController, state: HomeScreenUiState
) {
    val context = LocalContext.current

    val list = state.allAlarms
    val enabledList = state.enabledAlarms
    val upcomingAlarm = state.upcomingAlarm
    val listState = rememberLazyListState()
    val isFolded = remember { derivedStateOf { listState.firstVisibleItemIndex > 1 } }

    val formattedInterval: String = when {
        (state.upcomingAlarm.hours > 0) -> "${state.upcomingAlarm.hours}:${state.upcomingAlarm.minutes}:${state.upcomingAlarm.seconds}"
        (state.upcomingAlarm.hours == 0 && state.upcomingAlarm.minutes > 0) -> "${state.upcomingAlarm.minutes}:${state.upcomingAlarm.seconds}"
        else -> {
            "${state.upcomingAlarm.seconds}"
        }
    }
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
                Text(text = if (enabledList.isNotEmpty()) {
                    if (upcomingAlarm.title.isNotEmpty()) {
                        upcomingAlarm.title
                    } else {
                        "Alarm no. ${upcomingAlarm.count}"
                    } + " is incoming"

                } else {
                    "No incoming alarms"
                },
                    fontSize = (if (!isFolded.value) 40.sp else 28.sp),
                    modifier = Modifier
                        .padding(vertical = 20.dp)
                        .clickable { vm.deleteAllAlarms(context) })
            }

            Text(text = if (enabledList.isNotEmpty()) "Interval is " + formattedInterval else "",
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
                vm.triggerAlarm(context = context, alarmState)
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        verticalArrangement = Arrangement.Bottom,
        horizontalAlignment = Alignment.End
    ) {

        AddNewAlarmButton(
            addNew = {
                navController.navigate(Screens.NewAlarmScreen.route)
            }, hasIcon = true, isScheduled = false
        )
    }
}
