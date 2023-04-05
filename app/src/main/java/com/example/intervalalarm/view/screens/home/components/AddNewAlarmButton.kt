package com.example.intervalalarm.view.screens.home.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun AddNewAlarmButton(
    addNew: () -> Unit,
    hasIcon: Boolean,
    isScheduled: Boolean
) {
    Surface(
        modifier = if (hasIcon) Modifier.size(70.dp) else Modifier.height(80.dp).width(135.dp),
        shape = RoundedCornerShape(50.dp),
        elevation = 10.dp
    ) {
        FloatingActionButton(
            backgroundColor = MaterialTheme.colors.primary,
            onClick = { addNew() }) {
            if (hasIcon) {
                Icon(
                    Icons.Default.Add,
                    contentDescription = "add new alarm button",
                    Modifier.size(60.dp),
                    tint = Color.White.copy(alpha = 0.8f)
                )
            } else if (isScheduled) {
                Text(
                    modifier =
                    Modifier.padding(horizontal = 8.dp),
                    color = Color.White.copy(alpha = 0.8f),
                    fontWeight = FontWeight(1000),
                    text = "Schedule"
                )
            } else {
                Text(
                    modifier =
                    Modifier.padding(horizontal = 8.dp).padding(horizontal = 10.dp),
                    color = Color.White.copy(alpha = 0.8f),
                    fontWeight = FontWeight(1000),
                    text = "START NOW"
                )
            }
        }
    }
}