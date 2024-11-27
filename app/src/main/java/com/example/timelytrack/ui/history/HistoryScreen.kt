@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.timelytrack.ui.history

//import android.graphics.Color

import android.util.Log
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
import com.example.timelytrack.ui.home.groupLogsByDate
import com.example.timelytrack.viewmodel.LogViewModel
import kotlinx.coroutines.launch
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import com.example.timelytrack.model.LogEntry

import com.patrykandpatrick.vico.core.*
import com.patrykandpatrick.vico.compose.*
import com.patrykandpatrick.vico.compose.cartesian.CartesianChartHost
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberColumnCartesianLayer
import com.patrykandpatrick.vico.compose.cartesian.rememberCartesianChart
import com.patrykandpatrick.vico.views.*
import com.patrykandpatrick.vico.compose.common.*
import com.patrykandpatrick.vico.core.cartesian.data.CartesianChartModelProducer
import com.patrykandpatrick.vico.core.cartesian.data.columnSeries
import com.patrykandpatrick.vico.core.cartesian.CartesianLayerInsetter
import com.patrykandpatrick.vico.core.cartesian.axis.HorizontalAxis
import com.patrykandpatrick.vico.core.cartesian.axis.VerticalAxis
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberBottom
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberStart
import com.patrykandpatrick.vico.core.cartesian.data.CartesianValueFormatter
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import kotlin.math.roundToInt
import kotlin.random.Random

//import com.patrykandpatrick.vico.compose.axis.vertical.startAxis
//import com.patrykandpatrick.vico.compose.chart.Chart
//import com.patrykandpatrick.vico.compose.chart.column.columnChart
//import com.patrykandpatrick.vico.compose.style.ProvideChartStyle
//import com.patrykandpatrick.vico.core.entry.entryModelOf
//import com.patrykandpatrick.vico.core.entry.ChartEntry
//import com.patrykandpatrick.vico.compose.cartesian.axis.
//import com.patrykandpatrick.vico.compose.axis.vertical.startAxis

@Preview(showBackground = true)
@Composable
fun HistoryScreenPreview() {
    HistoryScreen()
}




@Composable
fun HistoryScreen() {

    // Data retrieve
    val viewModel: LogViewModel = viewModel(factory = LogViewModel.Factory)
    val logEntries = viewModel.allLogEntries.collectAsState()
    val groupedLogs = groupLogsByDate(logEntries)
    val groupedLogDates = groupedLogs.keys.toList()

    Scaffold(
        content={ innerPadding ->
            Text(innerPadding.toString())
            LazyColumn{

                item{
                    groupedLogs.forEach { (date, logs) ->
                        Text("Date: $date" + " " + "Logs: ${logs.size}")
                    }
                }

//                item { DemoSwipeToDismissBox() }
//                item { DemoBottomSheet() }
//                item { DemoTimePickerWithError()}
//                item{DemoYCharts()}


                item{DemoVicoChart(groupedLogs)}
            }
        }
    )

}

