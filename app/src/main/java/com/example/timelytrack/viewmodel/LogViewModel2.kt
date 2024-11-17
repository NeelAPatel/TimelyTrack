package com.example.timelytrack.viewmodel

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.timelytrack.model.LogEntry
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import androidx.lifecycle.viewModelScope
import com.example.timelytrack.data.LogEntryDao
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.UUID


class LogViewModel2(private val logEntryDao: LogEntryDao) : ViewModel() {

    // variables
    private val _logEntries = MutableStateFlow<List<LogEntry>>(emptyList()) //App will modify Private variable
    val logEntries: StateFlow<List<LogEntry>> = _logEntries.asStateFlow() // expose public variable and functions

    init {
        // Collect entries from the database to keep _logEntries updated
        viewModelScope.launch {
            logEntryDao.getAllLogEntries().collect { entries ->
                _logEntries.value = entries
            }
        }
    }


    // Function to add a new log entry with the current start timestamp
    fun addLogEntry2(categoryId: String) {
        val newLogEntry = LogEntry(
            id = UUID.randomUUID().toString(),
            categoryId = categoryId,
            startTimestamp = System.currentTimeMillis()
        )
        viewModelScope.launch {
            logEntryDao.insert(newLogEntry)
        }
    }

    // Function to delete a specific log entry by its ID
    fun removeLogEntry2(logEntry: LogEntry) {
        viewModelScope.launch {
            logEntryDao.delete(logEntry)
        }
    }

    // Function to complete the latest log entry by setting the end timestamp
    fun completeLastLogEntry2() {
        viewModelScope.launch {
            // Get the latest log entry from the database
            val latestEntry = logEntryDao.getAllLogEntries().firstOrNull()?.lastOrNull()
            latestEntry?.let { entry ->
                // Update the entry's end timestamp to the current time
                entry.endTimestamp = System.currentTimeMillis()
                logEntryDao.update(entry)
            }
        }
    }


//    fun addLogEntry() {
//        // Create a new log entry and add it to the list
//
//        val newEntry = LogEntry(categoryId = "1", startTimestamp = System.currentTimeMillis())
//        _logEntries.update { currentEntries -> currentEntries  + newEntry }
//    }
//
//    fun completeLastLogEntry() {
//        // Find the last log entry and update its endTimestamp
//        // this is used by the Checkmark button
//
//        _logEntries.value.lastOrNull()?.let { lastLog ->
//            val updatedLog = lastLog.copy(
//                endTimestamp = System.currentTimeMillis()
//            )
//            _logEntries.update { currentEntries ->
//                currentEntries.dropLast(1) + updatedLog
//            }
//        }
//    }
//
//    fun removeLogEntry(logEntry: LogEntry) {
//        // Remove a log entry from the list
//        _logEntries.update { currentEntries -> currentEntries.filterNot { it == logEntry } }
//    }
//
//
//    fun clearLogEntries() {
//        // Clear all log entries from the list
//        _logEntries.value = emptyList()
//    }

//    //=== Retrieves a log entry by ID
//    fun getLogEntryById(id: String): LogEntry? {
//        return _logEntries.value.find { it.id == id }
//    }




}
