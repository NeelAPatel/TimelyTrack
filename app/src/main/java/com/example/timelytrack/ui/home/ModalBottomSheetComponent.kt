package com.example.timelytrack.ui.home

//import android.app.TimePickerDialog
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
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
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.ui.Alignment
import com.example.timelytrack.data.TimeWrapper
import com.example.timelytrack.data.toTimeWrapper
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
        isBottomSheetOpen = { /* Do nothing for preview */ },
        bottomSheetState = rememberStandardBottomSheetState(
            initialValue = SheetValue.Expanded
        ),
        selectedLogToEdit = mockLogEntry,
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


    MyDialogContent(mockLogEntry,
        isBottomSheetOpen = { /* Do nothing for preview */ },
        bottomSheetState = rememberModalBottomSheetState(),
        scope = rememberCoroutineScope(),
        onLogUpdated = {})
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ModalBottomSheetComponent(
    isBottomSheetOpen: () -> Unit,
    bottomSheetState: SheetState,
    selectedLogToEdit: LogEntry?,
    scope: CoroutineScope,
    onLogUpdated: (LogEntry) -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = { isBottomSheetOpen() },
        sheetState = bottomSheetState,
    ) {

        MyDialogContent(
            isBottomSheetOpen = isBottomSheetOpen, bottomSheetState = bottomSheetState ,
            selectedLogToEdit = selectedLogToEdit, scope = scope,
            onLogUpdated = onLogUpdated)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyDialogContent(
    selectedLogToEdit: LogEntry?,
    isBottomSheetOpen: () -> Unit,
    bottomSheetState: SheetState,
    scope: CoroutineScope,
    onLogUpdated: (LogEntry) -> Unit
) {
    if (selectedLogToEdit == null) return

    val context = LocalContext.current
    var startTimestamp by remember { mutableLongStateOf(selectedLogToEdit.startTimestamp) }
    var endTimestamp by remember { mutableStateOf(selectedLogToEdit.endTimestamp) }
    var isTimePickerVisible by remember { mutableStateOf(false) }
    var isStartTimePicker by remember { mutableStateOf(true) }
    var pickerTime by remember { mutableLongStateOf(System.currentTimeMillis()) }
    val timePickerState = rememberTimePickerState(
        initialHour = pickerTime.toTimeWrapper().get24Hour,
        initialMinute = pickerTime.toTimeWrapper().getMinute,
        is24Hour = false
    )



    // === UI ====
    // Debug String
    Text(selectedLogToEdit.toString())

    // Row 1 : Scheduling Row
    // Icon | Start Time | -> | End Time
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.border(1.dp, MaterialTheme.colorScheme.primary).fillMaxWidth()
    ) {

        Icon(Icons.Filled.Schedule, contentDescription = "Clock", modifier = Modifier.padding(16.dp))
        // Start Time
        Box(
            modifier = Modifier
                .clickable {
                    isStartTimePicker = true
                    isTimePickerVisible = true
                    pickerTime = startTimestamp
                }

                .border(1.dp, MaterialTheme.colorScheme.primary, shape = RoundedCornerShape(8.dp))
                .padding(16.dp)
        ) { Text("" + formatTimestampToTime(startTimestamp)) }

        // Arrow
        Icon(Icons.Filled.KeyboardDoubleArrowRight, contentDescription = "Right arrow", modifier = Modifier.padding(16.dp))

        // End Time
        Box(
            modifier = Modifier
                .clickable {
                    isStartTimePicker = false
                    isTimePickerVisible = true
                    pickerTime = endTimestamp ?: System.currentTimeMillis()
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
        // Save Button
        Button(
            onClick = {
                if (endTimestamp!! < startTimestamp) {
                    // Show an error or handle invalid end time
                    println("Error: End time must be after or equal to start time.")
                } else {
                    val updatedLog = selectedLogToEdit.copy(
                        startTimestamp = startTimestamp,
                        endTimestamp = endTimestamp
                    )
                    onLogUpdated(updatedLog)
                    isBottomSheetOpen()
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Save")
        }
    }

    // Show the TimePickerDialog
    TimePickerDialog(
        isVisible = isTimePickerVisible,
        onDismiss = { isTimePickerVisible = false },
        initialTimestamp = pickerTime,
        onTimeSelected = { selectedTime ->
            if (isStartTimePicker) {
                startTimestamp = selectedTime
            } else {
                if (selectedTime >= startTimestamp) {
                    endTimestamp = selectedTime
                } else {
                    println("Error: End time must be after or equal to start time.")
                }
            }
        }
    )

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimePickerDialog(
    isVisible: Boolean,
    onDismiss: () -> Unit,
    initialTimestamp: Long,
    onTimeSelected: (Long) -> Unit
) {
    if (isVisible) {
        val timePickerState = rememberTimePickerState(
            initialHour = initialTimestamp.toTimeWrapper().get24Hour,
            initialMinute = initialTimestamp.toTimeWrapper().getMinute,
            is24Hour = false
        )

        AlertDialog(
            onDismissRequest = onDismiss,
            title = { Text("Pick a Time") },
            text = {
                TimePicker(
                    state = timePickerState,
                    modifier = Modifier.padding(8.dp)
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        val calendar = Calendar.getInstance().apply {
                            set(Calendar.HOUR_OF_DAY, timePickerState.hour)
                            set(Calendar.MINUTE, timePickerState.minute)
                        }
                        onTimeSelected(calendar.timeInMillis)
                        onDismiss()
                    }
                ) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { onDismiss() }
                ) {
                    Text("Cancel")
                }
            }
        )
    }
}


// Function to format timestamp to hh:mm AM/PM
fun formatTimestampToTime(timestamp: Long?): String {
    return if (timestamp != null) {
        val formatter = SimpleDateFormat("hh:mm a", Locale.getDefault())
        formatter.format(Date(timestamp))
    } else {
        ""
    }
}
