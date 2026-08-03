package app.taskvault.data.repository

import app.taskvault.data.local.TodoDao
import app.taskvault.data.local.toDomainModel
import app.taskvault.data.local.toEntityModel
import app.taskvault.data.remote.TodoRemoteDataSource
import app.taskvault.domain.Todo
import app.taskvault.domain.TodoRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.util.UUID

class TodoRepositoryImpl(
    private val todoDao: TodoDao,
    private val remoteDataSource: TodoRemoteDataSource
) : TodoRepository {

    private val scope = CoroutineScope(Dispatchers.IO)

    init {
        scope.launch {
            try {
                val remoteTodos = remoteDataSource.getTodos()
                todoDao.insertTodos(remoteTodos.map { it.toEntityModel() })
            } catch (e: Exception) {
            }
        }
    }

    override fun getTodos(): Flow<List<Todo>> {
        return todoDao.getTodos().map { entities ->
            entities.map { it.toDomainModel() }
        }
    }

    override suspend fun addTodo(title: String, description: String, dueDate: Long?, remindMe: Long?, priority: String) {
        val newTodo = Todo(
            id = UUID.randomUUID().toString(),
            title = title,
            description = description,
            isCompleted = false,
            timestamp = System.currentTimeMillis(),
            dueDate = dueDate,
            remindMe = remindMe,
            priority = priority
        )
        todoDao.insertTodo(newTodo.toEntityModel())
        try {
            remoteDataSource.addTodo(newTodo)
        } catch (e: Exception) {
        }
    }

    override suspend fun updateTodo(todo: Todo) {
        todoDao.updateTodo(todo.toEntityModel())
        try {
            remoteDataSource.updateTodo(todo)
        } catch (e: Exception) {
        }
    }

    override suspend fun deleteTodo(todoId: String) {
        todoDao.deleteTodoById(todoId)
        try {
            remoteDataSource.deleteTodo(todoId)
        } catch (e: Exception) {
        }
    }
}
