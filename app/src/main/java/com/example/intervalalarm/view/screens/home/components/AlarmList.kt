package com.example.intervalalarm.view.screens.home.components

import android.content.res.Configuration
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.intervalalarm.R
import com.example.intervalalarm.view.theme.IntervalAlarmTheme
import com.example.intervalalarm.view.screens.home.states.AlarmStatus
import com.example.intervalalarm.view.screens.home.states.AlarmUiState

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun AlarmList(
    list: List<AlarmUiState> = listOf(
        AlarmUiState(
            count = 1,
            description = LocalContext.current.getString(R.string.loren_ipsum)
        ),
        AlarmUiState(count = 2, status = AlarmStatus.Scheduled,
                description = LocalContext.current.getString(R.string.loren_ipsum)),

        AlarmUiState(
            count = 3,
            status = AlarmStatus.Enabled,
            description = LocalContext.current.getString(R.string.loren_ipsum)
        )
    ),
    listState: LazyListState = rememberLazyListState(),
    openDetails: (Int) -> Unit = {},
    triggerStatus: (id: String) -> Unit = {}
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        LazyColumn(state = listState) {
            items(
                items = list,
                key = { it.count },
            ) { item ->

                Row(
                    modifier = Modifier
                        .padding(top = 12.dp)
                        .fillMaxWidth()
                        .wrapContentHeight()
                        .clip(RoundedCornerShape(34.dp))
                        .animateItemPlacement()
                ) {

                    AlarmCard(
                        alarm = item,
                        openAlarmDetails = { openDetails(item.count) },
                        triggerAlarmStatus = {
                            triggerStatus(it)
                        }
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview(showBackground = true)
@Composable
fun Man() {
    IntervalAlarmTheme {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            AlarmList()
        }
    }
}