package app.taskvault.data.remote

import app.taskvault.domain.Todo
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.tasks.await

class TodoRemoteDataSource(
    private val firebaseDatabase: FirebaseDatabase
) {
    private val todosRef = firebaseDatabase.getReference("todos")

    suspend fun getTodos(): List<Todo> {
        return try {
            val snapshot = todosRef.get().await()
            val todos = mutableListOf<Todo>()
            for (child in snapshot.children) {
                val id = child.child("id").getValue(String::class.java) ?: continue
                val title = child.child("title").getValue(String::class.java) ?: ""
                val description = child.child("description").getValue(String::class.java) ?: ""
                val isCompleted = child.child("isCompleted").getValue(Boolean::class.java) ?: false
                val timestamp = child.child("timestamp").getValue(Long::class.java) ?: 0L

                todos.add(Todo(id, title, description, isCompleted, timestamp))
            }
            todos
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun addTodo(todo: Todo) {
        todosRef.child(todo.id).setValue(todo).await()
    }

    suspend fun updateTodo(todo: Todo) {
        todosRef.child(todo.id).setValue(todo).await()
    }

    suspend fun deleteTodo(todoId: String) {
        todosRef.child(todoId).removeValue().await()
    }
}
