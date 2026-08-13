package app.taskvault.di

import android.content.Context
import androidx.room.Room
import app.taskvault.data.local.ExpenseDao
import app.taskvault.data.local.NoteDao
import app.taskvault.data.local.PomodoroDao
import app.taskvault.data.local.TodoDao
import app.taskvault.data.local.TodoDatabase
import com.google.firebase.auth.FirebaseAuth
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideFirebaseAuth(): FirebaseAuth {
        return FirebaseAuth.getInstance()
    }

    @Provides
    @Singleton
    fun provideFirebaseDatabase(): com.google.firebase.database.FirebaseDatabase {
        return com.google.firebase.database.FirebaseDatabase.getInstance().apply {
            try {
                setPersistenceEnabled(true)
            } catch (e: Exception) {
            }
        }
    }

    @Provides
    @Singleton
    fun provideTodoDatabase(@ApplicationContext context: Context): TodoDatabase {
        return Room.databaseBuilder(
            context,
            TodoDatabase::class.java,
            "todo_database"
        )
            .fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    fun provideTodoDao(database: TodoDatabase): TodoDao = database.todoDao

    @Provides
    fun providePomodoroDao(database: TodoDatabase): PomodoroDao = database.pomodoroDao

    @Provides
    fun provideNoteDao(database: TodoDatabase): NoteDao = database.noteDao

    @Provides
    fun provideExpenseDao(database: TodoDatabase): ExpenseDao = database.expenseDao

    @Provides
    fun provideSecretDao(database: TodoDatabase): app.taskvault.data.local.SecretDao = database.secretDao

    @Provides
    fun provideLedgerDao(database: TodoDatabase): app.taskvault.data.local.LedgerDao = database.ledgerDao
}
