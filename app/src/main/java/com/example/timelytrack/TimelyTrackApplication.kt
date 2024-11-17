package com.example.timelytrack

import com.example.timelytrack.data.LogEntryDatabase
import android.app.Application
import android.os.Handler
import android.os.Looper
import android.util.Log
import com.example.timelytrack.data.LogEntryContainer
import com.example.timelytrack.data.LogEntryRepository


class TimelyTrackApplication : Application() {
    lateinit var container: LogEntryContainer

    override fun onCreate() {
        super.onCreate()
        container = LogEntryContainer(this)
    }
}