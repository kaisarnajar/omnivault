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
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import java.util.UUID

class TodoRepositoryImpl(
    private val todoDao: TodoDao,
    private val remoteDataSource: TodoRemoteDataSource,
    private val authRepository: AuthRepository,
    private val alarmScheduler: app.taskvault.worker.AlarmScheduler,
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
                        } catch (e: Exception) {
                        }
                    }
                } else {
                    todoDao.clearTodos()
                }
            }
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun getTodos(): Flow<List<Todo>> {
        return authRepository.authState.flatMapLatest { state ->
            if (state is AuthState.Authenticated) {
                val userId = authRepository.getCurrentUserId() ?: return@flatMapLatest flowOf(emptyList())
                todoDao.getTodos(userId).map { entities ->
                    entities.map { it.toDomainModel() }
                }
            } else {
                flowOf(emptyList())
            }
        }
    }

    override suspend fun addTodo(
        title: String,
        description: String,
        dueDate: Long?,
        remindMe: Long?,
        priority: String,
        category: String,
        eisenhowerTag: String
    ) {
        val newTodo =
            Todo(
                id = UUID.randomUUID().toString(),
                userId = authRepository.getCurrentUserId() ?: "",
                title = title,
                description = description,
                isCompleted = false,
                timestamp = System.currentTimeMillis(),
                dueDate = dueDate,
                remindMe = remindMe,
                priority = priority,
                category = category,
                eisenhowerTag = eisenhowerTag
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
    
    override suspend fun clearCompletedTodos() {
        val userId = authRepository.getCurrentUserId() ?: return
    }

    override suspend fun seedSampleData() {
        val userId = authRepository.getCurrentUserId() ?: return
        val categories = listOf("Work", "Personal", "Health", "Study", "Finance", "Other")
        val priorities = listOf("High", "Medium", "Low")
        val eisenhowerTags = listOf("Do", "Schedule", "Delegate", "Delete")
        
        val randomTasks = mutableListOf<Todo>()
        val currentTime = System.currentTimeMillis()
        
        for (i in 1..50) {
            val isCompleted = Math.random() > 0.8
            val todo = Todo(
                id = UUID.randomUUID().toString(),
                userId = userId,
                title = "Sample Task $i",
                description = "This is a randomly generated sample task to test performance and scrolling.",
                isCompleted = isCompleted,
                timestamp = currentTime - (Math.random() * 86400000 * 10).toLong(), // Random timestamp in last 10 days
                dueDate = if (Math.random() > 0.5) currentTime + (Math.random() * 86400000 * 5).toLong() else null, // Random due date in next 5 days
                remindMe = null,
                priority = priorities.random(),
                category = categories.random(),
                eisenhowerTag = eisenhowerTags.random()
            )
            randomTasks.add(todo)
            todoDao.insertTodo(todo.toEntityModel())
        }
        
        // Batch push to remote (best effort via single adds)
        randomTasks.forEach { 
            try { remoteDataSource.addTodo(userId, it) } catch (e: Exception) {} 
        }
    }
}
