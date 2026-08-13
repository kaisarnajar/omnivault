package app.taskvault.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [
        TodoEntity::class,
        NoteEntity::class,
        PomodoroSessionEntity::class,
        ExpenseEntity::class,
        SecretEntity::class,
        LedgerPersonEntity::class,
        LedgerTransactionEntity::class
    ],
    version = 12,
    exportSchema = false
)
abstract class TodoDatabase : RoomDatabase() {
    abstract val todoDao: TodoDao
    abstract val noteDao: NoteDao
    abstract val pomodoroDao: PomodoroDao
    abstract val expenseDao: ExpenseDao
    abstract val secretDao: SecretDao
    abstract val ledgerDao: LedgerDao
}
