package com.example.timelytrack.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.timelytrack.model.LogEntry
import kotlinx.coroutines.flow.Flow

// Interface for interacting with SQLite database, timelytrack_database, by using these functions to manipulate log entry data in and out of DB

@Dao
interface LogEntryDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertLogEntry(logEntry: LogEntry)

    @Update //(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateLogEntry(logEntry: LogEntry)

    @Delete //(onConflict = OnConflictStrategy.REPLACE)
    suspend fun deleteLogEntry(logEntry: LogEntry)

    @Query("SELECT * FROM log_entries WHERE id = :id")
    fun getLogEntryById(id: Long): Flow<LogEntry>?

    @Query("SELECT * FROM log_entries")
    fun getAllLogEntries(): Flow<List<LogEntry>>

}