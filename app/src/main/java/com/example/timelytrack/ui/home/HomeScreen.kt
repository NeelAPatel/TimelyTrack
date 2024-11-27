@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class)

package com.example.timelytrack.ui.home


import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.timelytrack.model.LogEntry
import com.example.timelytrack.viewmodel.LogViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class)
@ExperimentalFoundationApi
@Composable
fun HomeScreen() {
    // Fetch the entries as the base data for the screen
    val viewModel: LogViewModel = viewModel(factory = LogViewModel.Factory)
    val logEntries = viewModel.allLogEntries.collectAsState()
    val scope = rememberCoroutineScope()

    //Group logs by date
    val groupedLogs = groupLogsByDate(logEntries)
    val groupedLogDates = groupedLogs.keys.toList()

    // FAB Visibility
    var isFabVisible by remember { mutableStateOf(true) }
    var wasFABTapped by remember { mutableStateOf(false) } // Track if EFAB was tapped

    // Variables for multi-selection ## NOT WORKING
    val selectedLogEntries by viewModel.selectedLogEntries.collectAsState()

    //Variables for editing a single log entry with BottomSheet
    // State for managing the bottom sheet and selected log
    var selectedLogToEdit by remember { mutableStateOf<LogEntry?>(null)}
    var isBottomSheetOpen by rememberSaveable { mutableStateOf(false) }  // Open or close toggle
    val bottomSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = false) // remember the state of bottom sheet, opened or closed

    // Scroll state for LazyColumn and syncing with carousel
    val logColumnListState = rememberLazyListState()
    val logRowCarouselState = rememberLazyListState()
    var selectedCarouselDate by remember { mutableStateOf(groupedLogDates.firstOrNull() ?: "") }

    //Scroll to latest log at launch
    LaunchedEffect(logEntries.value.size) {
        if (logEntries.value.isNotEmpty()) {
            scope.launch {
                logColumnListState.scrollToItem(logEntries.value.size - 1)
            }
        }
    }


    LaunchedEffect(logColumnListState) {
        snapshotFlow { logColumnListState.layoutInfo.visibleItemsInfo to logColumnListState.firstVisibleItemIndex }
            .collect { (visibleItemsInfo)->
                // == fab control ==

                if (logEntries.value.isEmpty()) {
                    isFabVisible = true
                    return@collect
                }

                val lastIndex = logEntries.value.size - 1
                val isLastItemVisible = visibleItemsInfo.any { it.index == lastIndex }

                if (!wasFABTapped) {
                    isFabVisible = isLastItemVisible
                } else {
                    // Reset `wasFABTapped` when scrolling away from the last item
                    val isScrolledAway = visibleItemsInfo.none { it.index == lastIndex }
                    if (isScrolledAway) {
                        wasFABTapped = false
                    }
                }
            }
    }

    Scaffold(
        floatingActionButtonPosition = FabPosition.End,
        floatingActionButton = {
                SmallFloatingActionButton(
                    onClick = {
                        viewModel.addLogEntryWithManualDate("1", "Nov 21 2022", "10:00")
                        viewModel.addLogEntryWithManualDate("1", "Nov 21 2022", "15:00")
                        viewModel.addLogEntryWithManualDate("1", "Nov 22 2022", "09:30")
                        viewModel.addLogEntryWithManualDate("1", "Nov 22 2022", "13:45")
                        viewModel.addLogEntryWithManualDate("1", "Nov 22 2022", "09:30")
                        viewModel.addLogEntryWithManualDate("1", "Nov 23 2022", "09:30")
                        viewModel.addLogEntryWithManualDate("1", "Nov 23 2022", "13:45")
                        viewModel.addLogEntryWithManualDate("1", "Nov 23 2022", "09:30")
                        viewModel.addLogEntryWithManualDate("1", "Nov 23 2022", "13:45")
                        viewModel.addLogEntryWithManualDate("1", "Nov 23 2022", "09:30")
                        viewModel.addLogEntryWithManualDate("1", "Nov 24 2022", "13:45")

                    },
                    containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                    contentColor = MaterialTheme.colorScheme.onTertiaryContainer
                ) {

                }

                FABComponent(viewModel = viewModel,
                    isFabVisible = isFabVisible,
                    onFabTapped = {
                        wasFABTapped = true
                        isFabVisible = true // Keep the FAB visible after a tap

                        // ======== Scroll Down when FAB is tapped ==========
                        scope.launch {
                            if (logEntries.value.isNotEmpty()) {
                                logColumnListState.animateScrollToItem(logEntries.value.size - 1)
                            }
                            delay(100) // Ensure the new log entry is rendered
                            logColumnListState.animateScrollToItem(logEntries.value.size)
                        }
                    }
                )
        }
    ) {
        padding ->
        Column(modifier = Modifier.padding(padding)) {
            // Carousel: Add Horizontal Scrollable Row
//            CarouselComponent(
//                groupedLogDates = groupedLogDates,
//                selectedDate = selectedCarouselDate,
//                groupedLogs = groupedLogs,
//                carouselState = logRowCarouselState,
//                scope = scope,
//                listState = logColumnListState,
//                onDateSelected = { date ->
//                    selectedCarouselDate = date
//                }
//            )

            LazyColumn(
                state = logColumnListState,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            ) {
                groupedLogs.forEach { (date, logs) ->
                    stickyHeader {
                        ListStickyHeader(
                            date = date,
                            logs = logs,
                            index = groupedLogDates.indexOf(date),
                            onLongClick = {viewModel.toggleGroupedLogsSelection(logs)})
                    }
                    items(
                        items = logs,
                        key = { log -> log.id }  // Unique ID for each log entry
                    ) { log ->
                        SwipeToDeleteContainer(
                            item = log,
                            key = log.id,
                            onDelete = { viewModel.removeLogEntry(log) }
                        ) {
                            ListViewItem(
                                logEntry = it,
                                isSelected = log in selectedLogEntries,
                                onClick = {
                                    selectedLogToEdit = log // Set the current log to edit
                                    isBottomSheetOpen = !isBottomSheetOpen  // Toggle the bottom sheet
                                },
                                onLongClick = { viewModel.toggleLogSelection(log) }
                            )
                        }
                    }
                }

                // Add virtual space at the end of the list
                item {
                    Spacer(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(96.dp*2) // Height of FAB + extra padding for visibility
                    )
                }
            }
        }
    }

    // Display the bottom sheet if its open
    if (isBottomSheetOpen) {
        ModalBottomSheetComponent(
            isBottomSheetOpen = { isBottomSheetOpen = false },
            bottomSheetState = bottomSheetState,
            selectedLogToEdit = selectedLogToEdit,
            scope = scope,
            onLogUpdated = {updatedLog:LogEntry -> selectedLogToEdit = updatedLog
            viewModel.updateLogEntry(updatedLog)}
        )
    }

}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun CarouselComponent(
    groupedLogDates: List<String>,
    selectedDate: String,
    groupedLogs: Map<String, List<LogEntry>>,
    onDateSelected: (String) -> Unit,
    carouselState: LazyListState,
    scope: CoroutineScope,
    listState: LazyListState
) {
    LazyRow (
            state = carouselState,
            modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 0.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ){

        items(groupedLogDates) { date ->
            val isSelected = date == selectedDate
            val logs = groupedLogs[date] ?: emptyList()
            // Calculate total duration
            val totalDuration = logs.sumOf { log ->
                log.endTimestamp!! - log.startTimestamp
            }
            Column (
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .padding(0.dp)
                    .background(
                        color = if (isSelected) MaterialTheme.colorScheme.tertiaryContainer else MaterialTheme.colorScheme.surface,
                        shape = MaterialTheme.shapes.medium
                    )
                    .clickable { onDateSelected(date)
                        // Scroll LazyColumn to the tapped date
                        val columnIndex = groupedLogDates.indexOf(date)
                        if (columnIndex != -1) {
                            scope.launch {
                                listState.animateScrollToItem(columnIndex)
                            }
                        }

                    }
                    .padding(8.dp)
            )
            {
                Text(text = date.split(" ").get(1).take(1), style = MaterialTheme.typography.bodyMedium) // Month Initial
                Text(text = date.split(" ").get(2).take(2), style = MaterialTheme.typography.bodyLarge) // Day of Month

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(4.dp)
                ){
                    Icon(Icons.Filled.Flag, contentDescription = null, modifier = Modifier.size(12.dp)) // Flag icon
                    Text(text = "${logs.size}", style = MaterialTheme.typography.bodySmall) // Count of logs ${logs.size}
                }
            }
        }

    }

}


