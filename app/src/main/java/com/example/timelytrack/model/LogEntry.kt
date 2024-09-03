package com.example.timelytrack.model

data class LogEntry(
    val startTimestamp: Long,
    var endTimestamp: Long ?= null // nullable for incomplete logs
//    var logCategorization: String

)