@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.timelytrack.ui.history

//import android.graphics.Color

import android.widget.Space
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
import androidx.compose.runtime.State
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.toArgb
import com.example.timelytrack.model.LogEntry

import com.patrykandpatrick.vico.compose.cartesian.CartesianChartHost
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberColumnCartesianLayer
import com.patrykandpatrick.vico.compose.cartesian.rememberCartesianChart
import com.patrykandpatrick.vico.core.cartesian.data.CartesianChartModelProducer
import com.patrykandpatrick.vico.core.cartesian.data.columnSeries
import com.patrykandpatrick.vico.core.cartesian.axis.HorizontalAxis
import com.patrykandpatrick.vico.core.cartesian.axis.VerticalAxis
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberBottom
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberEnd
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberStart
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberTop
import com.patrykandpatrick.vico.compose.cartesian.rememberVicoScrollState
import com.patrykandpatrick.vico.compose.cartesian.rememberVicoZoomState
import com.patrykandpatrick.vico.compose.common.component.rememberLineComponent
import com.patrykandpatrick.vico.core.cartesian.CartesianMeasuringContext
import com.patrykandpatrick.vico.core.cartesian.axis.Axis
import com.patrykandpatrick.vico.core.cartesian.data.CartesianValueFormatter
import com.patrykandpatrick.vico.core.cartesian.layer.ColumnCartesianLayer
import com.patrykandpatrick.vico.core.cartesian.marker.DefaultCartesianMarker
import com.patrykandpatrick.vico.core.common.Fill
import com.patrykandpatrick.vico.core.common.shape.CorneredShape
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
@Composable
fun HistoryScreen() {

    //=== Variables ===
    var singleChoiceSelectedIndex by remember { mutableStateOf(1) }
    val singleChoiceSelectorOptions = listOf("Hourly", "Daily", "Weekly", "Monthly")
    val viewModel: LogViewModel = viewModel(factory = LogViewModel.Factory)
    val logEntries = viewModel.allLogEntries.collectAsState()

    // === Launch Effects ===

    // === UI ====
    Scaffold() { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .padding(innerPadding).padding(8.dp)
        ) {
            item {Text("Overall History")}
            // Single Choice Selector

            // Graph 1
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp)
                ) {
                    GeneralChart(singleChoiceSelectedIndex, logEntries = logEntries)
                }
            }
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

            item{Spacer(modifier = Modifier.height(20.dp))}
            item{ androidx.compose.material.Divider()}

            item {Text("Hourly Aggregate")}
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp)
                ) {
                    HourAggregateChart(singleChoiceSelectedIndex, logEntries = logEntries)
                }
            }

        }

    }
}

@Composable
fun HourAggregateChart(singleChoiceSelectedIndex: Int, logEntries: State<List<LogEntry>>) {
    val logsDateGroups = mutableListOf<String>() // Labels for the chart
    val logsSizeSeries = mutableListOf<Int>() // Values for the chart

//    // Prepare a map with all 24 hours initialized to 0
//    val hourlyMap = (0..23).associate { hour ->
////        String.format("%02d:00", hour) to 0
//        String.format("%02d:00", hour) to 0
//    }.toMutableMap()
//
//    // Aggregate log entries into the hourly map
//    val calendar = Calendar.getInstance()
//    logEntries.value.forEach { logEntry ->
//        calendar.timeInMillis = logEntry.startTimestamp
//        val hourKey = String.format("%02d:00", calendar.get(Calendar.HOUR_OF_DAY))
//        hourlyMap[hourKey] = hourlyMap.getOrDefault(hourKey, 0) + 1
//    }


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
            generalChartAxesConfigurator(logsDateGroups, logsSizeSeries, singleChoiceSelectedIndex)

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
                    labelRotationDegrees = -90f,
                    valueFormatter = CartesianValueFormatter { _, x, _ ->
                        logsDateGroups.getOrNull(x.roundToInt()) ?: " " // Ensure safety with getOrNull
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



@Composable
fun GeneralChart(singleChoiceSelectedIndex: Int, logEntries: State<List<LogEntry>>) {


    var (logsDateGroups, logsSizeSeries) = logEntryAggregator(singleChoiceSelectedIndex, logEntries) // x, y



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


        var axisConfig = generalChartAxesConfigurator(logsDateGroups, logsSizeSeries, singleChoiceSelectedIndex)

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
fun generalChartAxesConfigurator(logsDateGroups: MutableList<String>, logsSizeSeries: MutableList<Int>?, singleChoiceSelectedIndex: Int):
        Triple<VerticalAxis<Axis.Position.Vertical.Start>?, VerticalAxis<Axis.Position.Vertical.End>?, Pair<HorizontalAxis<Axis.Position.Horizontal.Top>?, HorizontalAxis<Axis.Position.Horizontal.Bottom>?>> {

    var startAxisConfig: VerticalAxis<Axis.Position.Vertical.Start>? = VerticalAxis.rememberStart( valueFormatter = integer())
    var endAxisConfig: VerticalAxis<Axis.Position.Vertical.End>? = null
    var topAxisConfig: HorizontalAxis<Axis.Position.Horizontal.Top>? = null
    var bottomAxisConfig: HorizontalAxis<Axis.Position.Horizontal.Bottom>? = HorizontalAxis.rememberBottom(
        labelRotationDegrees = 0f,
        valueFormatter = CartesianValueFormatter { _, x, _ ->
            logsDateGroups.getOrNull(x.roundToInt()) ?: " " // Ensure safety with getOrNull
        },
        itemPlacer = HorizontalAxis.ItemPlacer.aligned(spacing = 1), // Adjust spacing
    )

    when (singleChoiceSelectedIndex){
        0 -> {
//            startAxisConfig = VerticalAxis.rememberStart( valueFormatter = integer())
            endAxisConfig =null
//            topAxisConfig = HorizontalAxis.rememberTop( valueFormatter = integer())
            bottomAxisConfig = HorizontalAxis.rememberBottom(
                labelRotationDegrees = -90f,
                valueFormatter = CartesianValueFormatter { _, x, _ ->
                    logsDateGroups.getOrNull(x.roundToInt()) ?: " " // Ensure safety with getOrNull
                })
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

