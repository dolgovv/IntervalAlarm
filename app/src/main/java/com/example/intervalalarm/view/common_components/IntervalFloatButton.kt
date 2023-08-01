package com.example.intervalalarm.view.common_components

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
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
        modifier = Modifier.semantics { contentDescription = "Main Button" }
            .heightIn(50.dp, 270.dp)
            .widthIn(50.dp, 170.dp)
            .padding(22.dp),
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
                        .padding(6.dp),
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
                            .padding(horizontal = 6.dp)
                            .padding(vertical = 22.dp),
                        color = Color.White.copy(alpha = 0.8f),
                        fontWeight = FontWeight(1000),
                        text = "SCHEDULE",
                        textAlign = TextAlign.Center,
                        fontSize = 16.sp
                    )
                    Text(
                        modifier =
                        Modifier
                            .padding(horizontal = 6.dp)
                            .padding(vertical = 22.dp),
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
                        .padding(horizontal = 6.dp)
                        .padding(vertical = 22.dp),
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