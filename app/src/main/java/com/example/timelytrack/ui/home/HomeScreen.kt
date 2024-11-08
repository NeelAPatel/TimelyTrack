@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class)

package com.example.timelytrack.ui.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.DismissDirection
//import androidx.compose.material.DismissDirection
import androidx.compose.material.DismissState
import androidx.compose.material.DismissValue
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.SwipeToDismiss
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.Icons
import androidx.compose.material.rememberDismissState
//import androidx.compose.material.rememberDismissState
//import androidx.compose.material.SwipeToDismiss
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FabPosition
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
//import androidx.compose.material3.rememberDismissState
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.timelytrack.model.LogEntry
import com.example.timelytrack.viewmodel.LogViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlinx.coroutines.delay


@OptIn(ExperimentalFoundationApi::class)
@Preview(showBackground = true)
@Composable
fun FABComponentPreview() {
    val viewModel = LogViewModel()
    FABComponent(viewModel = viewModel)
}

@OptIn(ExperimentalFoundationApi::class)
@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    val viewModel = LogViewModel()
    HomeScreen(viewModel)
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
        Text("Logs backend count: " + viewModel.logEntries.collectAsState().value.size.toString())

        ExtendedFloatingActionButton(
            onClick = { viewModel.addLogEntry() },
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary,
            icon = {  Icon(Icons.Filled.Flag, contentDescription = "New Log")},
            text = { Text("New Log") },
        )
    }
}


@ExperimentalFoundationApi
@Composable
fun HomeScreen(viewModel: LogViewModel) {
    // --- Added groupedLogs variable to group logs by date ---
    val groupedLogs = groupLogsByDate(viewModel.logEntries.collectAsState())


    Scaffold(
        floatingActionButtonPosition = FabPosition.End,
        floatingActionButton = {
            FABComponent(viewModel = viewModel)
        },

        ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // --- Updated to loop through each date group ---
            groupedLogs.forEach { (date, logs) ->
                // --- Added StickyHeader for each date group ---
                stickyHeader {
                    ListStickyHeader(date = date, logs = logs)
                }
                // --- Display logs for the current date group ---


                items(
                    items = logs,
                    key = { log -> log.id } // Assuming your LogEntry has an 'id' property
                ) { log ->
                    SwipeToDeleteContainer(
                        item = log,
                        key = log.id, // Pass the key to SwipeToDeleteContainer
                        onDelete = { viewModel.removeLogEntry(log) }
                    ) {
                        ListViewItem(logEntry = it)
                    }
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
    val state = rememberDismissState(
        confirmStateChange = { value ->
            if (value == DismissValue.DismissedToStart) {
                isRemoved = true
                true
            } else {
                false
            }
        }
    )

    LaunchedEffect(key1 = isRemoved) {
        if(isRemoved) {
            delay(animationDuration.toLong())
            onDelete(item)
        }
    }

    AnimatedVisibility(
        visible = !isRemoved,
        exit = shrinkVertically(
            animationSpec = tween(durationMillis = animationDuration),
            shrinkTowards = Alignment.Top
        ) + fadeOut()
    ) {
        SwipeToDismiss(
            state = state,
            background = {
                DeleteBackground(swipeDismissState = state)
            },
            dismissContent = { content(item) },
            directions = setOf(DismissDirection.EndToStart)
        )
    }
}





@OptIn(ExperimentalMaterialApi::class)
@Composable
fun DeleteBackground(
    swipeDismissState: DismissState
){
    val color = if (swipeDismissState.dismissDirection == DismissDirection.EndToStart) {
        MaterialTheme.colorScheme.error
    } else Color.Transparent

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(color)
            .padding(16.dp),

        contentAlignment = Alignment.CenterEnd
    ) {
        Icon(
            imageVector = Icons.Default.Delete,
            contentDescription = null,
            tint = Color.White
        )
    }
}
//ef665d
// Grouping logs by date
// --- Added function to group logs by date ---
fun groupLogsByDate(logEntries: State<List<LogEntry>>): Map<String, List<LogEntry>> {
    val dateFormat = SimpleDateFormat("EEEE MMM dd, yyyy", Locale.getDefault())
    return logEntries.value.groupBy { logEntry -> dateFormat.format(Date(logEntry.startTimestamp))
    }
}