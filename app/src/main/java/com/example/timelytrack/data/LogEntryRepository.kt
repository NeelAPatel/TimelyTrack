package com.example.timelytrack.data

import com.example.timelytrack.model.LogEntry
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow


class LogEntryRepository (private val logEntryDao : LogEntryDao) {
    private val coroutineScope = CoroutineScope(Dispatchers.Default)
    val allItems: StateFlow<List<LogEntry>> = logEntryDao.getAllLogEntries().stateIn(
        scope = coroutineScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    fun getAll() = logEntryDao.getAllLogEntries()

    suspend fun insertLogEntry(logEntry: LogEntry) {
        logEntryDao.insertLogEntry(logEntry)
    }

    suspend fun deleteLogEntry(logEntry: LogEntry) {
        logEntryDao.deleteLogEntry(logEntry)
    }

    suspend fun updateLogEntry(logEntry: LogEntry) {
        logEntryDao.updateLogEntry(logEntry)
    }

    suspend fun getLogEntryById(id: Long): Flow<LogEntry>? {
        return logEntryDao.getLogEntryById(id)
    }

    suspend fun getAllLogEntries(): Flow<List<LogEntry>> {
        return logEntryDao.getAllLogEntries()
    }
}