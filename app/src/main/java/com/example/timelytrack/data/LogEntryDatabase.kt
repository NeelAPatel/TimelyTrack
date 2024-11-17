package com.example.timelytrack.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.timelytrack.model.LogEntry

@Database(entities = [LogEntry::class], version = 1, exportSchema = false)
abstract class LogEntryDatabase: RoomDatabase() {
    abstract fun logEntryDao(): LogEntryDao

    companion object {
        @Volatile
        private var Instance: LogEntryDatabase? = null

        fun getLogEntryDatabase(context: Context): LogEntryDatabase {
            return Instance ?: synchronized(this) {
                Room.databaseBuilder(
                    context = context,
                    klass = LogEntryDatabase::class.java,
                    name = "sample"
                )
                    .build()
                    .also { Instance = it }
            }
        }
    }
}


