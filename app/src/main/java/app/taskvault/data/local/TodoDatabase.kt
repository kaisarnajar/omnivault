package app.taskvault.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(entities = [TodoEntity::class], version = 3, exportSchema = false)
@TypeConverters(StringListConverter::class)
abstract class TodoDatabase : RoomDatabase() {
    abstract val todoDao: TodoDao
}
