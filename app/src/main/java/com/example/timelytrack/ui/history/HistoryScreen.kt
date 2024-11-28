@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.timelytrack.ui.history

//import android.graphics.Color

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.RectangleShape

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.selection.toggleable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.semantics.Role
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.timelytrack.viewmodel.LogViewModel
import kotlinx.coroutines.launch
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.State
import androidx.compose.ui.graphics.graphicsLayer
import com.example.timelytrack.model.LogEntry
import com.example.timelytrack.ui.home.rememberMarker

import com.patrykandpatrick.vico.compose.cartesian.CartesianChartHost
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberColumnCartesianLayer
import com.patrykandpatrick.vico.compose.cartesian.rememberCartesianChart
import com.patrykandpatrick.vico.core.cartesian.data.CartesianChartModelProducer
import com.patrykandpatrick.vico.core.cartesian.data.columnSeries
import com.patrykandpatrick.vico.core.cartesian.axis.HorizontalAxis
import com.patrykandpatrick.vico.core.cartesian.axis.VerticalAxis
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberBottom
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberStart
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberLineCartesianLayer
import com.patrykandpatrick.vico.compose.cartesian.marker.rememberDefaultCartesianMarker
import com.patrykandpatrick.vico.compose.cartesian.rememberVicoScrollState
import com.patrykandpatrick.vico.compose.cartesian.rememberVicoZoomState
import com.patrykandpatrick.vico.core.cartesian.CartesianMeasuringContext
import com.patrykandpatrick.vico.core.cartesian.axis.Axis
import com.patrykandpatrick.vico.core.cartesian.data.CartesianValueFormatter
import com.patrykandpatrick.vico.core.cartesian.data.lineSeries
import com.patrykandpatrick.vico.core.cartesian.marker.DefaultCartesianMarker
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import kotlin.math.roundToInt

@Preview(showBackground = true)
@Composable
fun HistoryScreenPreview() {
    HistoryScreen()
}



//                item { DemoSwipeToDismissBox() }
//                item { DemoBottomSheet() }
//                item { DemoTimePickerWithError()}
//                item{DemoYCharts()}
@Composable
fun HistoryScreen() {

    //=== Variables ===
    var singleChoiceSelectedIndex by remember { mutableStateOf(1) }
    val singleChoiceSelectorOptions = listOf("Hourly", "Daily", "Weekly", "Monthly")

    // === Launch Effects ===

    // === UI ====
    Scaffold() { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .padding(innerPadding).padding(8.dp).fillMaxWidth()
        ) {

            // Single Choice Selector
            item {
                SingleChoiceSegmentedButtonRow () {
                    singleChoiceSelectorOptions.forEachIndexed { index, label ->
                        SegmentedButton(
                            shape = SegmentedButtonDefaults.itemShape(
                                index = index,
                                count = singleChoiceSelectorOptions.size
                            ),
                            onClick = { singleChoiceSelectedIndex = index },
                            selected = index == singleChoiceSelectedIndex
                        ) {
                            Text(label)
                        }
                    }
                }
            }
            item { DemoVicoChart(singleChoiceSelectedIndex) }




        }

    }
}

