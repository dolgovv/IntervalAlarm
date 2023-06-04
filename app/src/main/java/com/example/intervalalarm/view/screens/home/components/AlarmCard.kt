package com.example.intervalalarm.view.screens.home.components

import android.content.res.Configuration
import android.content.res.Resources
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.intervalalarm.R
import com.example.intervalalarm.ui.theme.IntervalAlarmTheme
import com.example.intervalalarm.view.screens.home.states.AlarmStatus
import com.example.intervalalarm.view.screens.home.states.AlarmUiState

@Composable
fun AlarmCard(
    alarm: AlarmUiState, openAlarmDetails: () -> Unit, triggerAlarmStatus: (id: String) -> Unit
) {

    val context = LocalContext.current

    val buttonText = when (alarm.status) {
        AlarmStatus.Disabled -> "START NOW"
        AlarmStatus.Enabled -> "STOP IT"
        AlarmStatus.Scheduled -> "SCHEDULED"
    }

    val colorEnabled = MaterialTheme.colors.primary
    val colorDisabled = MaterialTheme.colors.secondary
    val colorScheduled = MaterialTheme.colors.secondaryVariant

    val statusColor = when (alarm.status) {
        AlarmStatus.Disabled -> colorDisabled
        AlarmStatus.Scheduled -> colorScheduled
        AlarmStatus.Enabled -> colorEnabled
    }

    val textColor = MaterialTheme.colors.onSecondary

    val buttonBackgroundColor =
        if (alarm.status == AlarmStatus.Disabled) statusColor.copy(alpha = 0.2f)
        else statusColor.copy(alpha = 0.6f)

    val buttonBorderWidth = when (alarm.status) {
        AlarmStatus.Disabled -> Dp.Hairline
        AlarmStatus.Scheduled -> 2.dp
        AlarmStatus.Enabled -> 2.dp
    }
    val buttonBorderColor =
        if (alarm.status == AlarmStatus.Disabled) statusColor.copy(alpha = 0.1f) else statusColor.copy(
            alpha = 0.4f
        )
    val cardMainColor = if (isSystemInDarkTheme()) MaterialTheme.colors.background else statusColor

    val primaryText =
        if (alarm.hours > 0) "${alarm.hours}h:${alarm.minutes}m:${alarm.seconds}s" else if (alarm.minutes > 0) "${alarm.minutes}m:${alarm.seconds}s" else "${alarm.seconds}s"

    val infoText = alarm.title

    Card(
        backgroundColor = cardMainColor,
        elevation = 1.dp,
        border = BorderStroke(buttonBorderWidth, buttonBorderColor),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { openAlarmDetails() },
        shape = RoundedCornerShape(34.dp),
    ) {

        Row(
            modifier = Modifier.padding(R.dimen.padding_small.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {

            Column(
                modifier = Modifier
                    .fillMaxWidth(0.55f)
                    .padding(R.dimen.padding_small.dp)
                    .heightIn(50.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.End
            ) {

                Text(
                    maxLines = 1,
                    textAlign = TextAlign.Center,
                    color = textColor,
                    overflow = TextOverflow.Ellipsis,
                    text = primaryText,
                    fontSize = MaterialTheme.typography.h4.fontSize
                )

                Text(
                    maxLines = 1,
                    textAlign = TextAlign.Center,
                    color = textColor,
                    overflow = TextOverflow.Ellipsis,
                    text = infoText,
                    fontSize = MaterialTheme.typography.body1.fontSize,
                    modifier = Modifier.padding(bottom = R.dimen.padding_small.dp)
                )
            }

            Column(
                modifier = Modifier,
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.Center
            ) {

                Surface(
                    shape = RoundedCornerShape(50.dp),
                    elevation = 10.dp,
                ) {
                    OutlinedButton(
                        shape = RoundedCornerShape(50.dp),
                        modifier = Modifier.heightIn(min = 80.dp),
                        colors = ButtonDefaults.buttonColors(backgroundColor = buttonBackgroundColor),
                        contentPadding = PaddingValues(horizontal = R.dimen.padding_small.dp),
                        border = BorderStroke(Dp.Hairline, buttonBorderColor),
                        onClick = {
                            triggerAlarmStatus(alarm.id)
                        }) {


                        Box(
                            modifier = Modifier,
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                color = textColor,
                                fontWeight = FontWeight(400),
                                text = buttonText,
                                fontSize = MaterialTheme.typography.body1.fontSize,
                                textAlign = TextAlign.Center
                            )
                            Text(
                                color = Color.Transparent,
                                fontWeight = FontWeight(400),
                                text = "SCHEDULED",
                                fontSize = MaterialTheme.typography.body1.fontSize,
                                textAlign = TextAlign.Center
                            )
                        }
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
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colors.background),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            AlarmList()
        }
    }
}