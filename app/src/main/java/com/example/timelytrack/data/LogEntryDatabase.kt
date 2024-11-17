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


//// Sets up overall SQLite database for the app. DB name is timelytrack_database
//@Database(entities = [LogEntry::class], version = 1, exportSchema = false)
////@TypeConverters(CustomFieldConverter::class) // Use if CustomField requires conversion
//abstract class LogEntryDatabase : RoomDatabase() {
//
//    abstract fun logEntryDao(): LogEntryDao
//
//    companion object {
//        @Volatile // Volatile ensures the value of INSTANCE is always up-to-date and the same for all execution threads
//        private var INSTANCE: LogEntryDatabase? = null
//
//        // Singleton to ensure only one instance of the database is created
//        fun getDatabase(context: Context): LogEntryDatabase {
////            Log.d("LogEntryDatabase", "Requesting database instance...")
//            return INSTANCE ?: synchronized(this) {
////                Log.d("LogEntryDatabase", "Database instance is null, creating new instance...")
//                val instance = Room.databaseBuilder(
//                    context.applicationContext,
//                    LogEntryDatabase::class.java,
//                    "timelytrack_database"
//                )
////                    .fallbackToDestructiveMigration() // Optional: for simple migration handling
//                    .build()
//                INSTANCE = instance // Set the instance to the newly created database
//                instance
////                Log.d("LogEntryDatabase", "New database instance created and set to INSTANCE.")
//            }
//        }
//    }
//}


