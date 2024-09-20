
package com.example.timelytrack.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.timelytrack.model.LogEntry
import java.util.Locale

@Preview(showBackground = true)
@Composable
fun ListStickyHeaderPreview() {
    val date = "Saturday, March 20, 2024"
    val logs = listOf(
        LogEntry(startTimestamp = 1679289600000, endTimestamp = 1679290200000), // Example logs
        LogEntry(startTimestamp = 9679290800000, endTimestamp = 1679291400000)
    )
    ListStickyHeader(date = date, logs = logs)

}

@Composable
fun ListStickyHeader(date: String, logs: List<LogEntry>) {


    Row(
        modifier = Modifier
            .shadow(4.dp)
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.primaryContainer)
            .padding(8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = date,
            style = MaterialTheme.typography.titleMedium
        )
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.Filled.Flag,
                contentDescription = "Total Logs",
                modifier = Modifier.size(16.dp)
            )

            Spacer(modifier = Modifier.width(4.dp))

            Text(text = "${logs.size}")

            Spacer(modifier = Modifier.width(8.dp))

            Icon(
                imageVector = Icons.Filled.Schedule,
                contentDescription = "Time Icon",
                modifier = Modifier.size(16.dp)
            )
            // Calculate and display the sum of durations
            val totalDuration = logs.sumOf { log ->
                if (log.endTimestamp != null) {
                    log.endTimestamp!! - log.startTimestamp
                } else {
                    0}
            }

            val totalMinutes = totalDuration / 1000 / 60
            val totalHours = totalMinutes / 60
            val totalDays = totalHours / 24

            val durationText = when {
                totalDays > 0 -> String.format(Locale.getDefault(), "%dd %dh %dm", totalDays, totalHours % 24, totalMinutes % 60)
                totalHours > 0 -> String.format(Locale.getDefault(), "%dh %dm", totalHours, totalMinutes % 60)
                totalMinutes > 0 -> String.format(Locale.getDefault(), "%dm", totalMinutes)
                else -> "0m" // Show 0m if duration is 0
            }
            Text(text = durationText)
        } // end of row
    }
}