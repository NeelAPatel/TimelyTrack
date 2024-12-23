@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.timelytrack.ui.history

//import android.graphics.Color

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.getValue

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.timelytrack.viewmodel.LogViewModel
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowRightAlt
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.runtime.State
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.ui.zIndex
import com.example.timelytrack.model.LogEntry

import com.patrykandpatrick.vico.compose.cartesian.CartesianChartHost
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberAxisLabelComponent
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberAxisLineComponent
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberColumnCartesianLayer
import com.patrykandpatrick.vico.compose.cartesian.rememberCartesianChart
import com.patrykandpatrick.vico.core.cartesian.data.CartesianChartModelProducer
import com.patrykandpatrick.vico.core.cartesian.data.columnSeries
import com.patrykandpatrick.vico.core.cartesian.axis.HorizontalAxis
import com.patrykandpatrick.vico.core.cartesian.axis.VerticalAxis
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberBottom
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberStart
import com.patrykandpatrick.vico.compose.cartesian.rememberVicoScrollState
import com.patrykandpatrick.vico.compose.cartesian.rememberVicoZoomState
import com.patrykandpatrick.vico.compose.common.component.rememberLineComponent
import com.patrykandpatrick.vico.compose.common.component.rememberTextComponent
import com.patrykandpatrick.vico.core.cartesian.CartesianMeasuringContext
import com.patrykandpatrick.vico.core.cartesian.axis.Axis
import com.patrykandpatrick.vico.core.cartesian.data.CartesianValueFormatter
import com.patrykandpatrick.vico.core.cartesian.layer.ColumnCartesianLayer
import com.patrykandpatrick.vico.core.cartesian.marker.DefaultCartesianMarker
import com.patrykandpatrick.vico.core.common.Fill
import com.patrykandpatrick.vico.core.common.shape.CorneredShape
import kotlinx.coroutines.launch
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
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen() {
    // Variables

    // Database
    val viewModel: LogViewModel = viewModel(factory = LogViewModel.Factory)
    val logEntries = viewModel.allLogEntries.collectAsState()

    //get user data

    // For date range selector
    val startOfToday: Long = Calendar.getInstance().apply {
        timeInMillis = System.currentTimeMillis()
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }.timeInMillis
    val endOfToday: Long = Calendar.getInstance().apply {
        timeInMillis = System.currentTimeMillis()
        set(Calendar.HOUR_OF_DAY, 23)
        set(Calendar.MINUTE, 59)
        set(Calendar.SECOND, 59)
        set(Calendar.MILLISECOND, 999)
    }.timeInMillis

    val startOfRange: Long = Calendar.getInstance().apply {
        timeInMillis = startOfToday
        add(Calendar.DAY_OF_YEAR, -30) // Go back 30 days
    }.timeInMillis



    var lastKnownDateRange by rememberSaveable {
        mutableStateOf<Pair<Long?, Long?>>(startOfRange to endOfToday) // Default to 30-day range
    }
    var selectedDateRange by rememberSaveable {
        mutableStateOf<Pair<Long?, Long?>>(lastKnownDateRange) // Initially, the selected range matches the last known range
    }
//    var selectedDateRange by remember { mutableStateOf<Pair<Long?, Long?>>(logEntries.value.map { it.startTimestamp }.sorted().first() to logEntries.value.map { it.startTimestamp }.sorted().last()) }
    var showSelectedDateRangeModal by remember { mutableStateOf(false) }
    var showDataVisibilityAdjustmentModal by remember { mutableStateOf(false) }

    // Future Visbility modifiers
        // show Empty days?
    var showEmptyDaysChecked by rememberSaveable { mutableStateOf(false) }
// Future Visibility modifiers
    var showFullDataRangeChecked by rememberSaveable { mutableStateOf(false) }

        // show All data?

    // For Aggregation Filter
    var singleChoiceSelectedIndex by remember { mutableStateOf(1) } // default is daily
    val singleChoiceSelectorOptions = listOf("Hourly", "Daily", "Weekly", "Monthly")

    // === UI ====
    Scaffold() { innerPadding ->
        LazyColumn(
            modifier = Modifier.padding(innerPadding).padding(12.dp)
        ) {

            // Row for Date Range Selector + Filter
            item{
                DateRangeSelectorComposable(
                    selectedDateRange = selectedDateRange,
                    onDateRangeClick = {showSelectedDateRangeModal = true},
                    onDataVisibilityClick = {showDataVisibilityAdjustmentModal = true})
                AggregationFilterSelectorComposable(singleChoiceSelectorOptions, singleChoiceSelectedIndex)

                if (showSelectedDateRangeModal) {
                    DateRangePickerModal(
                        initialDateRange = selectedDateRange, // Pass current range to modal
                        onDateRangeSelected = {
                            selectedDateRange = it // Update the selected date range
                            showSelectedDateRangeModal = false // Close the modal
                        },
                        onDismiss = { showSelectedDateRangeModal = false }
                    )

                }

                if (showDataVisibilityAdjustmentModal) {
                    DataVisibilityAdjustmentModal(
                        onDismiss = {
                            showDataVisibilityAdjustmentModal = false // Simply close the modal
                        },
                        onConfirm = { emptyDaysChecked, fullDataRangeChecked ->
                            // Update the states only on Confirm
                            showEmptyDaysChecked = emptyDaysChecked
                            showFullDataRangeChecked = fullDataRangeChecked
                            showDataVisibilityAdjustmentModal = false // Close the modal

                            if (fullDataRangeChecked) {
                                // Update date range when "Show All Data" is checked
                                lastKnownDateRange = selectedDateRange // Save the last known date range before switching
                                selectedDateRange = Pair(
                                    logEntries.value.map { it.startTimestamp }.sorted().firstOrNull(),
                                    endOfToday
                                )
                            } else {
                                // Revert to the last known range if unchecked
                                selectedDateRange = lastKnownDateRange
                            }
                        },
                        initialShowEmptyDaysChecked = showEmptyDaysChecked,
                        initialShowFullDataRangeChecked = showFullDataRangeChecked
                    )
                }
            }

            // Box for Aggregation Chart
            item {
                Box() {
                    AggregationChart(singleChoiceSelectedIndex, logEntries = logEntries, selectedDateRange = selectedDateRange, showEmptyDaysChecked = showEmptyDaysChecked)
                }
            }
            item{Spacer(modifier = Modifier.height(20.dp))
                androidx.compose.material.Divider()
            }

            item {
                Text("Hourly Aggregate")
                Box(
                ) {
                    HourlyDistributionChart(singleChoiceSelectedIndex, logEntries = logEntries)
                }
            }

        }

    }
}

