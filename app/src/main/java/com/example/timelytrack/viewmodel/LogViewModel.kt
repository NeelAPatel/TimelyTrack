package com.example.timelytrack.viewmodel

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import com.example.timelytrack.model.LogEntry

class LogViewModel : ViewModel() {
    var logEntries = mutableStateListOf<LogEntry>()
        private set

    fun addLogEntry() {
        logEntries.add(LogEntry(startTimestamp = System.currentTimeMillis()))
    }

    fun completeLastLogEntry() {
        logEntries.lastOrNull()?.let { lastLog ->
            lastLog.endTimestamp = System.currentTimeMillis()
            logEntries[logEntries.lastIndex] = lastLog
        }
    }
}
