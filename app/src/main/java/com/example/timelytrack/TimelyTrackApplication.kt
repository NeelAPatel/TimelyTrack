package com.example.timelytrack

import android.app.Application
import com.example.timelytrack.data.LogEntryContainer

class TimelyTrackApplication : Application() {
    lateinit var container: LogEntryContainer

    override fun onCreate() {
        super.onCreate()

        // Initialize Dependency Injections, Database, Network client here
        container = LogEntryContainer(this) // Database initialization ; could be consolidated into this file directly
    }
}