@Composable
fun DemoVicoChart(singleChoiceSelectedIndex: Int) {
    val viewModel: LogViewModel = viewModel(factory = LogViewModel.Factory)
    val logEntries = viewModel.allLogEntries.collectAsState()


    var (logsDateGroups, logsSizeSeries) = logEntryAggregator(singleChoiceSelectedIndex, logEntries)



    // Chart configuration
    val modelProducer = remember { CartesianChartModelProducer() }
    LaunchedEffect(logsSizeSeries) {
        modelProducer.runTransaction {
            columnSeries {
//            lineSeries {

            if (logsSizeSeries != null) {
                    series(logsSizeSeries)
                }
            }

        }
    }

    LaunchedEffect(logsSizeSeries) {
        modelProducer.runTransaction {
            columnSeries {
//            lineSeries {
                if (logsSizeSeries != null) {
                    series(logsSizeSeries)
                } // Pass the log sizes directly as a collection
            }
        }
    }

    if (logsDateGroups != null) {
        val scrollState = rememberVicoScrollState(/* ... */)
        val zoomState = rememberVicoZoomState(/* ... */)
        CartesianChartHost(
            rememberCartesianChart(
                rememberColumnCartesianLayer(),
//                rememberLineCartesianLayer(),

                startAxis = VerticalAxis.rememberStart( valueFormatter = integer()),
                bottomAxis = HorizontalAxis.rememberBottom(
                    labelRotationDegrees = -90f,
                    valueFormatter = CartesianValueFormatter { _, x, _ ->
                        logsDateGroups.getOrNull(x.roundToInt()) ?: "X" // Ensure safety with getOrNull
                    },
                    itemPlacer = HorizontalAxis.ItemPlacer.aligned(spacing = 1), // Adjust spacing
                ),
                marker = rememberMarker(DefaultCartesianMarker.LabelPosition.Top)
            ),
            modelProducer,
            scrollState = scrollState, zoomState = zoomState,
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp)
                .then(Modifier.graphicsLayer { rotationZ = 0f })
        )
    }
}

/** Formats values to display as integers. */
public fun integer(): CartesianValueFormatter = object : CartesianValueFormatter {
    override fun format(
        context: CartesianMeasuringContext,
        value: Double,
        verticalAxisPosition: Axis.Position.Vertical?,
    ): CharSequence {
        // Round the value to the nearest integer and return as a string
        return value.toInt().toString()
    }
}
fun logEntryAggregator (singleChoiceSelectedIndex: Int, logEntries: State<List<LogEntry>>): Pair<MutableList<String>?, MutableList<Int>?> {
    // Switch case based on index 0,1,2,3 to determine different ways of aggegating logEntries
    val logsDateGroups = mutableListOf<String>() // Extract dates for labels
    val logsSizeSeries = mutableListOf<Int>() // Extract values for the chart
    when (singleChoiceSelectedIndex) {
        0 -> {
            logsDateGroups.clear()
            logsSizeSeries.clear()
            val dateFormat = SimpleDateFormat("MMM dd, HH a", Locale.getDefault())
            val groupedLogs = logEntries.value.groupBy { logEntry -> dateFormat.format(Date(logEntry.startTimestamp)) }

            groupedLogs.forEach { (key, value) ->
                // Extract date from the key, e.g., "Monday Nov 21, 2022" -> "Nov 21"
//                val date = key.split(" ").get(0) + " " + key.split(" ").get(1).replace(",", "")
                logsDateGroups.add(key)

                // Add the size of the LogEntry list to logsSizeSeries
                logsSizeSeries.add(value.size)
            }

            return Pair(logsDateGroups, logsSizeSeries)
        }

        1 -> {

            logsDateGroups.clear()
            logsSizeSeries.clear()
            val dateFormat = SimpleDateFormat("MMM dd", Locale.getDefault())
            val groupedLogs = logEntries.value.groupBy { logEntry -> dateFormat.format(Date(logEntry.startTimestamp)) }

            groupedLogs.forEach { (key, value) ->
                // Extract date from the key, e.g., "Monday Nov 21, 2022" -> "Nov 21"
                val date = key.split(" ").get(0) + " " + key.split(" ").get(1).replace(",", "")
                logsDateGroups.add(date)

                // Add the size of the LogEntry list to logsSizeSeries
                logsSizeSeries.add(value.size)
            }

            return Pair(logsDateGroups, logsSizeSeries)
        }
        2 -> {
            logsDateGroups.clear()
            logsSizeSeries.clear()
            // Weekly breakdown: Format as <Month> <week start date> - <week end date>
            val weekDateFormat = SimpleDateFormat("MMM dd", Locale.getDefault())
            val calendar = Calendar.getInstance()
            val groupedLogs = logEntries.value.groupBy { logEntry ->
                // Set the date to the start of the week (Sunday as the start)
                calendar.time = Date(logEntry.startTimestamp)
                calendar.set(Calendar.DAY_OF_WEEK, calendar.firstDayOfWeek)
                weekDateFormat.format(calendar.time)
            }

            groupedLogs.forEach { (weekStart, value) ->
                // Find the week end date (7 days after the week start)
                calendar.time = SimpleDateFormat("MMM dd", Locale.getDefault()).parse(weekStart)
                calendar.add(Calendar.DAY_OF_YEAR, 6) // Add 6 days to get the end of the week
                val weekEnd = weekDateFormat.format(calendar.time)

                // Combine week start and end date into one label
                val label = "$weekStart - $weekEnd"
                logsDateGroups.add(label)
                logsSizeSeries.add(value.size)
            }

            return Pair(logsDateGroups, logsSizeSeries)
        }
        3 -> {
            logsDateGroups.clear()
            logsSizeSeries.clear()
            // Monthly breakdown: Format as <Month> (e.g., "Jan", "Feb")
            val monthDateFormat = SimpleDateFormat("MMM yyyy", Locale.getDefault()) // Include year to differentiate between same months in different years
            val groupedLogs = logEntries.value.groupBy { logEntry ->
                // Get only the month and year (e.g., "Jan 2024")
                monthDateFormat.format(Date(logEntry.startTimestamp))
            }

            groupedLogs.forEach { (monthYear, value) ->
                logsDateGroups.add(monthYear)  // Month (e.g., "Jan", "Feb") or "Jan 2024"
                logsSizeSeries.add(value.size)  // Log count for that month
            }

            return Pair(logsDateGroups, logsSizeSeries)
        }
    }
    return Pair(logsDateGroups, logsSizeSeries)
}


