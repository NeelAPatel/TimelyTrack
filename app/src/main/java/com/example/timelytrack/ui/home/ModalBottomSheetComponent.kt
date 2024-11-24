package com.example.timelytrack.ui.home

//import android.app.TimePickerDialog
import android.util.Log
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
import androidx.compose.foundation.layout.widthIn
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


    //Variable for start/endtimestamp that are being used
    var startTimestamp by remember { mutableLongStateOf(selectedLogToEdit.startTimestamp) }
    var endTimestamp by remember { mutableStateOf(selectedLogToEdit.endTimestamp) }


    //Timepicker variables
    var isTimePickerVisible by remember { mutableStateOf(false) } // True = show, false = hide
    var initialPickerTime by remember {mutableLongStateOf(System.currentTimeMillis())} // Long format
    val initialTimePickerState = rememberTimePickerState(
        initialHour = initialPickerTime.toTimeWrapper().get24Hour,
        initialMinute = initialPickerTime.toTimeWrapper().getMinute,
        is24Hour = false
    )

    var isStartTimestamp by remember { mutableStateOf(false) } // True = show, false = hide
    var isError by remember { mutableStateOf(false) }

//    val context = LocalContext.current



    //.border(1.dp, MaterialTheme.colorScheme.primary)
    // === UI ====
    Column( modifier = Modifier.padding(8.dp)) {

        // Debug String
        Row() {Text(selectedLogToEdit.toString())}

        // Row 1 : Scheduling Row
        // Icon | Start Time | -> | End Time
        Row (
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth(),
        ) {
            Icon(Icons.Filled.Schedule, contentDescription = "Clock", modifier = Modifier.padding(8.dp))
            OutlinedTextField(
                label = { Text("Start Time") },
                value = startTimestamp.toTimeWrapper().getFormattedTime("hh: mm a"),
                trailingIcon = {
                    Icon(
                        imageVector = Icons.Filled.Schedule,
                        contentDescription = "Pick Time",
                        modifier = Modifier
                            .clickable {
                                isStartTimestamp = true
                                initialPickerTime = startTimestamp
                                isTimePickerVisible = true
                            }
                    )
                },
                readOnly = true,
                isError = isError,
                onValueChange = {},
                modifier = Modifier.padding(8.dp).fillMaxWidth(0.5f)
            )

//            // Arrow
//            Icon(Icons.Filled.KeyboardDoubleArrowRight, contentDescription = "Right arrow", modifier = Modifier.padding(16.dp))

            OutlinedTextField(
                label = { Text("End Time") },
                value =  endTimestamp.toTimeWrapper().getFormattedTime("hh: mm a"),
                trailingIcon = {
                    Icon(
                        imageVector = Icons.Filled.Schedule,
                        contentDescription = "Pick Time",
                        modifier = Modifier
                            .clickable {
                                isStartTimestamp = false
                                isTimePickerVisible = true
                                initialPickerTime = endTimestamp ?: System.currentTimeMillis()
                            }
                    )
                },
                readOnly = true,
                isError = isError,
                onValueChange = {},
                modifier = Modifier.padding(8.dp)
            )
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
                enabled = !isError,
                modifier = Modifier.fillMaxWidth()
            ) {
                if (isError) {
                    Text("Save disabled. Error detected")
                }
                else
                    Text("Save")

            }
        }
    }



    // Show the TimePickerDialog
    TimePickerDialog(
        isVisible = isTimePickerVisible,
        onDismiss = { isTimePickerVisible = false },
        initialTimestamp = initialPickerTime,
        onTimeSelected = { selectedTime ->


            Log.e("Timest", "=============")
            Log.e("Timest", isStartTimestamp.toString())
            Log.e("Timest", "SelTime > EndTime = " + (selectedTime > endTimestamp).toString())

            Log.e("INITIAL Timestamp", startTimestamp.toTimeWrapper().getFormattedTime("hh: mm a") + " " + endTimestamp.toTimeWrapper().getFormattedTime("hh: mm a") )
            Log.e("selectedTimestamp", selectedTime.toTimeWrapper().getFormattedTime("hh: mm a") + " " + selectedTime)
            Log.e("Timest", " - - - -")

            val initialTimeDifference = endTimestamp.minus(startTimestamp) ?: 0L

            if (isStartTimestamp) {
                isError = false
                startTimestamp = selectedTime

                // Adjust endTimestamp to maintain the initial difference
                if (selectedTime > (endTimestamp ?: selectedTime)) {
                    endTimestamp = selectedTime + initialTimeDifference
                }
                Log.e("selectedTimeStamp", endTimestamp.toString())
                Log.e("selectedTimeStamp",selectedTime.toTimeWrapper().getFormattedTime("hh: mm a") +  " --- -- --")
            }

            if (!isStartTimestamp){
                endTimestamp = selectedTime
                if (selectedTime < startTimestamp)
                    isError = true
                else
                    isError = false
                Log.e("selectedTimeStamp", "--- -- -- " + selectedTime.toTimeWrapper().getFormattedTime("hh: mm a"))
            }


            Log.e("Timest", startTimestamp.toTimeWrapper().getFormattedTime("hh: mm a") + " " + endTimestamp.toTimeWrapper().getFormattedTime("hh: mm a") )
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

        var isInvalidTime by remember { mutableStateOf(false) }


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

                // Validation message
                if (isInvalidTime) {
                    Text(
                        text = "End time must be after start time",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }

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
