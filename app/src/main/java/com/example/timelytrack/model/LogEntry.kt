package com.example.timelytrack.model

data class LogEntry(
    val startTimestamp: Long,
    var endTimestamp: Long ?= null, // nullable for incomplete logs
//     var logCategory: String, // Which tracker is it part of
//     var activity: String // Which activity is it part of

)