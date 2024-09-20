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
import androidx.compose.ui.tooling.preview.Preview
import com.example.timelytrack.model.LogEntry
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Preview(showBackground = true)
@Composable
fun ListViewItemPreview() {
    val logEntry = LogEntry(startTimestamp = 1679289600000, endTimestamp = 1679290200000)
    ListViewItem(logEntry = logEntry)
}

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
