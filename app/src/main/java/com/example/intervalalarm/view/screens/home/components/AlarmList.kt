package com.example.intervalalarm.view.screens.home.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.intervalalarm.view.screens.home.states.AlarmUiState

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun AlarmList(
    list: List<AlarmUiState>,
    listState: LazyListState,
    openDetails: (Int) -> Unit,
    triggerStatus: (id: String) -> Unit
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
                        .fillMaxWidth()
                        .height(120.dp)
                        .padding(top = 22.dp)
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