@Composable
fun DataVisibilityAdjustmentModal(
    onDismiss: () -> Unit,
    onConfirm: (Boolean, Boolean) -> Unit, // Callback for Confirm action
    initialShowEmptyDaysChecked: Boolean,
    initialShowFullDataRangeChecked: Boolean,
) {
    // Local state to manage checkbox values temporarily
    var showEmptyDaysChecked by remember { mutableStateOf(initialShowEmptyDaysChecked) }
    var showFullDataRangeChecked by remember { mutableStateOf(initialShowFullDataRangeChecked) }

    Dialog(onDismissRequest = { onDismiss() }) {
        // Draw a rectangle shape with rounded corners inside the dialog
        Card(
            modifier = Modifier
                .padding(24.dp)
                .fillMaxWidth()
                .wrapContentHeight()
                .defaultMinSize(minWidth = 280.dp)
                .then(Modifier.widthIn(min = 280.dp, max = 560.dp)), // Width range: 280dp to 560dp
            shape = RoundedCornerShape(28.dp),
        ) {
            Column(
                modifier = Modifier
                    .padding(24.dp)
                    .wrapContentHeight()
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    text = "Adjust Data Visibility",
                    style = MaterialTheme.typography.titleLarge, // Material 3 title style
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp), // Padding between title and body
                    textAlign = TextAlign.Start
                )
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically // Align content vertically
                    ) {
                        Checkbox(
                            checked = showEmptyDaysChecked,
                            onCheckedChange = { isChecked ->
                                showEmptyDaysChecked = isChecked
                            }
                        )
                        Text(
                            text = "Show Empty Days",
                            modifier = Modifier.padding(start = 8.dp) // Padding between checkbox and text
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically // Align content vertically
                    ) {
                        Checkbox(
                            checked = showFullDataRangeChecked,
                            onCheckedChange = { isChecked ->
                                showFullDataRangeChecked = isChecked
                            }
                        )
                        Text(
                            text = "Show All Data",
                            modifier = Modifier.padding(start = 8.dp) // Padding between checkbox and text
                        )
                    }
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                ) {
                    TextButton(
                        onClick = { onDismiss() }, // Dismiss without saving changes
                        modifier = Modifier.padding(8.dp),
                    ) {
                        Text("Dismiss")
                    }
                    TextButton(
                        onClick = {
                            onConfirm(showEmptyDaysChecked, showFullDataRangeChecked) // Save changes on confirm
                        },
                        modifier = Modifier.padding(8.dp),
                    ) {
                        Text("Confirm")
                    }
                }
            }
        }
    }
}

