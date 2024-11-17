package com.example.timelytrack.model

import android.media.metrics.LogSessionId
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

// Primary information for the app
// Need to implement custom fields down the road

@Entity(tableName = "log_entries")
data class LogEntry(
    @PrimaryKey (autoGenerate = true)
    val id: Int = 0,
//    val id: String = UUID.randomUUID().toString(), // UUID is a unique identifier for each log entry


    @ColumnInfo(name = "category_id")
    val categoryId: String, // Reference to the category to which this log entry belongs

    // Usual fields
    @ColumnInfo(name = "start_timestamp")
    val startTimestamp: Long,

    @ColumnInfo(name = "end_timestamp")
    var endTimestamp: Long ?= null, // nullable for incomplete logs

)


/**
 * id = unique id
 * start time
 * end time
 *
 * array of custom fields
 */