package com.example.timelytrack.ui.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SheetState
import androidx.compose.material3.SheetValue
import androidx.compose.material3.SheetValue.Hidden
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.rememberStandardBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.timelytrack.model.LogEntry
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true)
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
        onCloseBottomSheet = { /* Do nothing for preview */ },
        bottomSheetState = rememberStandardBottomSheetState(
            initialValue = SheetValue.Expanded
        ),
        selectedLog = mockLogEntry,
        scope = scope
    )

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ModalBottomSheetComponent(
    onCloseBottomSheet: () -> Unit,
    bottomSheetState: SheetState,
    selectedLog: LogEntry?,
    scope: CoroutineScope
) {
    ModalBottomSheet(
        onDismissRequest = { onCloseBottomSheet() },
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
                                onCloseBottomSheet()
                            }
                        }
                }
            ) {
                Text("Hide Bottom Sheet")
                Text("Selected Log: ${selectedLog?.id}", Modifier.clickable(onClick = {}))
            }
        }
        var text by remember { mutableStateOf("") }
        Text("Selected Log: ${selectedLog?.id}", Modifier.clickable(onClick = {}))
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