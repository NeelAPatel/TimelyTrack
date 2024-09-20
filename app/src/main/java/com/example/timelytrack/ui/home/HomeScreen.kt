package com.example.timelytrack.ui.home

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FabPosition
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.unit.dp
import com.example.timelytrack.model.LogEntry
import com.example.timelytrack.viewmodel.LogViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@ExperimentalFoundationApi
@Composable
fun HomeScreen(viewModel: LogViewModel) {
    // --- Added groupedLogs variable to group logs by date ---
    val groupedLogs = groupLogsByDate(viewModel.logEntries)


//    val context = LocalContext.current
    Scaffold(
        floatingActionButtonPosition = FabPosition.End,
        floatingActionButton = {
            Column(
                    horizontalAlignment = Alignment.End,
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.padding(end = 16.dp, bottom = 16.dp)
                ) {

//                val context = LocalContext.current
//                val scope = rememberCoroutineScope()


                SmallFloatingActionButton(
                    onClick = { viewModel.completeLastLogEntry() },
                    containerColor = MaterialTheme.colorScheme.onPrimary,
                    contentColor = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(48.dp)
                ) {
                    Icon(Icons.Filled.Check, contentDescription = "Complete current log")
                }

                Spacer(modifier = Modifier.height(16.dp))

                ExtendedFloatingActionButton(
                    onClick = { viewModel.addLogEntry() },
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                    icon = {  Icon(Icons.Filled.Flag, contentDescription = "New Log")},
                    text = { Text("New Log") },



                )
            }
        }
    ) {

        padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // --- Updated to loop through each date group ---
            groupedLogs.forEach { (date, logs) ->
                // --- Added StickyHeader for each date group ---
                stickyHeader {

                    Row(modifier = Modifier.shadow(4.dp).fillMaxWidth().background(MaterialTheme.colorScheme.primaryContainer).padding(8.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
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
                                    0
                                }
                            }

                            val totalMinutes = totalDuration/1000/ 60
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

                // --- Display logs for the current date group ---
                items(logs) { log ->
                    ListViewItem(logEntry = log)
//                    Divider()
                }
            }
        }
    }
}

// Grouping logs by date
// --- Added function to group logs by date ---
fun groupLogsByDate(logEntries: List<LogEntry>): Map<String, List<LogEntry>> {
    val dateFormat = SimpleDateFormat("EEEE MMM dd, yyyy", Locale.getDefault())
    return logEntries.groupBy { dateFormat.format(Date(it.startTimestamp)) }
}