package com.example.intervalalarm.view.screens.home.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.intervalalarm.R

@Composable
fun IntervalFloatButton(
    function: () -> Unit,
    isScheduled: Boolean,
    hasIcon: Boolean,
    icon: ImageVector = Icons.Default.Add,
    color: Color = MaterialTheme.colors.primary,
    iconColor: Color = Color.White.copy(alpha = 0.8f)
) {
    Surface(
        modifier = Modifier
            .heightIn(50.dp, 100.dp)
            .widthIn(50.dp, 170.dp),
        shape = RoundedCornerShape(50.dp),
        elevation = 10.dp
    ) {

        FloatingActionButton(
            backgroundColor = color,
            onClick = { function() }) {
            if (hasIcon) {
                Icon(
                    imageVector = icon,
                    contentDescription = "navigation button",
                    Modifier
                        .size(60.dp)
                        .padding(R.dimen.padding_small.dp),
                    tint = iconColor
                )
            } else if (isScheduled) {
                Box(
                    modifier = Modifier,
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        modifier =
                        Modifier
                            .padding(horizontal = R.dimen.padding_small.dp)
                            .padding(vertical = R.dimen.padding_large.dp),
                        color = Color.White.copy(alpha = 0.8f),
                        fontWeight = FontWeight(1000),
                        text = "SCHEDULE",
                        textAlign = TextAlign.Center,
                        fontSize = 16.sp
                    )
                    Text(
                        modifier =
                        Modifier
                            .padding(horizontal = R.dimen.padding_small.dp)
                            .padding(vertical = R.dimen.padding_large.dp),
                        color = Color.Transparent,
                        fontWeight = FontWeight(1000),
                        text = "START NOW",
                        textAlign = TextAlign.Center,
                        fontSize = 16.sp
                    )
                }
            } else {
                Text(
                    modifier =
                    Modifier
                        .padding(horizontal = R.dimen.padding_small.dp)
                        .padding(vertical = R.dimen.padding_large.dp),
                    color = Color.White.copy(alpha = 0.8f),
                    fontWeight = FontWeight(1000),
                    text = "START NOW",
                    textAlign = TextAlign.Center,
                    fontSize = 16.sp
                )
            }
        }
    }
}