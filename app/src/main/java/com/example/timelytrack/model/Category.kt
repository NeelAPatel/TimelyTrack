package com.example.timelytrack.model

import java.util.UUID

// This stores groups of logs at the high level.
// Each category = separate

data class Category(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val logs: List<LogEntry> = listOf()
)