package com.example.timelytrack.data

import android.content.Context

class LogEntryContainer(private val context: Context) {
    // Simple form of dependency injection
    // Single point of access for repo, ensures only one instance of repo is created
    val logEntryRepository: LogEntryRepository by lazy {
        LogEntryRepository(LogEntryDatabase.getLogEntryDatabase(context).logEntryDao())
    }
}