// ===============================================================================================================================================
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DemoTimePickerWithError() {
    var timeText by remember { mutableStateOf("12:00 PM") } // Default time
    var isError by remember { mutableStateOf(false) } // Error state
    var errorMessage by remember { mutableStateOf("") } // Error message
    var showTimePicker by remember { mutableStateOf(false) } // Toggles the time picker dialog
    val timePickerState = rememberTimePickerState(initialHour = 12, initialMinute = 0)

    // Watch for changes in the TimePicker state and validate immediately
    LaunchedEffect(timePickerState.hour, timePickerState.minute) {
        validateTime(timePickerState.hour, timePickerState.minute) { valid, error ->
            isError = !valid
            errorMessage = error
        }
    }

    Column(modifier = Modifier.padding(16.dp)) {
        // OutlinedTextField with error handling
        OutlinedTextField(
            value = timeText,
            onValueChange = {},
            label = { Text("Time") },
            isError = isError,
            trailingIcon = {
                Icon(
                    imageVector = Icons.Filled.Schedule,
                    contentDescription = "Pick Time",
                    modifier = Modifier.clickable {
                        showTimePicker = true
                    }
                )
            },
            modifier = Modifier.fillMaxWidth()
        )

        // Error Message
        if (isError) {
            Text(
                text = errorMessage,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(start = 16.dp)
            )
        }

        // Material3 TimePicker in a Dialog
        if (showTimePicker) {
            AlertDialog(
                onDismissRequest = { showTimePicker = false },
                confirmButton = {
                    TextButton(
                        onClick = {
                            val formattedTime = String.format(
                                "%02d:%02d %s",
                                if (timePickerState.hour % 12 == 0) 12 else timePickerState.hour % 12,
                                timePickerState.minute,
                                if (timePickerState.hour < 12) "AM" else "PM"
                            )
                            timeText = formattedTime
                            showTimePicker = false
                        }
                    ) {
                        Text("OK")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showTimePicker = false }) {
                        Text("Cancel")
                    }
                },
                text = {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        TimePicker(
                            state = timePickerState,
                            modifier = Modifier.padding(8.dp)
                        )

                        if (isError) {
                            // Highlight the error message in the TimePicker UI itself
                            Text(
                                text = errorMessage,
                                color = MaterialTheme.colorScheme.error,
                                style = MaterialTheme.typography.bodySmall,
                                modifier = Modifier.padding(top = 8.dp)
                            )
                        }
                    }
                }
            )
        }
    }
}

