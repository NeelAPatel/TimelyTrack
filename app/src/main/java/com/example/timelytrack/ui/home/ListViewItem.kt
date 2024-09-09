package com.example.timelytrack.ui.home

import android.annotation.SuppressLint
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.example.timelytrack.model.LogEntry
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun ListViewItem(logEntry: LogEntry) {
    val dateFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())

    // Calculate duration
    val duration = if (logEntry.endTimestamp != null) {
        val diffInMillis = logEntry.endTimestamp!! - logEntry.startTimestamp
        val minutes = diffInMillis / 1000 / 60
        "$minutes min"
    } else {
        "--"
    }

    // swiping feature
    var offsetX by remember { mutableStateOf(0.dp) }
    val revealProgress by animateFloatAsState(
        targetValue = (offsetX.value / 100.dp.value).coerceIn(0f, 1f)
    )
    val animatedOffsetX by animateDpAsState(
        targetValue = if (revealProgress == 0f) 0.dp else -100.dp
    )


    Column(modifier = Modifier.fillMaxWidth()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .offset { IntOffset(animatedOffsetX.roundToPx(), 0) }
                .pointerInput(Unit) {
                    detectHorizontalDragGestures(
                        onDragEnd = {
                            if (revealProgress < 0.5f) {
                                offsetX = 0.dp
                            } else {
                                offsetX = -100.dp
                            }
                        },
                        onHorizontalDrag = { change, dragAmount ->
                            offsetX += Dp(dragAmount)
                        }
                    )
                }
        ) {
            // Item Content
            ListItem(
                headlineContent = {
                    Text(text = dateFormat.format(Date(logEntry.startTimestamp)))
                },
                supportingContent = {
                    logEntry.endTimestamp?.let {
                        Text("End Time: ${dateFormat.format(Date(it))}")
                    } ?: Text("End Time: --")
                },
                trailingContent = {
                    Text(text = duration)
                },
                leadingContent = {
                    Icon(
                        Icons.Filled.Schedule,
                        contentDescription = "Log Time Icon"
                    )
                }
            )

            // Reveal Content (Delete Icon)
            if (revealProgress > 0f) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Color.Green.copy(
                                alpha = 0.5f + 0.5f * revealProgress
                            )
                        ),
                    contentAlignment = Alignment.CenterEnd
                ) {
                    Icon(
                        imageVector = Icons.Filled.Delete,
                        contentDescription = "Delete",
                        modifier = Modifier
                            .size(48.dp)
                            .padding(16.dp)
                    )
                }
            }
        }
        HorizontalDivider()
    }
}