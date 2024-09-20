package com.example.timelytrack.viewmodel

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import com.example.timelytrack.model.LogEntry

class LogViewModel : ViewModel() {
    var logEntries = mutableStateListOf<LogEntry>()
        private set


    //=== Adds
    fun addLogEntry() {
        logEntries.add(LogEntry(startTimestamp = System.currentTimeMillis()))
    }

    // Complete
    fun completeLastLogEntry() {
        logEntries.lastOrNull()?.let { lastLog ->

            val updatedLog = lastLog.copy(
                endTimestamp = System.currentTimeMillis()
            )
            logEntries[logEntries.lastIndex] = updatedLog
        }
    }


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