// Validation function for TimePicker's hour and minute fields
fun validateTime(hour: Int, minute: Int, onResult: (Boolean, String) -> Unit) {
    if (hour == 11 && minute == 0) {
        onResult(false, "11:00 AM/PM is not allowed")
    } else {
        onResult(true, "")
    }
}


// Validation function for manual text input
fun validateTimeText(time: String, onResult: (Boolean, String) -> Unit) {
    if (time.isBlank()) {
        onResult(false, "Time cannot be empty")
    } else if (!Regex("\\d{1,2}:\\d{2} [AP]M").matches(time)) {
        onResult(false, "Invalid time format (expected hh:mm AM/PM)")
    } else if (time.startsWith("11:00")) {
        onResult(false, "11:00 AM/PM is not allowed")
    } else {
        onResult(true, "")
    }
}


@Composable
fun DemoSwipeToDismissBox() {
    val dismissState = rememberSwipeToDismissBoxState()
    SwipeToDismissBox(
        state = dismissState,
        backgroundContent = {
            val color by
            animateColorAsState(
                when (dismissState.targetValue) {
                    SwipeToDismissBoxValue.Settled -> Color.LightGray
                    SwipeToDismissBoxValue.StartToEnd -> Color.Green
                    SwipeToDismissBoxValue.EndToStart -> Color.Red
                }
            )
            Box(Modifier.fillMaxSize().background(color))
        }
    ) {
        OutlinedCard(shape = RectangleShape) {
            ListItem(
                headlineContent = { Text("Cupcake") },
                supportingContent = { Text("Swipe me left or right!") }
            )
        }
    }
}


@Composable
fun DemoBottomSheet() {
    var openBottomSheet by rememberSaveable { mutableStateOf(false) }
    var skipPartiallyExpanded by rememberSaveable { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val bottomSheetState =
        rememberModalBottomSheetState(skipPartiallyExpanded = skipPartiallyExpanded)


// App content
    Column(
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Row(
            Modifier.toggleable(
                value = skipPartiallyExpanded,
                role = Role.Checkbox,
                onValueChange = { checked -> skipPartiallyExpanded = checked }
            )
        ) {
            Checkbox(checked = skipPartiallyExpanded, onCheckedChange = null)
            Spacer(Modifier.width(16.dp))
            Text("Skip partially expanded State")
        }
        Button(
            onClick = { openBottomSheet = !openBottomSheet },
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Text(text = "Show Bottom Sheet")
        }
    }

// Sheet content
    if (openBottomSheet) {

        ModalBottomSheet(
            onDismissRequest = { openBottomSheet = false },
            sheetState = bottomSheetState,
        ) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                Button(
                    // Note: If you provide logic outside of onDismissRequest to remove the sheet,
                    // you must additionally handle intended state cleanup, if any.
                    onClick = {
                        scope
                            .launch { bottomSheetState.hide() }
                            .invokeOnCompletion {
                                if (!bottomSheetState.isVisible) {
                                    openBottomSheet = false
                                }
                            }
                    }
                ) {
                    Text("Hide Bottom Sheet")
                }
            }
            var text by remember { mutableStateOf("") }
            OutlinedTextField(
                value = text,
                onValueChange = { text = it },
                modifier = Modifier.padding(horizontal = 16.dp),
                label = { Text("Text field") }
            )
            LazyColumn {
                items(25) {
                    ListItem(
                        headlineContent = { Text("Item $it") },
                        leadingContent = {
                            Icon(
                                Icons.Default.Favorite,
                                contentDescription = "Localized description"
                            )
                        },
                        colors =
                        ListItemDefaults.colors(
                            containerColor = MaterialTheme.colorScheme.surfaceContainerLow
                        ),
                    )
                }
            }
        }
    }
}

