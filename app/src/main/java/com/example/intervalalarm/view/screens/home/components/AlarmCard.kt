package com.example.intervalalarm.view.screens.home.components

import android.content.res.Configuration
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.intervalalarm.ui.theme.IntervalAlarmTheme
import com.example.intervalalarm.view.screens.home.states.AlarmStatus
import com.example.intervalalarm.view.screens.home.states.AlarmUiState

@Composable
fun AlarmCard(
    alarm: AlarmUiState,
    openAlarmDetails: () -> Unit,
    triggerAlarmStatus: (id: String) -> Unit
){

    val buttonText = when (alarm.status) {
        AlarmStatus.Disabled -> "START NOW"
        AlarmStatus.Scheduled -> "SCHEDULED"
        AlarmStatus.Enabled -> "STOP IT"
    }

    val buttonColor = when (alarm.status){
        AlarmStatus.Disabled    -> MaterialTheme.colors.secondary.copy(alpha = 0.3f)
        AlarmStatus.Scheduled -> MaterialTheme.colors.secondary.copy(alpha = 0.6f)
        AlarmStatus.Enabled     -> MaterialTheme.colors.primary.copy(alpha = 0.6f)
    }

    val buttonBorderColor = buttonColor.copy(alpha = 0.1f)
    val primaryText = if (alarm.hours > 0) "${alarm.hours}h:${alarm.minutes}m:${alarm.seconds}s" else if (alarm.minutes > 0) "${alarm.minutes}m:${alarm.seconds}s" else "${alarm.seconds}s"
    val infoText = alarm.title.ifEmpty { "alarm no ${alarm.count}" }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { openAlarmDetails() },
        shape = RoundedCornerShape(34.dp),
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column( //Left (INFO) Column
                modifier = Modifier
                    .fillMaxWidth(0.5f)
                    .fillMaxHeight(),
                verticalArrangement = Arrangement.Center
            ) {
                Row( // Upper (TIME) Row
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight(0.55f),
                    verticalAlignment = Alignment.Bottom,
                    horizontalArrangement = Arrangement.End
                ) {
                    Row(// (TIME) Row
                        modifier = Modifier
                            .fillMaxWidth()
                            .fillMaxHeight(0.8f),
                        verticalAlignment = Alignment.Bottom,
                        horizontalArrangement = Arrangement.End
                    ) {
                        Text(
                            text = primaryText,
                            fontSize = 32.sp
                        )
                    }
                }

                Row( // Down (Schedule) Row
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight(),
                    verticalAlignment = Alignment.Top,
                    horizontalArrangement = Arrangement.End
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .fillMaxHeight(0.8f),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.End
                    ) {
                        Text(
                            modifier = Modifier,
                            text = infoText,
                            fontSize = 16.sp
                        )
                    }
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.End
            ) {

                Surface(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(10.dp)
                        .padding(start = 40.dp),
                    shape = RoundedCornerShape(50.dp),
                    elevation = 10.dp
                ) {

                    OutlinedButton(
                        onClick = { triggerAlarmStatus(alarm.id) },
                        shape = RoundedCornerShape(50.dp),
                        colors = ButtonDefaults.buttonColors(backgroundColor = buttonColor),
                        border = BorderStroke(Dp.Hairline, buttonBorderColor)
                    ) {
                        Text(fontWeight = FontWeight(400),
                            text = buttonText)
                    }
                }
            }
        }
    }
}


@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview(showBackground = true)
@Composable
fun DefaultPreview2() {
    IntervalAlarmTheme {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
//            AlarmCard({},{})
//            AlarmCard({},{})
//            AlarmCard({},{})
//            AlarmCard({},{})
        }
    }
}