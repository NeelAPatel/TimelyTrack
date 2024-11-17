package com.example.timelytrack.viewmodel

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import com.example.timelytrack.model.LogEntry
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import androidx.lifecycle.viewModelScope
import com.example.timelytrack.data.LogEntryDao
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.UUID


class LogViewModel : ViewModel() {

    // variables
    private val _logEntries = MutableStateFlow<List<LogEntry>>(emptyList()) //App will modify Private variable
    val logEntries: StateFlow<List<LogEntry>> = _logEntries // expose public variable and functions

    fun addLogEntry() {
        // Create a new log entry and add it to the list

        val newEntry = LogEntry(categoryId = "1", startTimestamp = System.currentTimeMillis())
        _logEntries.update { currentEntries -> currentEntries  + newEntry }
    }

    fun completeLastLogEntry() {
        // Find the last log entry and update its endTimestamp
        // this is used by the Checkmark button

        _logEntries.value.lastOrNull()?.let { lastLog ->
            val updatedLog = lastLog.copy(
                endTimestamp = System.currentTimeMillis()
            )
            _logEntries.update { currentEntries ->
                currentEntries.dropLast(1) + updatedLog
            }
        }
    }

    fun removeLogEntry(logEntry: LogEntry) {
        // Remove a log entry from the list
        _logEntries.update { currentEntries -> currentEntries.filterNot { it == logEntry } }
    }


    fun clearLogEntries() {
        // Clear all log entries from the list
        _logEntries.value = emptyList()
    }

//    //=== Retrieves a log entry by ID
//    fun getLogEntryById(id: String): LogEntry? {
//        return _logEntries.value.find { it.id == id }
//    }


    //=== Removes

    // Remove 1 log
//    fun removeSelectedLogEntry() {
//        logEntries.removeLastOrNull()
//    }

    // Remove multiple selected logs
//    fun removeSelectedLogEntries() {
//        logEntries.clear()
//    }

    // Clear all logs
//    fun clearAllLogs() {
//        logEntries.clear()
//    }

    //=== Edits

    // Edit log entry
//    fun editLogEntry(index: Int, newLogEntry: LogEntry) {
//        logEntries[index] = newLogEntry
//    }

    // Edit log entry category
//    fun editLogEntryCategory(index: Int, newCategory: String) {
//        logEntries[index] = logEntries[index].copy(logCategorization = newCategory)
//    }

    // Edit log entry start time
//    fun editLogEntryStartTime(index: Int, newStartTime: Long) {
//        logEntries[index] = logEntries[index].copy(startTimestamp = newStartTime)
//    }

    // Edit log entry end time
//    fun editLogEntryEndTime(index: Int, newEndTime: Long) {
//        logEntries[index] = logEntries[index].copy(endTimestamp = newEndTime)
//    }

    // Edit log entry activity
//    fun editLogEntryActivity(index: Int, newActivity: String) {
//        logEntries[index] = logEntries[index].copy(logActivity = newActivity)
//    }




}