@OptIn(ExperimentalMaterialApi::class)
@Composable
fun <T> SwipeToDeleteContainer(
    item: T,
    onDelete: (T) -> Unit,
    key: Any,
    animationDuration: Int = 500,
    content: @Composable (T) -> Unit
) {
    var isRemoved by remember(key) {
        mutableStateOf(false)
    }

    val dismissState = rememberSwipeToDismissBoxState()


    AnimatedVisibility(
        visible = !isRemoved,
        exit = shrinkVertically(
            animationSpec = tween(durationMillis = animationDuration),
            shrinkTowards = Alignment.Top
        ) + fadeOut()
    ) {
        SwipeToDismissBox(
            state = dismissState,
            enableDismissFromEndToStart = true,
            enableDismissFromStartToEnd = false,
            backgroundContent = {
                val color by animateColorAsState(
                    when (dismissState.targetValue) {
                        SwipeToDismissBoxValue.Settled -> Color.LightGray // Default background
                        SwipeToDismissBoxValue.StartToEnd -> Color.Green  // Swipe right
                        SwipeToDismissBoxValue.EndToStart -> MaterialTheme.colorScheme.error // Swipe left
                    }
                )
                Box(
                    Modifier
                        .fillMaxSize()
                        .background(color)
                        .padding(16.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Delete,
                        contentDescription = "Delete",
                        modifier = Modifier.align(Alignment.CenterEnd),
                        tint = Color.White
                    )
                }
            },
        ) {
            // Render the log item content
            content(item)
        }
        // Check for dismissal in a LaunchedEffect
        LaunchedEffect(dismissState.currentValue) {
            if (dismissState.currentValue == SwipeToDismissBoxValue.EndToStart) {
                isRemoved = true // Trigger AnimatedVisibility exit
                delay(300) // Wait for the animation to complete
                onDelete(item) // Trigger the deletion logic
            }
        }
    }

}

// Grouping logs by date
// --- Added function to group logs by date ---
fun groupLogsByDate(logEntries: State<List<LogEntry>>): Map<String, List<LogEntry>> {
    val dateFormat = SimpleDateFormat("EEEE MMM dd, yyyy", Locale.getDefault())
    return logEntries.value.groupBy { logEntry -> dateFormat.format(Date(logEntry.startTimestamp)) }
}