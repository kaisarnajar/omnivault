package app.taskvault.domain

import kotlinx.coroutines.flow.Flow

interface TodoRepository {
    fun getTodos(): Flow<List<Todo>>

    suspend fun addTodo(
        title: String,
        description: String,
        dueDate: Long?,
        remindMe: Long?,
        priority: String,
    )

    suspend fun updateTodo(todo: Todo)

    suspend fun deleteTodo(todoId: String)
}