@Composable
private fun AggregationFilterSelectorComposable(
    singleChoiceSelectorOptions: List<String>,
    singleChoiceSelectedIndex: Int
) {
    var singleChoiceSelectedIndex1 = singleChoiceSelectedIndex
    Row(
        //center aligns content
        modifier = Modifier
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            Icons.Filled.FilterList,
            contentDescription = "Filter",
            modifier = Modifier.padding(start = 8.dp, end = 8.dp)
        )


        SingleChoiceSegmentedButtonRow(
            Modifier.fillMaxWidth(),
        ) {
            singleChoiceSelectorOptions.forEachIndexed { index, label ->
                SegmentedButton(
                    shape = SegmentedButtonDefaults.itemShape(
                        index = index,
                        count = singleChoiceSelectorOptions.size
                    ),
                    onClick = { singleChoiceSelectedIndex1 = index },
                    selected = index == singleChoiceSelectedIndex1
                ) {
                    Text(label)
                }
            }
        }
    }
}

@Composable
private fun DateRangeSelectorComposable(
    onDateRangeClick: () -> Unit,
    selectedDateRange: Pair<Long?, Long?>,
    onDataVisibilityClick: () -> Unit
) {
    Row(
        //center aligns content
        modifier = Modifier
            .fillMaxWidth(),
//        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            Icons.Filled.CalendarMonth,
            contentDescription = "Range Calendar",
            modifier = Modifier.padding(start = 8.dp, end = 8.dp)
        )
        OutlinedButton(
            onClick = onDateRangeClick,
//            modifier = Modifier.padding(8.dp),
        ) {

            Text(SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(Date(selectedDateRange.first!!)))
            Icon(
                Icons.Filled.ArrowRightAlt,
                contentDescription = "Right Arrow",
                modifier = Modifier.padding(start = 8.dp, end = 8.dp)
            )
            Text(SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(Date(selectedDateRange.second!!)))
        }

        Spacer(modifier = Modifier.weight(1f)) // Pushes the next item to the right
        IconButton(
            onClick = onDataVisibilityClick
        ) {
            Icon(
                Icons.Filled.Visibility,
                contentDescription = "Data Visibility",
                modifier = Modifier.padding(8.dp) // Add padding to align it nicely
            )
        }
    }
}

