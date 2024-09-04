package com.example.timelytrack.ui.home

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FabPosition
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.timelytrack.viewmodel.LogViewModel


@ExperimentalFoundationApi
@Composable
fun HomeScreen(viewModel: LogViewModel) {
    Scaffold(
        floatingActionButtonPosition = FabPosition.End,
        floatingActionButton = {
            Column(
                    horizontalAlignment = Alignment.End,
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.padding(end = 16.dp, bottom = 16.dp)
                ) {

                val context = LocalContext.current
                val scope = rememberCoroutineScope()


                SmallFloatingActionButton(
                    onClick = { viewModel.completeLastLogEntry() },
                    containerColor = MaterialTheme.colorScheme.onPrimary,
                    contentColor = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(48.dp)
//                        .combinedClickable(
//                            onClick = { viewModel.addLogEntry() },
//                            onLongClick = {
//                                scope.launch {
//                                    Toast.makeText(context, "Complete last ongoing log", Toast.LENGTH_SHORT).show()
//                                }
//                            }
//                        )
                ) {
                    Icon(Icons.Filled.Check, contentDescription = "Complete current log")
                }

                Spacer(modifier = Modifier.height(16.dp))

                ExtendedFloatingActionButton(
                    onClick = { viewModel.addLogEntry() },
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                    icon = {  Icon(Icons.Filled.Flag, contentDescription = "New Log")},
                    text = { Text("New Log") }
                )
            }
        }
    ) {

        padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(viewModel.logEntries) { log ->
                LogEntryItem(log)
            }
        }
    }
}
