package com.example.timelytrack.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.timelytrack.model.LogEntry

// Interface for interacting with SQLite database, timelytrack_database, by using these functions to manipulate log entry data in and out of DB

@Dao
interface LogEntryDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(logEntry: LogEntry)

    @Update //(onConflict = OnConflictStrategy.REPLACE)
    suspend fun update(logEntry: LogEntry)

    @Delete //(onConflict = OnConflictStrategy.REPLACE)
    suspend fun delete(logEntry: LogEntry)

    @Query("SELECT * FROM log_entries WHERE id = :id")
    suspend fun getLogEntryById(id: Long): LogEntry?

    @Query("SELECT * FROM log_entries")
    suspend fun getAllLogEntries(): List<LogEntry>


}