package app.taskvault.ui.todos

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.taskvault.domain.Todo
import app.taskvault.domain.TodoRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class TodoViewModel(
    private val repository: TodoRepository,
    private val authRepository: app.taskvault.domain.AuthRepository,
) : ViewModel() {
    private val _todos = MutableStateFlow<List<Todo>>(emptyList())
    val todos: StateFlow<List<Todo>> = _todos.asStateFlow()

    private val _selectedTodo = MutableStateFlow<Todo?>(null)
    val selectedTodo: StateFlow<Todo?> = _selectedTodo.asStateFlow()

    init {
        viewModelScope.launch {
            repository.getTodos().collect { todoList ->
                _todos.value = todoList
            }
        }
    }

    fun addTodo(
        title: String,
        description: String,
        dueDate: Long?,
        remindMe: Long?,
        priority: String,
    ) {
        viewModelScope.launch {
            repository.addTodo(title, description, dueDate, remindMe, priority)
        }
    }

    fun selectTodoForEdit(todo: Todo?) {
        _selectedTodo.value = todo
    }

    fun updateTodoDetail(
        id: String,
        title: String,
        description: String,
        dueDate: Long?,
        remindMe: Long?,
        priority: String,
    ) {
        viewModelScope.launch {
            val currentTodo = _todos.value.find { it.id == id }
            if (currentTodo != null) {
                repository.updateTodo(
                    currentTodo.copy(
                        title = title,
                        description = description,
                        dueDate = dueDate,
                        remindMe = remindMe,
                        priority = priority,
                    ),
                )
            }
        }
    }

    fun toggleTodoCompletion(todo: Todo) {
        viewModelScope.launch {
            repository.updateTodo(todo.copy(isCompleted = !todo.isCompleted))
        }
    }

    fun deleteTodo(todoId: String) {
        viewModelScope.launch {
            repository.deleteTodo(todoId)
        }
    }

    fun logout() {
        viewModelScope.launch {
            authRepository.logout()
        }
    }
}
