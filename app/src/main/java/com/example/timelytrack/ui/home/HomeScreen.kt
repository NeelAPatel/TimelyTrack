@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class)

package com.example.timelytrack.ui.home


import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
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
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.distinctUntilChanged


@OptIn(ExperimentalMaterial3Api::class)
@ExperimentalFoundationApi
@Composable
fun HomeScreen() {
    // Fetch the entries as the base data for the screen
    val viewModel: LogViewModel = viewModel(factory = LogViewModel.Factory)
    val logEntries = viewModel.allLogEntries.collectAsState()

    //Group logs by date
    val groupedLogs = groupLogsByDate(logEntries)

    // Variables for multi-selection ## NOT WORKING
    val selectedLogEntries by viewModel.selectedLogEntries.collectAsState()

    // Variables for auto-scroll position ## NOT WORKING
    val listState = rememberLazyListState() // Used to track scrolling
    val scrollToBottom by viewModel.scrollToBottom.collectAsState()
    var isFabVisible by remember { mutableStateOf(true) }
    var wasFABTapped by remember { mutableStateOf(false) } // Track if EFAB was tapped

    //Variables for editing a single log entry with BottomSheet
    // State for managing the bottom sheet and selected log
    var selectedLogToEdit by remember { mutableStateOf<LogEntry?>(null)}
    var isBottomSheetOpen by rememberSaveable { mutableStateOf(false) }  // Open or close toggle
    val bottomSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = false) // remember the state of bottom sheet, opened or closed
    val scope = rememberCoroutineScope()


    // Observe scroll state to toggle FAB visibility
    LaunchedEffect(listState) {
        snapshotFlow { listState.layoutInfo }
            .collect { layoutInfo ->
                val lastIndex = logEntries.value.size - 1
                val isLastItemVisible = layoutInfo.visibleItemsInfo.any { it.index == lastIndex }

                if (!wasFABTapped) {
                    isFabVisible = isLastItemVisible
                } else {
                    // Reset `wasFABTapped` when scrolling away from the last item
                    val isScrolledAway = layoutInfo.visibleItemsInfo.none { it.index == lastIndex }
                    if (isScrolledAway) {
                        wasFABTapped = false
                    }
                }
            }
    }

    Scaffold(
        floatingActionButtonPosition = FabPosition.End,
        floatingActionButton = {
                FABComponent(viewModel = viewModel,
                    isFabVisible = isFabVisible,
                    onFabTapped = {
                        wasFABTapped = true
                        isFabVisible = true // Keep the FAB visible after a tap
                    },
//                    onScrollDetected = { wasFABTapped = false } // Reset flag on scroll)
                )
        }
    ) {
        padding ->

        LazyColumn(
            state = listState,
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            groupedLogs.forEach { (date, logs) ->
                stickyHeader {
                    ListStickyHeader(
                        date = date,
                        logs = logs,
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
    return logEntries.value.groupBy { logEntry -> dateFormat.format(Date(logEntry.startTimestamp))
    }
}