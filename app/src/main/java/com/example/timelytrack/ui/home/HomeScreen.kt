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
//noinspection UsingMaterialAndMaterial3Libraries
import androidx.compose.material.DismissDirection
//noinspection UsingMaterialAndMaterial3Libraries
//noinspection UsingMaterialAndMaterial3Libraries
import androidx.compose.material.ExperimentalMaterialApi
//noinspection UsingMaterialAndMaterial3Libraries
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Flag
//noinspection UsingMaterialAndMaterial3Libraries
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FabPosition
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.timelytrack.model.LogEntry
import com.example.timelytrack.viewmodel.LogViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import androidx.compose.material3.SwipeToDismissBoxValue
import kotlinx.coroutines.delay


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
    val scrollToBottom by viewModel.scrollToBottom.collectAsState()

    // Variables for auto-scroll position ## NOT WORKING
    val listState = rememberLazyListState()

    //Variables for editing a single log entry with BottomSheet
    // State for managing the bottom sheet and selected log
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var selectedLog by remember { mutableStateOf<LogEntry?>(null) }
    val coroutineScope = rememberCoroutineScope()


    // Scroll Position launch effect
    LaunchedEffect(scrollToBottom, logEntries) {
    //  println("Triggered scrollToBottom with logEntries.size = ${logEntries.value.size}")
        if (scrollToBottom) {
            listState.animateScrollToItem(logEntries.value.size - 1)
            println("Scrolled to bottom.")
            viewModel.clearScrollToBottom()
        }
    }

    Scaffold(
        floatingActionButtonPosition = FabPosition.End,
        floatingActionButton = { FABComponent(viewModel = viewModel) },
    ) {
        padding ->

        LazyColumn(
            state = listState,
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // --- Updated to loop through each date group ---
            // Display grouped logs with headers
            groupedLogs.forEach { (date, logs) ->
                stickyHeader {
                    ListStickyHeader(date = date, logs = logs, onSelectAll = {viewModel.selectAllLogsInGroup(logs)})
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
                            onSelect = { viewModel.toggleLogSelection(log) },
                            onEdit = { /* Show edit bottom sheet */ },
                        )
                    }
                }
            }
        }
    }
}

@ExperimentalFoundationApi
@Composable
fun FABComponent(viewModel: LogViewModel){
    Column(
        horizontalAlignment = Alignment.End,
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.padding(end = 16.dp, bottom = 16.dp)
    ) {

        SmallFloatingActionButton(
            onClick = { viewModel.completeLastLogEntry() },
            containerColor = MaterialTheme.colorScheme.onPrimary,
            contentColor = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(48.dp)
        ) {
            Icon(Icons.Filled.Check, contentDescription = "Complete current log")
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Debug text for the number of log entries
        Text("Logs backend count: " + viewModel.allLogEntries.collectAsState().value.size.toString())

        ExtendedFloatingActionButton(
            onClick = { viewModel.addLogEntry("1") },
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary,
            icon = {  Icon(Icons.Filled.Flag, contentDescription = "New Log")},
            text = { Text("New Log") },
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
                )
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