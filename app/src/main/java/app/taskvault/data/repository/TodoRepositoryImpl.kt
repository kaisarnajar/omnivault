package app.taskvault.data.repository

import app.taskvault.data.local.TodoDao
import app.taskvault.data.local.toDomainModel
import app.taskvault.data.local.toEntityModel
import app.taskvault.data.remote.TodoRemoteDataSource
import app.taskvault.domain.AuthRepository
import app.taskvault.domain.AuthState
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
    private val remoteDataSource: TodoRemoteDataSource,
    private val authRepository: AuthRepository,
    private val alarmScheduler: app.taskvault.worker.AlarmScheduler
) : TodoRepository {

    private val scope = CoroutineScope(Dispatchers.IO)

    init {
        scope.launch {
            authRepository.authState.collect { state ->
                if (state is AuthState.Authenticated) {
                    val userId = authRepository.getCurrentUserId()
                    if (userId != null) {
                        try {
                            val remoteTodos = remoteDataSource.getTodos(userId)
                            todoDao.insertTodos(remoteTodos.map { it.toEntityModel() })
                        } catch (e: Exception) { }
                    }
                } else {
                    todoDao.clearTodos()
                }
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
        
        if (remindMe != null) {
            alarmScheduler.scheduleAlarm(newTodo.id.hashCode(), newTodo.title, remindMe)
        }
        
        val userId = authRepository.getCurrentUserId()
        if (userId != null) {
            try {
                remoteDataSource.addTodo(userId, newTodo)
            } catch (e: Exception) {
            }
        }
    }

    override suspend fun updateTodo(todo: Todo) {
        todoDao.updateTodo(todo.toEntityModel())
        
        if (todo.remindMe != null) {
            alarmScheduler.scheduleAlarm(todo.id.hashCode(), todo.title, todo.remindMe)
        } else {
            alarmScheduler.cancelAlarm(todo.id.hashCode())
        }

        val userId = authRepository.getCurrentUserId()
        if (userId != null) {
            try {
                remoteDataSource.updateTodo(userId, todo)
            } catch (e: Exception) {
            }
        }
    }

    override suspend fun deleteTodo(todoId: String) {
        todoDao.deleteTodoById(todoId)
        alarmScheduler.cancelAlarm(todoId.hashCode())

        val userId = authRepository.getCurrentUserId()
        if (userId != null) {
            try {
                remoteDataSource.deleteTodo(userId, todoId)
            } catch (e: Exception) {
            }
        }
    }
}
