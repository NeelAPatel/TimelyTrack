package com.example.timelytrack.ui.home

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.timelytrack.model.LogEntry
import java.util.*

@Composable
fun LogEntryItem(log: LogEntry) {
    Column(modifier = Modifier.padding(16.dp)) {
        Text("Start: ${Date(log.startTimestamp)}")
        log.endTimestamp?.let {
            Text("End: ${Date(it)}")
        }
    }
}