//
//@Preview
//@Composable
//fun ColorSchemePreview() {
//    MaterialTheme {
//        Column {
//            ColorDisplay(color = MaterialTheme.colorScheme.tertiary, name = "primary")
//            ColorDisplay(color = MaterialTheme.colorScheme.onPrimary, name = "onPrimary")
//            ColorDisplay(color = MaterialTheme.colorScheme.primaryContainer, name = "primaryContainer")
//            // ... display other colors similarly
//        }
//    }
//}
//
//@Composable
//fun ColorDisplay(color: Color, name: String) {
//    Box(
//        modifier = Modifier
//            .size(100.dp)
//            .background(color),
//        contentAlignment = Alignment.Center
//    ) {
//        Text(text = name, color = Color.White)
//    }
//}

//
//
///** The directions in which a [SwipeToDismiss] can be dismissed. */
//enum class DismissDirection {
//    /** Can be dismissed by swiping in the reading direction. */
//    StartToEnd,
//
//    /** Can be dismissed by swiping in the reverse of the reading direction. */
//    EndToStart
//}
//
///** Possible values of [DismissState]. */
//enum class DismissValue {
//    /** Indicates the component has not been dismissed yet. */
//    Default,
//
//    /** Indicates the component has been dismissed in the reading direction. */
//    DismissedToEnd,
//
//    /** Indicates the component has been dismissed in the reverse of the reading direction. */
//    DismissedToStart
//}
//
///**
// * State of the [SwipeToDismiss] composable.
// *
// * @param initialValue The initial value of the state.
// * @param confirmStateChange Optional callback invoked to confirm or veto a pending state change.
// */
//@ExperimentalMaterialApi
//class DismissState(
//    initialValue: DismissValue,
//    confirmStateChange: (DismissValue) -> Boolean = { true }
//) : SwipeableState<DismissValue>(initialValue, confirmStateChange = confirmStateChange) {
//    /**
//     * The direction (if any) in which the composable has been or is being dismissed.
//     *
//     * If the composable is settled at the default state, then this will be null. Use this to change
//     * the background of the [SwipeToDismiss] if you want different actions on each side.
//     */
//    val dismissDirection: DismissDirection?
//        get() = if (offset.value == 0f) null else if (offset.value > 0f) StartToEnd else EndToStart
//
//    /**
//     * Whether the component has been dismissed in the given [direction].
//     *
//     * @param direction The dismiss direction.
//     */
//    fun isDismissed(direction: DismissDirection): Boolean {
//        return currentValue == if (direction == StartToEnd) DismissedToEnd else DismissedToStart
//    }
//
//    /**
//     * Reset the component to the default position with animation and suspend until it if fully
//     * reset or animation has been cancelled. This method will throw [CancellationException] if the
//     * animation is interrupted
//     *
//     * @return the reason the reset animation ended
//     */
//    suspend fun reset() = animateTo(targetValue = Default)
//
//    /**
//     * Dismiss the component in the given [direction], with an animation and suspend. This method
//     * will throw [CancellationException] if the animation is interrupted
//     *
//     * @param direction The dismiss direction.
//     */
//    suspend fun dismiss(direction: DismissDirection) {
//        val targetValue = if (direction == StartToEnd) DismissedToEnd else DismissedToStart
//        animateTo(targetValue = targetValue)
//    }
//
//    companion object {
//        /** The default [Saver] implementation for [DismissState]. */
//        fun Saver(confirmStateChange: (DismissValue) -> Boolean) =
//            Saver<DismissState, DismissValue>(
//                save = { it.currentValue },
//                restore = { DismissState(it, confirmStateChange) }
//            )
//    }
//}
//
///**
// * Create and [remember] a [DismissState].
// *
// * @param initialValue The initial value of the state.
// * @param confirmStateChange Optional callback invoked to confirm or veto a pending state change.
// */
//@Composable
//@ExperimentalMaterialApi
//fun rememberDismissState(
//    initialValue: DismissValue = Default,
//    confirmStateChange: (DismissValue) -> Boolean = { true }
//): DismissState {
//    return rememberSaveable(saver = DismissState.Saver(confirmStateChange)) {
//        DismissState(initialValue, confirmStateChange)
//    }
//}
//
///**
// * A composable that can be dismissed by swiping left or right.
// *
// * @sample androidx.compose.material.samples.SwipeToDismissListItems
// * @param state The state of this component.
// * @param modifier Optional [Modifier] for this component.
// * @param directions The set of directions in which the component can be dismissed.
// * @param dismissThresholds The thresholds the item needs to be swiped in order to be dismissed.
// * @param background A composable that is stacked behind the content and is exposed when the content
// *   is swiped. You can/should use the [state] to have different backgrounds on each side.
// * @param dismissContent The content that can be dismissed.
// */
//@Composable
//@ExperimentalMaterialApi
//@Suppress("ReferencesDeprecated")
//fun SwipeToDismiss(
//    state: DismissState,
//    modifier: Modifier = Modifier,
//    directions: Set<DismissDirection> = setOf(EndToStart, StartToEnd),
//    dismissThresholds: (DismissDirection) -> ThresholdConfig = {
//        FixedThreshold(DISMISS_THRESHOLD)
//    },
//    background: @Composable RowScope.() -> Unit,
//    dismissContent: @Composable RowScope.() -> Unit
//) =
//    BoxWithConstraints(modifier) {
//        val width = constraints.maxWidth.toFloat()
//        val isRtl = LocalLayoutDirection.current == LayoutDirection.Rtl
//
//        val anchors = mutableMapOf(0f to Default)
//        if (StartToEnd in directions) anchors += width to DismissedToEnd
//        if (EndToStart in directions) anchors += -width to DismissedToStart
//
//        val thresholds = { from: DismissValue, to: DismissValue ->
//            dismissThresholds(getDismissDirection(from, to)!!)
//        }
//        val minFactor =
//            if (EndToStart in directions) StandardResistanceFactor else StiffResistanceFactor
//        val maxFactor =
//            if (StartToEnd in directions) StandardResistanceFactor else StiffResistanceFactor
//        Box(
//            Modifier.swipeable(
//                state = state,
//                anchors = anchors,
//                thresholds = thresholds,
//                orientation = Orientation.Horizontal,
//                enabled = state.currentValue == Default,
//                reverseDirection = isRtl,
//                resistance =
//                ResistanceConfig(
//                    basis = width,
//                    factorAtMin = minFactor,
//                    factorAtMax = maxFactor
//                )
//            )
//        ) {
//            Row(content = background, modifier = Modifier.matchParentSize())
//            Row(
//                content = dismissContent,
//                modifier = Modifier.offset { IntOffset(state.offset.value.roundToInt(), 0) }
//            )
//        }
//    }
//
//private fun getDismissDirection(from: DismissValue, to: DismissValue): androidx.compose.material.DismissDirection? {
//    return when {
//        // settled at the default state
//        from == to && from == Default -> null
//        // has been dismissed to the end
//        from == to && from == DismissedToEnd -> StartToEnd
//        // has been dismissed to the start
//        from == to && from == DismissedToStart -> EndToStart
//        // is currently being dismissed to the end
//        from == Default && to == DismissedToEnd -> StartToEnd
//        // is currently being dismissed to the start
//        from == Default && to == DismissedToStart -> EndToStart
//        // has been dismissed to the end but is now animated back to default
//        from == DismissedToEnd && to == Default -> StartToEnd
//        // has been dismissed to the start but is now animated back to default
//        from == DismissedToStart && to == Default -> EndToStart
//        else -> null
//    }
//}
//
//private val DISMISS_THRESHOLD = 56.dp