@Composable
@ExperimentalMaterial3Api
fun DateRangePickerModal(
    initialDateRange: Pair<Long?, Long?>,
    onDateRangeSelected: (Pair<Long?, Long?>) -> Unit,
    onDismiss: () -> Unit
) {
    val dateRangePickerState = rememberDateRangePickerState(
        initialSelectedStartDateMillis = initialDateRange.first,
        initialSelectedEndDateMillis = initialDateRange.second
    )
    Dialog(
        onDismissRequest = { onDismiss() }, // Dismiss when tapping outside or pressing back
        properties = DialogProperties(usePlatformDefaultWidth = false) // Full screen dialog
    )  {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.surfaceVariant) // Full background for the dialog
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                // Top bar with Cancel and Save buttons
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = { onDismiss() }) {
                        Icon(
                            imageVector = Icons.Default.Close, // Material Design close icon
                            contentDescription = "Close",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                    // Save button
                    TextButton(
                        onClick = {
                            onDateRangeSelected(
                                Pair(
                                        dateRangePickerState.selectedStartDateMillis?.plus(24L * 60 * 60 * 1000),
                                        dateRangePickerState.selectedEndDateMillis?.plus(24L * 60 * 60 * 1000)
                                )
                            )
                        },
                        enabled = dateRangePickerState.selectedEndDateMillis != null
                    ) {
                        Text("Save")
                    }
                }
                DateRangePicker(
                    state = dateRangePickerState,
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                )
            }

        }
    }
//
//    Dialog(onDismissRequest = onDismiss
//    ) {
//        Box(
//            modifier = Modifier
//                .fillMaxSize() // Ensure the dialog content uses full available space
//                .background(MaterialTheme.colorScheme.background) // Set background color to match the theme
//        ) {
//            Scaffold(
//            topBar = {
//                TopAppBar(
//                    title = { Text("Select Date Range") },
//                    navigationIcon = {
//                        IconButton(onClick = onDismiss) {
//                            Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
//                        }
//                    },
//                    actions = {
//                        TextButton(
//                            onClick = {
//                                onDateRangeSelected(
//                                    Pair(
//                                        dateRangePickerState.selectedStartDateMillis?.plus(24L * 60 * 60 * 1000),
//                                        dateRangePickerState.selectedEndDateMillis?.plus(24L * 60 * 60 * 1000)
//                                    )
//                                )
//                                onDismiss()
//                            }
//                        ) {
//                            Text("Apply")
//                        }
//                    }
//                )
//            }
//        ) { paddingValues ->
//            Column(
//                modifier = Modifier
//                    .fillMaxSize()
//                    .padding(paddingValues)
//            ) {
//                DateRangePicker(
//                    state = dateRangePickerState,
//                    title = {
//                        Text(
//                            text = "Select date range",
//                            modifier = Modifier.padding(16.dp)
//                        )
//                    },
//                    showModeToggle = false,
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .weight(1f)
//                )
//            }
//        }
//    }}

}

@Composable
fun AggregationChart(singleChoiceSelectedIndex: Int, logEntries: State<List<LogEntry>>, selectedDateRange: Pair<Long?, Long?>, showEmptyDaysChecked: Boolean) {

    var (logsDateGroups, logsSizeSeries) = logEntryAggregator(
        singleChoiceSelectedIndex,
        logEntries,
        selectedDateRange, showEmptyDaysChecked
    ) // x, y


    // Trigger Chart data production
    val modelProducer = remember { CartesianChartModelProducer() }
    LaunchedEffect(logsSizeSeries) {
        modelProducer.runTransaction {
            columnSeries {
                if (logsSizeSeries != null) { series(logsSizeSeries)}
            }
        }
    }

    if (logsDateGroups != null)
    {

        // Set up Scrolling/Zooming configuration; empty = default
        val scrollState = rememberVicoScrollState(/* ... */)
        val zoomState = rememberVicoZoomState(/* ... */)


        var axisConfig = AggregationChartAxesConfigurator(logsDateGroups, logsSizeSeries, singleChoiceSelectedIndex)

        val startAxisConfig = axisConfig.first
        val endAxisConfig = axisConfig.second
        val topAxisConfig = axisConfig.third.first
        val bottomAxisConfig = axisConfig.third.second
        // Set up Axis Configuration; null = no axes


        CartesianChartHost(
            rememberCartesianChart( //Chart Data Provider
                rememberColumnCartesianLayer( // Chart UI
                    ColumnCartesianLayer.ColumnProvider.series(
                        rememberLineComponent(
                            fill = Fill(Color(MaterialTheme.colorScheme.primary.toArgb()).toArgb()),
                            thickness = 6.dp,
                            shape = CorneredShape.rounded(allPercent = 40),
                        )
                    )
                ),
                startAxis = startAxisConfig,
                endAxis = endAxisConfig,
                topAxis = topAxisConfig,
                bottomAxis = bottomAxisConfig,
                marker = rememberMarker(DefaultCartesianMarker.LabelPosition.AbovePoint)
            ),
            modelProducer,
            scrollState = scrollState,
            zoomState = zoomState,
            modifier = Modifier // for entire graph
                .fillMaxWidth()
                .height(300.dp)
                .then(Modifier.graphicsLayer { rotationZ = 0f })
        )
    }
    else{
        Text("No data to display")
    }
}



