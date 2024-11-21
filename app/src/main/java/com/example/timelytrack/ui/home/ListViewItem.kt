package com.example.timelytrack.ui.home

import android.annotation.SuppressLint
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.timelytrack.model.LogEntry
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

//@Preview(showBackground = true)
//@Composable
//fun ListViewItemPreview() {
//    val logEntry = LogEntry(id = 0, categoryId = 1.toString(), startTimestamp = 1679289600000, endTimestamp = 1679290200000)
//    ListViewItem(
//        logEntry = logEntry, combinedClickable = Modifier.Companion
//            .combinedClickable(
//                onClick = { tappedListItemId = it.id },
//                onLongClick = {
//                    haptics.performHapticFeedback(HapticFeedbackType.LongPress)
//                    contextMenuPhotoId = photo.id
//                },
//                onLongClickLabel = stringResource(R.string.open_context_menu)
//            )
//    )
//}

@OptIn(ExperimentalFoundationApi::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun ListViewItem(    logEntry: LogEntry,
                     isSelected: Boolean,
                     onLongClick: () -> Unit,
                     onClick: () -> Unit) {
    val dateFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())
    val backgroundColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer else Color.Transparent
    val borderWidth:BorderStroke = if (isSelected) BorderStroke(4.dp, MaterialTheme.colorScheme.primary) else BorderStroke(0.dp, Color.Transparent)

    // Calculate duration
    val duration = if (logEntry.endTimestamp != null) {
        val diffInMillis = logEntry.endTimestamp!! - logEntry.startTimestamp
        val minutes = diffInMillis / 1000 / 60
        "$minutes min"
    } else {
        "--"
    }

    Column (
        modifier = Modifier
            .background(backgroundColor)
            .border(borderWidth, shape = MaterialTheme.shapes.small )
            .combinedClickable(
                onClick = onClick,     // Single tap to edit
                onLongClick = onLongClick // Long press to toggle selection
            )
//            .padding(16.dp)
    ){
        ListItem(
            leadingContent = {
                Icon(
                    Icons.Filled.Schedule,
                    contentDescription = "Log Time Icon"
                )
            },
            headlineContent = {
                Text(text = dateFormat.format(Date(logEntry.startTimestamp)))
            },
            supportingContent = {
                logEntry.endTimestamp?.let {
                    Text("End Time: ${dateFormat.format(Date(it))}")
                } ?: Text("End Time: --")
            },
            trailingContent = {
                Text(text = duration)
            },
        )
//        HorizontalDivider()
    }
}
