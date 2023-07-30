package com.example.intervalalarm.view.common_components

import androidx.compose.foundation.background
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
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch

@Composable
fun WheelIntervalPicker(
    isEnabled: Boolean = true,
    highlightedNumbersColor: Color = MaterialTheme.colors.primary,

    defaultHour: Int = 0,
    defaultMinute: Int = 0,
    defaultSecond: Int = 0,

    updateHour: (Int) -> Unit,
    updateMinute: (Int) -> Unit,
    updateSecond: (Int) -> Unit
) {

    // UI CUSTOMIZE
    val fontSize = 46.sp
    val secondaryNumbersColor = if (isEnabled) Color.Gray else MaterialTheme.colors.background

    val hours: List<Int> = ((-1..25).map { it })
    val minutes: List<Int> = ((-1..60).map { it })
    val seconds: List<Int> = ((-1..60).map { it })

    val hoursListState = rememberLazyListState()
    val minuteListState = rememberLazyListState()
    val secondsListState = rememberLazyListState()

    val chosenHour = remember { mutableStateOf(defaultHour) }
    val chosenMinute = remember { mutableStateOf(defaultMinute) }
    val chosenSecond = remember { mutableStateOf(defaultSecond) }

    val swipeCoroutine = rememberCoroutineScope()
    val touchCoroutine = rememberCoroutineScope()

    NewWheelAutoScroll(
        hoursListState,
        swipeCoroutine,
        { chosenHour.value = it },
        { updateHour(it) },
        defaultHour,
        isEnabled
    )
    NewWheelAutoScroll(
        minuteListState,
        swipeCoroutine,
        { chosenMinute.value = it },
        { updateMinute(it) },
        defaultMinute,
        isEnabled
    )
    NewWheelAutoScroll(
        secondsListState,
        swipeCoroutine,
        { chosenSecond.value = it },
        { updateSecond(it) },
        defaultSecond,
        isEnabled
    )

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(fontSize.value.dp * 4 + 4.dp)
            .background(Color.Gray)
    ) {

        Row(
            modifier = Modifier.fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {

            LazyColumn(
                modifier = Modifier.semantics {
                    contentDescription = "Wheel Picker: Hour Lazy Column"
                },
                state = hoursListState,
            ) {
                items(items = hours, key = { it.toString() }) { item ->

                    Column(
                        modifier = Modifier
                            .semantics { contentDescription = "Hour $item" }
                            .clickable(enabled = isEnabled) {
                                if (item in 0..23) {
                                    touchCoroutine.launch {
                                        hoursListState.animateScrollToItem(item)
                                    }
                                }
                            }
                            .padding(horizontal = 12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {

                        Column(
                            Modifier,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {

                            Text(
                                text = if (item in 0..23) item.toString() else "",
                                fontSize = fontSize,
                                color = if (item == chosenHour.value) highlightedNumbersColor else secondaryNumbersColor
                            )
                            Divider(
                                Modifier.width(40.dp),
                                color = if (isEnabled) Color.Gray else MaterialTheme.colors.background
                            )
                        }
                    }
                }
            }

            Text(modifier = Modifier.wrapContentSize(), text = ":", fontSize = fontSize)

            LazyColumn(
                modifier = Modifier.semantics {
                    contentDescription = "Wheel Picker: Minute Lazy Column"
                },
                state = minuteListState,
                userScrollEnabled = isEnabled
            ) {
                items(items = minutes, key = { it.toString() }) { item ->

                    Column(
                        modifier = Modifier
                            .semantics { contentDescription = "Minute $item" }
                            .clickable(enabled = isEnabled) {
                                if (item in 0..59) {
                                    touchCoroutine.launch {
                                        minuteListState.animateScrollToItem(item)
                                    }
                                }
                            }
                            .padding(horizontal = 12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {

                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {

                            Text(
                                text = if (item in 0..59) item.toString() else "",
                                fontSize = fontSize,
                                color = if (item == chosenMinute.value) highlightedNumbersColor else secondaryNumbersColor
                            )
                            Divider(
                                Modifier.width(40.dp),
                                color = if (isEnabled) Color.Gray else MaterialTheme.colors.background
                            )
                        }
                    }
                }
            }
            Text(modifier = Modifier.wrapContentSize(), text = ":", fontSize = fontSize)

            LazyColumn(
                modifier = Modifier.semantics {
                    contentDescription = "Wheel Picker: Second Lazy Column"
                },
                state = secondsListState,
                userScrollEnabled = isEnabled
            ) {
                items(items = seconds, key = { it.toString() }) { item ->

                    Column(
                        modifier = Modifier
                            .semantics { contentDescription = "Second $item" }
                            .clickable(enabled = isEnabled) {
                                if (item in 0..59) {
                                    touchCoroutine.launch {
                                        secondsListState.animateScrollToItem(item)
                                    }
                                }
                            }
                            .padding(horizontal = 12.dp),
                        horizontalAlignment = Alignment.Start
                    ) {

                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {

                            Text(
                                text = if (item in 0..59) item.toString() else "",
                                fontSize = fontSize,
                                color = if (item == chosenSecond.value) highlightedNumbersColor else secondaryNumbersColor
                            )
                            Divider(
                                Modifier.width(40.dp),
                                color = if (isEnabled) Color.Gray else MaterialTheme.colors.background
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun NewWheelAutoScroll(
    listState: LazyListState,
    swipeCoroutine: CoroutineScope,
    updateHighlighted: (Int) -> Unit,
    updateState: (Int) -> Unit,
    defaultValue: Int,
    isEnabled: Boolean
) {

    LaunchedEffect(defaultValue) {
        snapshotFlow { listState.firstVisibleItemIndex }.distinctUntilChanged()
            .collect { updateHighlighted(it) }
    }

    if (isEnabled) {

        LaunchedEffect(key1 = !listState.isScrollInProgress) {
            if (!listState.isScrollInProgress) {
                delay(250)
                swipeCoroutine.launch {
                    listState.animateScrollToItem(listState.firstVisibleItemIndex)
                    updateState(listState.firstVisibleItemIndex)
                }
            }
        }

    } else {
        LaunchedEffect(key1 = defaultValue) {
            swipeCoroutine.launch {
                listState.scrollToItem(defaultValue)
            }
        }
    }
}