@Composable
fun DemoVicoChart(groupedLogs: Map<String, List<LogEntry>>) {
//    val chartEntries = remember(groupedLogs) {
//        groupedLogs.entries.mapIndexed { index, (date, logs) ->
//            LogEntry(x = index.toFloat(), y = logs.size.toFloat())
//        }
//    }

//    val logsSizeSeries = groupedLogs.map { it.value.size } // Get logs size as series
//    val logsDateGroups = groupedLogs.map {it.key.split(" ").get(1) + " " + it.key.split(" ").get(2).replace(",", "")}
    // Generate a list of dates for November 2024
    val formatter = DateTimeFormatter.ofPattern("MMM dd")
    val dates = (1..30).map { day ->
        LocalDate.of(2024, 11, day).format(formatter) // Format as "Nov 01", "Nov 02", etc.
    }

    // Generate random values for each day
    val values = List(30) { Random.nextInt(0, 20) } // Random log counts (0-20)

    // Combine dates and values into a dummy groupedLogs structure
    val groupedLogs = dates.zip(values).associate { it.first to it.second }

    // Chart configuration
    val logsSizeSeries = groupedLogs.values.toList() // Extract values for the chart
    val logsDateGroups = groupedLogs.keys.toList() // Extract dates for labels


//    val logsSizeSeries = listOf(4f, 12f, 8f) // Heights of the bars
//    val logsDateGroups = listOf("Nov 27", "Nov 28", "Nov 29") // Labels for each bar

    // Create chart entries with explicit x values
    val entries = logsSizeSeries.mapIndexed { index, size ->
        index.toFloat() to size // Pair of (x, y) for each bar
    }


    // Chart configuration
    val modelProducer = remember { CartesianChartModelProducer() }
    LaunchedEffect(logsSizeSeries) {
        modelProducer.runTransaction { columnSeries { series(logsSizeSeries) }

        }
    }

    LaunchedEffect(logsSizeSeries) {
        modelProducer.runTransaction {
            columnSeries {
                series(logsSizeSeries) // Pass the log sizes directly as a collection
            }
        }
    }



//
////    val modelProducer = remember { CartesianChartModelProducer() }
//    LaunchedEffect(Unit) {
////        modelProducer.runTransaction { columnSeries { series(4, 12, 8, 16) } }
//        modelProducer.runTransaction { columnSeries { series(4, 12, 8, 16) } }
//    }
    CartesianChartHost(
        rememberCartesianChart(
            rememberColumnCartesianLayer(),
            startAxis = VerticalAxis.rememberStart(
//                valueFormatter = CartesianValueFormatter.
            ),
            bottomAxis = HorizontalAxis.rememberBottom(
                valueFormatter = CartesianValueFormatter { _, x, _ ->
                    // Map x to the corresponding date label
                    logsDateGroups.getOrNull(x.roundToInt()) ?: "" // Ensure safety with getOrNull
                },
                itemPlacer = HorizontalAxis.ItemPlacer.aligned(spacing = 1) // Adjust spacing
            ),
        ),
        modelProducer,
        modifier = Modifier
            .fillMaxWidth()
            .height(300.dp)
    )
//    )
//    val model = entryModelOf(chartEntries)
//
//    ProvideChartStyle(
//        chartStyle = com.patrykandpatrick.vico.core.DefaultColors.Blue.toChartStyle()
//    ) {
//        Chart(
//            chart = columnChart(),
//            model = model,
//            startAxis = startAxis(
//                label = { value, _ -> "${value.toInt()}" }
//            ),
//            bottomAxis = bottomAxis(
//                label = { value, _ ->
//                    groupedLogs.keys.elementAtOrNull(value.toInt()) ?: ""
//                }
//            ),
//            modifier = Modifier
//                .fillMaxWidth()
//                .height(300.dp)
//        )
//    }
}

//
//@Composable
//fun DemoYCharts() {
////    val barData = DataUtils.getBarChartData(50, maxRange, BarChartType.VERTICAL, DataCategoryOptions())
////    Log.e("barData", barData.toString())
//
//    // make a slider to control value of bar data
//    var sliderPosition by remember { mutableFloatStateOf(0f) }
//
//
//    val customBarData = listOf(
//        BarData(label = "Jan", point = Point(10f, 20f)),
//        BarData(label = "Feb", point = Point(20f, 30f)),
//        BarData(label = "Mar", point = Point(15f, 25f)),
//        BarData(label = "Apr", point = Point(5f, 10f)),
//        BarData(label = "May", point = Point(30f, 40f))
//    )
//
//    val maxRange = 40
//    val yStepSize = 10
//
//    val xAxisData = AxisData.Builder()
//        .axisStepSize(30.dp)
//        .steps(customBarData.size - 1)
//        .bottomPadding(40.dp)
//        .axisLabelAngle(20f)
//        .startDrawPadding(48.dp)
//        .labelData { index -> customBarData[index].label }
//        .build()
//
//    val yAxisData = AxisData.Builder()
//        .steps(yStepSize)
//        .labelAndAxisLinePadding(20.dp)
//        .axisOffset(20.dp)
//        .labelData { index -> (index * (maxRange / yStepSize)).toString() }
//        .build()
//
//    val barChartData = BarChartData(
//        chartData = customBarData,
//        xAxisData = xAxisData,
//        yAxisData = yAxisData,
//        barStyle = BarStyle(
//            paddingBetweenBars = 20.dp,
//            barWidth = 25.dp
//        ),
//        showYAxis = true,
//        showXAxis = true,
//        horizontalExtraSpace = 10.dp,
//    )
//
//    Column {
//        Text(customBarData.toString())
//        BarChart(modifier = Modifier.height(350.dp), barChartData = barChartData)
//    }
//    Column {
////        Text(customBarData.toString())
//
//
//        BarChart(modifier = Modifier.height(350.dp), barChartData = barChartData)
//            Slider(
//                value = sliderPosition,
//                onValueChange = { sliderPosition = it }
//            )
//            Text(text = sliderPosition.toString())
//    }
//}



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


@Preview
@Composable
fun ColorSchemePreview() {
    MaterialTheme {
        Column {
            ColorDisplay(color = MaterialTheme.colorScheme.tertiary, name = "primary")
            ColorDisplay(color = MaterialTheme.colorScheme.onPrimary, name = "onPrimary")
            ColorDisplay(color = MaterialTheme.colorScheme.primaryContainer, name = "primaryContainer")
            // ... display other colors similarly
        }
    }
}

@Composable
fun ColorDisplay(color: Color, name: String) {
    Box(
        modifier = Modifier
            .size(100.dp)
            .background(color),
        contentAlignment = Alignment.Center
    ) {
        Text(text = name, color = Color.White)
    }
}

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