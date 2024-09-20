package com.example.timelytrack.ui.home

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Column
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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



    Column {
        ListItem(
            headlineContent = {
//                Row(
//                    modifier = Modifier.fillMaxWidth(),
//                    horizontalArrangement = Arrangement.SpaceBetween
//                ) {
//                    Text(text = dateFormat.format(Date(logEntry.startTimestamp)), fontWeight = FontWeight.Bold)
//                }

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
        HorizontalDivider()
    }
}






//fun ListViewItem(logEntry: LogEntry) {
//    val dateFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())
//
//    // Calculate duration
//    val duration = if (logEntry.endTimestamp != null) {
//        val diffInMillis = logEntry.endTimestamp!! - logEntry.startTimestamp
//        val minutes = diffInMillis / 1000 / 60
//        "$minutes min"
//    } else {
//        "--"
//    }
//
//    // swiping feature
//    var offsetX by remember { mutableStateOf(0.dp) }
//    val revealProgress by animateFloatAsState(
//        targetValue = (offsetX.value / 100.dp.value).coerceIn(0f, 1f)
//    )
//    val animatedOffsetX by animateDpAsState(
//        targetValue = if (revealProgress == 0f) 0.dp else -100.dp
//    )
//
//
//    Column(modifier = Modifier.fillMaxWidth()) {
//        Box(
//            modifier = Modifier
//                .fillMaxWidth()
//                .offset { IntOffset(animatedOffsetX.roundToPx(), 0) }
//                .pointerInput(Unit) {
//                    detectHorizontalDragGestures(
//                        onDragEnd = {
//                            if (revealProgress < 0.5f) {
//                                offsetX = 0.dp
//                            } else {
//                                offsetX = -100.dp
//                            }
//                        },
//                        onHorizontalDrag = { change, dragAmount ->
//                            offsetX += Dp(dragAmount)
//                        }
//                    )
//                }
//        ) {
//            // Item Content
//            ListItem(
//                headlineContent = {
//                    Text(text = dateFormat.format(Date(logEntry.startTimestamp)))
//                },
//                supportingContent = {
//                    logEntry.endTimestamp?.let {
//                        Text("End Time: ${dateFormat.format(Date(it))}")
//                    } ?: Text("End Time: --")
//                },
//                trailingContent = {
//                    Text(text = duration)
//                },
//                leadingContent = {
//                    Icon(
//                        Icons.Filled.Schedule,
//                        contentDescription = "Log Time Icon"
//                    )
//                }
//            )
//
//            // Reveal Content (Delete Icon)
//            if (revealProgress > 0f) {
//                Box(
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .background(
//                            Color.Green.copy(
//                                alpha = 0.5f + 0.5f * revealProgress
//                            )
//                        ),
//                    contentAlignment = Alignment.CenterEnd
//                ) {
//                    Icon(
//                        imageVector = Icons.Filled.Delete,
//                        contentDescription = "Delete",
//                        modifier = Modifier
//                            .size(48.dp)
//                            .padding(16.dp)
//                    )
//                }
//            }
//        }
//        HorizontalDivider()
//    }
//}