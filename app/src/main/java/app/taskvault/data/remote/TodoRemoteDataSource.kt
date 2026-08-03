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
            snapshot.children.mapNotNull { it.getValue(Todo::class.java) }
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
