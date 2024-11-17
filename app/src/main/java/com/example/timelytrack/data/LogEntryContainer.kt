package com.example.timelytrack.data

import com.example.timelytrack.data.LogEntryDatabase
import android.content.Context

class LogEntryContainer(private val context: Context) {
    val logEntryRepository: LogEntryRepository by lazy {
        LogEntryRepository(LogEntryDatabase.getLogEntryDatabase(context).logEntryDao())
    }
}