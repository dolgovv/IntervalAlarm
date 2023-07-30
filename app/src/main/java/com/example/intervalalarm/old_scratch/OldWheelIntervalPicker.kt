package com.example.intervalalarm.old_scratch

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.intervalalarm.view.screens.new_alarm.states.WheelPickerUiState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun OldWheelIntervalPicker(
    state: WheelPickerUiState,

    updateHour: (Int) -> Unit,
    updateMinute: (Int) -> Unit,
    updateSeconds: (Int) -> Unit,

    defaultHour: Int = 0,
    defaultMinute: Int = 0,
    defaultSecond: Int = 0,

    status: WheelPickerStatus = WheelPickerStatus.Enabled,
    highlightedNumbersColor: Color = MaterialTheme.colors.primary
) {
    val isEnabled = status == WheelPickerStatus.Enabled

    val hours: List<Int> = ((-1..24).map { it })
    val minutes: List<Int> = ((-1..60).map { it })
    val seconds: List<Int> = ((-1..60).map { it })

    val hoursListState = rememberLazyListState()
    val minutesListState = rememberLazyListState()
    val secondsListState = rememberLazyListState()

    val swipeCoroutine = rememberCoroutineScope()
    val touchCoroutine = rememberCoroutineScope()

    val highlightedHour = remember { mutableStateOf(state.currentHour) }
    val highlightedMinute = remember { mutableStateOf(state.currentMinute) }
    val highlightedSecond = remember { mutableStateOf(state.currentSecond) }

    if (!isEnabled) {
        LaunchedEffect(key1 = state.currentHour) {
//            withContext(Dispatchers.Default) {
                hoursListState.scrollToItem(defaultHour)
//            }
        }
        LaunchedEffect(key1 = state.currentMinute) {
//            withContext(Dispatchers.Default) {
                minutesListState.scrollToItem(defaultMinute)
//            }
        }
        LaunchedEffect(key1 = state.currentSecond) {
//            withContext(Dispatchers.Default) {
                secondsListState.scrollToItem(defaultSecond)
//            }
        }
    }

    val secondaryNumbersColor = if (isEnabled) Color.Gray else MaterialTheme.colors.background

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(230.dp)
            .padding(horizontal = 22.dp)
            .padding(vertical = 12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize(),
            horizontalArrangement = Arrangement.Center
        ) {

            Row(
                modifier = Modifier
                    .fillMaxWidth(0.33f)
                    .fillMaxHeight(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {

                WheelCoroutine(
                    listState = hoursListState, swipeCoroutine = swipeCoroutine,
                    updateHighlighted = { highlightedHour.value = it },
                    updateState = { updateHour(it) },
                    isEnabled = isEnabled
                )

                if (isEnabled) {
                    LazyColumn(
                        state = hoursListState
                    ) {
                        items(
                            items = hours,
                            key = { it.toString() },
                        ) { item ->

                            Column(
                                modifier = Modifier
                                    .size(70.dp)
                                    .clickable(enabled = item in 0..23) {
                                        if (state.currentHour != item) {
                                            updateHour(item)
                                            touchCoroutine.launch {
                                                hoursListState.animateScrollToItem(item)
                                            }
                                        }
                                    },
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {

                                Text(
                                    text = if (item in 0..23) item.toString() else "",
                                    fontSize = 46.sp,
                                    color = if (highlightedHour.value == item) highlightedNumbersColor else secondaryNumbersColor
                                )
                                Divider()
                            }
                        }
                    }
                } else {

                    Column(
                        modifier = Modifier
                            .size(70.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {

                        Text(
                            text = defaultHour.toString(), fontSize = 46.sp,
                            color = highlightedNumbersColor
                        )
                    }
                }

                Text(text = ":", fontSize = 50.sp)
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth(0.5f)
                    .fillMaxHeight(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {

                WheelCoroutine(
                    listState = minutesListState, swipeCoroutine = swipeCoroutine,
                    updateHighlighted = { highlightedMinute.value = it },
                    updateState = { updateMinute(it) })

                if (isEnabled) {
                    LazyColumn(state = minutesListState) {
                        items(
                            items = minutes,
                            key = { it.toString() },
                        ) { item ->
                            Column(
                                modifier = Modifier
                                    .size(70.dp)
                                    .clickable(enabled = item in 0..59) {
                                        if (state.currentMinute != item) {
                                            updateMinute(item)
                                            touchCoroutine.launch {
                                                minutesListState.animateScrollToItem(item)
                                            }
                                        }
                                    },
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {

                                Text(
                                    text = if (item in 0..59) item.toString() else "",
                                    fontSize = 46.sp,
                                    color = if (highlightedMinute.value == item) highlightedNumbersColor else secondaryNumbersColor
                                )
                                Divider()
                            }
                        }
                    }
                } else {

                    Column(
                        modifier = Modifier
                            .size(70.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {

                        Text(
                            text = defaultMinute.toString(), fontSize = 46.sp,
                            color = highlightedNumbersColor
                        )
                    }
                }

                Text(text = ":", fontSize = 50.sp)
            }

            Row(
                modifier = Modifier
                    .fillMaxSize(),
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically
            ) {

                WheelCoroutine(
                    listState = secondsListState, swipeCoroutine = swipeCoroutine,
                    updateHighlighted = { highlightedSecond.value = it },
                    updateState = { updateSeconds(it) })

                if (isEnabled) {
                    LazyColumn(state = secondsListState) {
                        items(
                            items = seconds,
                            key = { it.toString() },
                        ) { item ->
                            Column(
                                modifier = Modifier
                                    .size(70.dp)
                                    .clickable(enabled = item in 0..59) {
                                        if (state.currentSecond != item) {
                                            updateSeconds(item)
                                            touchCoroutine.launch {
                                                secondsListState.animateScrollToItem(item)
                                            }
                                        }
                                    },
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {

                                Text(
                                    text = if (item in 0..59) item.toString() else "",
                                    fontSize = 46.sp,
                                    color = if (highlightedSecond.value == item) highlightedNumbersColor else secondaryNumbersColor
                                )
                                Divider()
                            }
                        }
                    }
                } else {

                    Column(
                        modifier = Modifier
                            .size(70.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {

                        Text(
                            text = defaultSecond.toString(), fontSize = 46.sp,
                            color = highlightedNumbersColor
                        )
                    }
                }
                Text(text = "", fontSize = 50.sp)
            }
        }
    }
}

@Composable
fun WheelCoroutine(
    listState: LazyListState,
    swipeCoroutine: CoroutineScope,
    updateHighlighted: (Int) -> Unit,
    updateState: (Int) -> Unit,
    isEnabled: Boolean = true
) {
    if (isEnabled) {
        LaunchedEffect(key1 = listState.firstVisibleItemIndex, listState.isScrollInProgress) {
            updateHighlighted(listState.firstVisibleItemIndex)

                delay(100)
                swipeCoroutine.launch {
                    listState.animateScrollToItem(listState.firstVisibleItemIndex)
                    updateState(listState.firstVisibleItemIndex)
                    Log.d("wheelcheck", "upd from newWheelCoroutine")
                }

        }
    } else {
        LaunchedEffect(key1 = listState.firstVisibleItemIndex) {
            swipeCoroutine.launch {
                listState.scrollToItem(listState.firstVisibleItemIndex)
                updateState(listState.firstVisibleItemIndex)
            }
        }
    }
}

sealed class WheelPickerStatus {
    object Disabled : WheelPickerStatus()
    object Enabled : WheelPickerStatus()
}