package app.taskvault.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(entities = [TodoEntity::class, NoteEntity::class, PomodoroSessionEntity::class, ExpenseEntity::class], version = 10, exportSchema = false)
abstract class TodoDatabase : RoomDatabase() {
    abstract val todoDao: TodoDao
    abstract val noteDao: NoteDao
    abstract val pomodoroDao: PomodoroDao
    abstract val expenseDao: ExpenseDao
}
