import android.content.Context
import android.util.Log
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.timelytrack.data.LogEntryDao
import com.example.timelytrack.model.LogEntry


// Sets up overall SQLite database for the app. DB name is timelytrack_database
@Database(entities = [LogEntry::class], version = 1, exportSchema = false)
//@TypeConverters(CustomFieldConverter::class) // Use if CustomField requires conversion
abstract class TimelyTrackDatabase : RoomDatabase() {

    abstract fun logEntryDao(): LogEntryDao

    companion object {
        @Volatile // Volatile ensures the value of INSTANCE is always up-to-date and the same for all execution threads
        private var INSTANCE: TimelyTrackDatabase? = null

        // Singleton to ensure only one instance of the database is created
        fun getDatabase(context: Context): TimelyTrackDatabase {
            Log.d("TimelyTrackDatabase", "Requesting database instance...")
            return INSTANCE ?: synchronized(this) {
                Log.d("TimelyTrackDatabase", "Database instance is null, creating new instance...")
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    TimelyTrackDatabase::class.java,
                    "timelytrack_database"
                )
                    .fallbackToDestructiveMigration() // Optional: for simple migration handling
                    .build()
                INSTANCE = instance // Set the instance to the newly created database
                Log.d("TimelyTrackDatabase", "New database instance created and set to INSTANCE.")

                instance
            }
        }
    }
}

