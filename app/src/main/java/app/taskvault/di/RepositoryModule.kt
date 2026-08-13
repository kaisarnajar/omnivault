package app.taskvault.di

import app.taskvault.data.repository.AuthRepositoryImpl
import app.taskvault.data.repository.ExpenseRepositoryImpl
import app.taskvault.data.repository.NoteRepositoryImpl
import app.taskvault.data.repository.PomodoroHistoryRepositoryImpl
import app.taskvault.data.repository.ProfileRepositoryImpl
import app.taskvault.data.repository.SecretRepository
import app.taskvault.data.repository.SecretRepositoryImpl
import app.taskvault.data.repository.TodoRepositoryImpl
import app.taskvault.domain.AuthRepository
import app.taskvault.domain.ExpenseRepository
import app.taskvault.domain.NoteRepository
import app.taskvault.domain.PomodoroHistoryRepository
import app.taskvault.domain.ProfileRepository
import app.taskvault.domain.TodoRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindAuthRepository(
        authRepositoryImpl: AuthRepositoryImpl
    ): AuthRepository

    @Binds
    @Singleton
    abstract fun bindProfileRepository(
        profileRepositoryImpl: ProfileRepositoryImpl
    ): ProfileRepository

    @Binds
    @Singleton
    abstract fun bindTodoRepository(
        todoRepositoryImpl: TodoRepositoryImpl
    ): TodoRepository

    @Binds
    @Singleton
    abstract fun bindPomodoroHistoryRepository(
        pomodoroHistoryRepositoryImpl: PomodoroHistoryRepositoryImpl
    ): PomodoroHistoryRepository

    @Binds
    @Singleton
    abstract fun bindNoteRepository(
        noteRepositoryImpl: NoteRepositoryImpl
    ): NoteRepository

    @Binds
    @Singleton
    abstract fun bindExpenseRepository(
        expenseRepositoryImpl: ExpenseRepositoryImpl
    ): ExpenseRepository

    @Binds
    @Singleton
    abstract fun bindSecretRepository(
        secretRepositoryImpl: SecretRepositoryImpl
    ): SecretRepository

    @Binds
    @Singleton
    abstract fun bindLedgerRepository(
        ledgerRepositoryImpl: app.taskvault.data.repository.LedgerRepositoryImpl
    ): app.taskvault.domain.LedgerRepository
}
