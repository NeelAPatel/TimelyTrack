package com.example.timelytrack.ui.home

//import android.app.TimePickerDialog
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.KeyboardDoubleArrowRight
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.timelytrack.model.LogEntry
import kotlinx.coroutines.CoroutineScope
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import androidx.compose.material3.Icon

import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = false)
@Composable
fun ModalBottomSheetComponentPreview() {
    val scope = rememberCoroutineScope()

    // Mock LogEntry for the preview
    val mockLogEntry = LogEntry(
        id = 0,
        categoryId = "1",
        startTimestamp = 1679289600000,
        endTimestamp = 1679290200000
    )
    ModalBottomSheetComponent(
        onCloseBottomSheet = { /* Do nothing for preview */ },
        bottomSheetState = rememberStandardBottomSheetState(
            initialValue = SheetValue.Expanded
        ),
        selectedLog = mockLogEntry,
        scope = scope,
        onLogUpdated = {}
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true)
@Composable
fun MyDialogContentPreview(){

    // Mock LogEntry for the preview
    val mockLogEntry = LogEntry(
        id = 0,
        categoryId = "1",
        startTimestamp = 1679289600000,
        endTimestamp = 1679290200000
    )


    MyDialogContent(mockLogEntry, onCloseBottomSheet = { /* Do nothing for preview */ }, bottomSheetState = rememberModalBottomSheetState(), scope = rememberCoroutineScope(), onLogUpdated = {})
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ModalBottomSheetComponent(
    onCloseBottomSheet: () -> Unit,
    bottomSheetState: SheetState,
    selectedLog: LogEntry?,
    scope: CoroutineScope,
    onLogUpdated: (LogEntry) -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = { onCloseBottomSheet() },
        sheetState = bottomSheetState,
    ) {

        MyDialogContent(selectedLog, onCloseBottomSheet, bottomSheetState, scope, onLogUpdated)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyDialogContent(
    selectedLog: LogEntry?,
    onCloseBottomSheet: () -> Unit,
    bottomSheetState: SheetState,
    scope: CoroutineScope,
    onLogUpdated: (LogEntry) -> Unit
) {
    if (selectedLog == null) return

    val context = LocalContext.current
    var startTimestamp by remember { mutableStateOf(selectedLog.startTimestamp) }
    var endTimestamp by remember { mutableStateOf(selectedLog.endTimestamp) }

    var isTimePickerVisible by remember { mutableStateOf(false) }
    var timePickerState = rememberTimePickerState()
    var isStartTimePicker by remember { mutableStateOf(true) }

    // TimePicker Dialog
    if (isTimePickerVisible) {
        AlertDialog(
            onDismissRequest = { isTimePickerVisible = false },
            title = { Text(text = "Pick a Time") },
            text = {
                TimePicker(
                    state = timePickerState,
                    modifier = Modifier.padding(8.dp)
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        val pickedHour = timePickerState.hour
                        val pickedMinute = timePickerState.minute
                        val calendar = Calendar.getInstance().apply {
                            set(Calendar.HOUR_OF_DAY, pickedHour)
                            set(Calendar.MINUTE, pickedMinute)
                        }
                        if (isStartTimePicker) {
                            startTimestamp = calendar.timeInMillis
                        } else {
                            endTimestamp = calendar.timeInMillis
                        }
                        isTimePickerVisible = false
                    }
                ) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(onClick = { isTimePickerVisible = false }) {
                    Text("Cancel")
                }
            }
        )
    }


    Text(selectedLog.toString())
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.border(1.dp, MaterialTheme.colorScheme.primary).fillMaxWidth()
    ) {
        // Scheduling Row
        Icon(
            Icons.Filled.Schedule,
            contentDescription = "Clock",
            modifier = Modifier.padding(16.dp)
        )
        Box(
            modifier = Modifier
                .clickable {
                    isStartTimePicker = true
                    isTimePickerVisible = true
                }

                .border(1.dp, MaterialTheme.colorScheme.primary, shape = RoundedCornerShape(8.dp))
                .padding(16.dp)
        ) { Text("" + formatTimestampToTime(startTimestamp)) }

        Icon(
            Icons.Filled.KeyboardDoubleArrowRight,
            contentDescription = "Right arrow",
            modifier = Modifier.padding(16.dp)
        )
        Box(
            modifier = Modifier
                .clickable {
                    isStartTimePicker = false
                    isTimePickerVisible = true
                }
                .border(1.dp, MaterialTheme.colorScheme.primary, shape = RoundedCornerShape(8.dp))
                .padding(16.dp)
        ) { // Show placeholder if endTimestamp is null
            Text(
                text = endTimestamp?.let { formatTimestampToTime(it) } ?: "Set End Time",
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = if (endTimestamp == null) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.onSurface
                )
            )
        }
    }
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.border(1.dp, MaterialTheme.colorScheme.primary).fillMaxWidth()
    ) {    // Save Button
        Button(
            onClick = {
                // Perform any action with the updated timestamps
                val updatedLog = selectedLog.copy(
                    startTimestamp = startTimestamp,
                    endTimestamp = endTimestamp
                )
                onLogUpdated(updatedLog)
                onCloseBottomSheet()
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "Save")
        }
    }

}



//fun formatTimestampToTime(timestamp: Long): String {
//    val formatter = SimpleDateFormat("hh:mm a", Locale.getDefault())
//    return formatter.format(Date(timestamp))
//}

// Function to format timestamp to hh:mm AM/PM
fun formatTimestampToTime(timestamp: Long?): String {
    return if (timestamp != null) {
        val formatter = SimpleDateFormat("hh:mm a", Locale.getDefault())
        formatter.format(Date(timestamp))
    } else {
        ""
    }
}

// Function to show a Time Picker dialog
@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun showTimePicker(
    context: android.content.Context,
    startTime: Long?,
    onTimeSelected: (Long) -> Unit
) {
//    val calendar = Calendar.getInstance().apply {
//        timeInMillis = initialTime ?: System.currentTimeMillis()
//    }
//    val hour = calendar.get(Calendar.HOUR_OF_DAY)
//    val minute = calendar.get(Calendar.MINUTE)




//
//    TimePicker(
//        state =
//        context,
//        { _, selectedHour, selectedMinute ->
//            val updatedCalendar = Calendar.getInstance().apply {
//                set(Calendar.HOUR_OF_DAY, selectedHour)
//                set(Calendar.MINUTE, selectedMinute)
//            }
//            onTimeSelected(updatedCalendar.timeInMillis)
//        },
//        hour,
//        minute,
//        false // Use 12-hour format
//    ).show()
}