@Composable
fun HourlyDistributionChart(singleChoiceSelectedIndex: Int, logEntries: State<List<LogEntry>>) {
    val logsDateGroups = mutableListOf<String>() // Labels for the chart
    val logsSizeSeries = mutableListOf<Int>() // Values for the chart

    // Prepare a map with all 24 hours initialized to 0
    val hourFormat = SimpleDateFormat("hh:00 a", Locale.getDefault())
    val hourlyMap = (0..23).associate { hour ->
        // Create a Date object for each hour of the day
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, hour)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        hourFormat.format(calendar.time) to 0
    }.toMutableMap()

    // Aggregate log entries into the hourly map
    val calendar = Calendar.getInstance()
    logEntries.value.forEach { logEntry ->
        calendar.timeInMillis = logEntry.startTimestamp
        val hourKey = hourFormat.format(calendar.time)
        hourlyMap[hourKey] = hourlyMap.getOrDefault(hourKey, 0) + 1
    }


    // Populate the labels and series from the hourly map
    hourlyMap.forEach { (hour, count) ->
        logsDateGroups.add(hour)
        logsSizeSeries.add(count)
    }


    // Trigger Chart data production
    val modelProducer = remember { CartesianChartModelProducer() }
    LaunchedEffect(logsSizeSeries) {
        modelProducer.runTransaction {
            columnSeries {
                if (logsSizeSeries != null) {
                    series(logsSizeSeries)
                }
            }
        }
    }

    if (logsDateGroups != null) {

        // Set up Scrolling/Zooming configuration; empty = default
        val scrollState = rememberVicoScrollState(/* ... */)
        val zoomState = rememberVicoZoomState(/* ... */)


        var axisConfig =
            AggregationChartAxesConfigurator(logsDateGroups, logsSizeSeries, singleChoiceSelectedIndex)

        val startAxisConfig = axisConfig.first
        val endAxisConfig = axisConfig.second
        val topAxisConfig = axisConfig.third.first
        val bottomAxisConfig = axisConfig.third.second
        // Set up Axis Configuration; null = no axes


        CartesianChartHost(
            rememberCartesianChart( //Chart Data Provider
                rememberColumnCartesianLayer( // Chart UI
                    ColumnCartesianLayer.ColumnProvider.series(
                        rememberLineComponent(
                            fill = Fill(Color(MaterialTheme.colorScheme.primary.toArgb()).toArgb()),
                            thickness = 6.dp,
                            shape = CorneredShape.rounded(allPercent = 40),
                        )
                    )
                ),
                startAxis = startAxisConfig,
                endAxis = endAxisConfig,
                topAxis = topAxisConfig,
                bottomAxis = HorizontalAxis.rememberBottom(
                    label = rememberAxisLabelComponent(color = MaterialTheme.colorScheme.onSurface),
                    titleComponent = rememberTextComponent(color = MaterialTheme.colorScheme.onSurface),
                    labelRotationDegrees = -90f,
                    valueFormatter = CartesianValueFormatter { _, x, _ ->
                        logsDateGroups.getOrNull(x.roundToInt()) ?: " XX " // Ensure safety with getOrNull
                    }),
                marker = rememberMarker(DefaultCartesianMarker.LabelPosition.AbovePoint)
            ),
            modelProducer,
            scrollState = scrollState,
            zoomState = zoomState,
            modifier = Modifier // for entire graph
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


/** Formats axes for the first graph **/
@Composable
fun AggregationChartAxesConfigurator(logsDateGroups: MutableList<String>, logsSizeSeries: MutableList<Int>?, singleChoiceSelectedIndex: Int):
        Triple<VerticalAxis<Axis.Position.Vertical.Start>?, VerticalAxis<Axis.Position.Vertical.End>?, Pair<HorizontalAxis<Axis.Position.Horizontal.Top>?, HorizontalAxis<Axis.Position.Horizontal.Bottom>?>> {



    val maxValue = logsSizeSeries?.maxOrNull() ?: 10 // Default maximum value
    val stepSize = when {
        maxValue <= 10 -> 1.0
        maxValue <= 50 -> 5.0
        else -> 10.0
    }


    var startAxisConfig: VerticalAxis<Axis.Position.Vertical.Start>? = VerticalAxis.rememberStart(
//        valueFormatter = integer(),
        valueFormatter = integer(),
        tickLength = 5.dp,
        itemPlacer = VerticalAxis.ItemPlacer.step({ _ -> stepSize } ), // Adjusts dynamically
        label = rememberAxisLabelComponent(color = MaterialTheme.colorScheme.onSurface),
        titleComponent = rememberTextComponent(color = MaterialTheme.colorScheme.onSurface)
    )
    var endAxisConfig: VerticalAxis<Axis.Position.Vertical.End>? = null
    var topAxisConfig: HorizontalAxis<Axis.Position.Horizontal.Top>? = null
    var bottomAxisConfig: HorizontalAxis<Axis.Position.Horizontal.Bottom>? = HorizontalAxis.rememberBottom(
        label = rememberAxisLabelComponent(color = MaterialTheme.colorScheme.onSurface),
        titleComponent = rememberTextComponent(color = MaterialTheme.colorScheme.onSurface),
        labelRotationDegrees = 0f,
        line = rememberAxisLineComponent(),
        valueFormatter = CartesianValueFormatter { _, x, _ ->
            logsDateGroups.getOrNull(x.roundToInt()) ?: " " // Ensure safety with getOrNull
        },
        itemPlacer = HorizontalAxis.ItemPlacer.aligned(spacing = 1), // Adjust spacing
        title = "Hours",

    )

    when (singleChoiceSelectedIndex){
        0 -> {
//            startAxisConfig = VerticalAxis.rememberStart( valueFormatter = integer())
            endAxisConfig =null
//            topAxisConfig = HorizontalAxis.rememberTop( valueFormatter = integer())
            bottomAxisConfig = HorizontalAxis.rememberBottom(
                label = rememberAxisLabelComponent(color = MaterialTheme.colorScheme.onSurface),
                titleComponent = rememberTextComponent(color = MaterialTheme.colorScheme.onSurface),
                labelRotationDegrees = -90f,
                line = rememberAxisLineComponent(),
                valueFormatter = CartesianValueFormatter { _, x, _ ->
                    logsDateGroups.getOrNull(x.roundToInt()) ?: " " // Ensure safety with getOrNull
                }
            )
        }
        1 -> {
//            topAxisConfig.title = "Hours"
        }
        2 -> {
//            topAxisConfig.title = "Days"
//            bottomAxisConfig.title = "Weeks"
        }
        3 -> {
//            topAxisConfig.title = "Months"
//            bottomAxisConfig.title = "Years"
        }
    }


    var axisConfig = Triple(startAxisConfig, endAxisConfig, Pair(topAxisConfig, bottomAxisConfig))
    return axisConfig
}



fun logEntryAggregator (
    singleChoiceSelectedIndex: Int,
    logEntries: State<List<LogEntry>>,
    selectedDateRange: Pair<Long?, Long?>,
    showEmptyDaysChecked: Boolean
): Pair<MutableList<String>?, MutableList<Int>?> {
    // Switch case based on index 0,1,2,3 to determine different ways of aggegating logEntries

    //Empty
    val logsDateGroups = mutableListOf<String>() // Extract dates for labels
    val logsSizeSeries = mutableListOf<Int>() // Extract values for the chart




    when (singleChoiceSelectedIndex) {
        0 -> {
            // Hourly breakdown: Format as <Hour> <AM/PM>
            logsDateGroups.clear()
            logsSizeSeries.clear()
            val dateFormat = SimpleDateFormat("MMM dd, HH:mm a", Locale.getDefault())
            val groupedLogs = logEntries.value.groupBy { logEntry -> dateFormat.format(Date(logEntry.startTimestamp)) }

            groupedLogs.forEach { (key, value) ->
                // Extract date from the key, e.g., "Monday Nov 21, 2022" -> "Nov 21"
                //val date = key.split(" ").get(0) + " " + key.split(" ").get(1).replace(",", "")
                logsDateGroups.add(key)

                // Add the size of the LogEntry list to logsSizeSeries
                logsSizeSeries.add(value.size)
            }

            return Pair(logsDateGroups, logsSizeSeries)
        }
        1 -> {
            // Daily breakdown: Format as <Month> <Day>
            val dateFormat = SimpleDateFormat("MMM dd", Locale.getDefault())
            val allDates = mutableListOf<String>()
            val logsDateGroups = mutableListOf<String>()
            val logsSizeSeries = mutableListOf<Int>()

            if (logEntries.value.isNotEmpty() && selectedDateRange.first != null && selectedDateRange.second != null) {
                val startTimestamp = selectedDateRange.first!!
                val endTimestamp = selectedDateRange.second!!

                if (showEmptyDaysChecked) {
                    // Show empty days (fill in with 0 for missing dates)
                    val startDate = Calendar.getInstance().apply { time = Date(startTimestamp) }
                    val endDate = Calendar.getInstance().apply { time = Date(endTimestamp) }

                    // Generate all dates within the selected date range
                    while (!startDate.after(endDate)) {
                        allDates.add(dateFormat.format(startDate.time))
                        startDate.add(Calendar.DATE, 1) // Increment by 1 day
                    }

                    // Group the logs by date within the selected range
                    val groupedLogs = logEntries.value
                        .filter { it.startTimestamp in startTimestamp..endTimestamp }
                        .groupBy { logEntry ->
                            dateFormat.format(Date(logEntry.startTimestamp))
                        }

                    // Fill in the logsDateGroups and logsSizeSeries, accounting for missing dates
                    allDates.forEach { date ->
                        logsDateGroups.add(date)
                        logsSizeSeries.add(groupedLogs[date]?.size ?: 0) // Add 0 if no logs for the date
                    }
                } else {
                    // Do not show empty days (only include days with data)
                    val groupedLogs = logEntries.value
                        .filter { it.startTimestamp in startTimestamp..endTimestamp }
                        .groupBy { logEntry ->
                            dateFormat.format(Date(logEntry.startTimestamp))
                        }

                    // Add only dates with data to the groups
                    groupedLogs.forEach { (date, logs) ->
                        logsDateGroups.add(date)
                        logsSizeSeries.add(logs.size)
                    }
                }
            }

            Log.e("logsDateGroups", logsDateGroups.toString())
            Log.e("logsSizeSeries", logsSizeSeries.toString())
            return Pair(logsDateGroups, logsSizeSeries)
        }
        -1 -> {
            // Daily breakdown: Format as <Month> <Day>
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
            Log.e("logsDateGroups", logsDateGroups.toString())
            Log.e("logsSizeSeries", logsSizeSeries.toString())
            return Pair(logsDateGroups, logsSizeSeries)
        }
        2 -> {
            // Weekly breakdown: Format as <Month> <week start date> - <week end date>
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
            // Monthly breakdown: Format as <Month> (e.g., "Jan", "Feb")
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

