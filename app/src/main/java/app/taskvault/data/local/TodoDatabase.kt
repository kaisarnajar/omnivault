package app.taskvault.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(entities = [TodoEntity::class, NoteEntity::class], version = 8, exportSchema = false)
abstract class TodoDatabase : RoomDatabase() {
    abstract val todoDao: TodoDao
    abstract val noteDao: NoteDao
}
