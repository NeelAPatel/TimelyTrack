package com.example.timelytrack.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.timelytrack.TimelyTrackApplication
import com.example.timelytrack.model.LogEntry
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import com.example.timelytrack.data.LogEntryRepository
import kotlinx.coroutines.flow.*


class LogViewModel(private val repository: LogEntryRepository) : ViewModel() {

    // variables
    val allLogEntries: StateFlow<List<LogEntry>> = repository.allItems

    // State for scrolling to the bottom when a new log is added
    private val _scrollToBottom = MutableStateFlow(false)
    val scrollToBottom: StateFlow<Boolean> = _scrollToBottom

    // State for selected log entries in multi-select mode
    private val _selectedLogEntries = MutableStateFlow<Set<LogEntry>>(emptySet())
    val selectedLogEntries: StateFlow<Set<LogEntry>> = _selectedLogEntries

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[APPLICATION_KEY] as TimelyTrackApplication)
                LogViewModel(application.container.logEntryRepository)
            }
        }
    }

    // Function to add a new log entry with the current start timestamp
    fun addLogEntry(categoryId: String) {
        val newLogEntry = LogEntry(
            categoryId = categoryId,
            startTimestamp = System.currentTimeMillis()
        )
        viewModelScope.launch {
            repository.insertLogEntry(newLogEntry)
        }
    }

    // Function to delete a specific log entry by its ID
    fun removeLogEntry(logEntry: LogEntry) {
        viewModelScope.launch {
            repository.deleteLogEntry(logEntry)
        }
    }

    // Function to complete the latest log entry by setting the end timestamp
    fun completeLastLogEntry() {
        viewModelScope.launch {
            // Get the latest log entry from the database
            val latestEntry = repository.getAllLogEntries().firstOrNull()?.lastOrNull()
            latestEntry?.let { entry ->
                // Update the entry's end timestamp to the current time
                entry.endTimestamp = System.currentTimeMillis()
                repository.updateLogEntry(entry)
            }
        }
    }

    // Clears the scroll-to-bottom event after the UI has handled it
    fun clearScrollToBottom() {
        _scrollToBottom.value = false
    }

    // Deletes a set of selected log entries
    fun removeLogEntries(entries: Set<LogEntry>) {
        viewModelScope.launch {
            entries.forEach { repository.deleteLogEntry(it) }
            _selectedLogEntries.value = emptySet()  // Clear selection after deletion
        }
    }

    // Toggles selection for a log entry (used for multi-select)
    fun toggleLogSelection(logEntry: LogEntry) {
        _selectedLogEntries.update { selectedEntries ->
            if (logEntry in selectedEntries) selectedEntries - logEntry else selectedEntries + logEntry
        }
    }

    // Selects all logs in a specific group
    fun selectAllLogsInGroup(groupLogs: List<LogEntry>) {
        _selectedLogEntries.update { selectedEntries ->
            selectedEntries + groupLogs
        }
    }
}


//class LogEntryViewModelFactory(private val repository: LogEntryRepository) : ViewModelProvider.Factory {
//    override fun <T : ViewModel> create(modelClass: Class<T>): T {
//        if (modelClass.isAssignableFrom(LogViewModel::class.java)) {
//            @Suppress("UNCHECKED_CAST")
//            return LogViewModel(repository) as T
//        }
//        throw IllegalArgumentException("Unknown ViewModel class")
//    }
//}
//



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

//
//    init {
//        // Collect entries from the database to keep _logEntries updated
//        viewModelScope.launch {
//            repository.getAllLogEntries().collect { entries ->
//                _logEntries.value = entries
//            }
//        }
//    }
