package app.taskvault.widget

import app.taskvault.domain.AuthRepository
import app.taskvault.domain.ExpenseRepository
import app.taskvault.domain.NoteRepository
import app.taskvault.domain.PomodoroHistoryRepository
import app.taskvault.domain.TodoRepository
import app.taskvault.data.repository.SecretRepository
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@EntryPoint
@InstallIn(SingletonComponent::class)
interface WidgetEntryPoint {
    fun todoRepository(): TodoRepository
    fun noteRepository(): NoteRepository
    fun pomodoroHistoryRepository(): PomodoroHistoryRepository
    fun expenseRepository(): ExpenseRepository
    fun authRepository(): AuthRepository
}
