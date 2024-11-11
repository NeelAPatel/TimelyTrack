import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.timelytrack.data.LogEntryDao
import com.example.timelytrack.model.LogEntry


// Sets up overall SQLite database for the app. DB name is timelytrack_database
@Database(entities = [LogEntry::class], version = 1)
//@TypeConverters(CustomFieldConverter::class) // Use if CustomField requires conversion
abstract class AppDatabase : RoomDatabase() {

    abstract fun logEntryDao(): LogEntryDao

    companion object {
        @Volatile // Volatile ensures the value of INSTANCE is always up-to-date and the same for all execution threads
        private var INSTANCE: AppDatabase? = null

        // Singleton to ensure only one instance of the database is created
        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "timelytrack_database"
                )
                    .fallbackToDestructiveMigration() // Optional: for simple migration handling
                    .build()
                INSTANCE = instance // Set the instance to the newly created database
                instance
            }
        }
    }
}
