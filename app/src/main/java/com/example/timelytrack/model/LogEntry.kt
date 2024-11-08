package com.example.timelytrack.model

import java.util.UUID

data class LogEntry(
    val id: String = UUID.randomUUID().toString(),
    val startTimestamp: Long,
    var endTimestamp: Long ?= null, // nullable for incomplete logs
//     var logCategory: String, // Which tracker is it part of
//     var activity: String // Which activity is it part of

)