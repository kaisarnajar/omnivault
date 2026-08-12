package app.taskvault.data.remote

import app.taskvault.domain.Todo
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.tasks.await

import javax.inject.Inject

class TodoRemoteDataSource @Inject constructor(
    private val firebaseDatabase: FirebaseDatabase,
) {
    private fun getTodosRef(userId: String) = firebaseDatabase.getReference("users/$userId/todos")

    suspend fun getTodos(userId: String): List<Todo> {
        return try {
            val snapshot = getTodosRef(userId).get().await()
            snapshot.children.mapNotNull { it.getValue(Todo::class.java) }
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun addTodo(
        userId: String,
        todo: Todo,
    ) {
        getTodosRef(userId).child(todo.id).setValue(todo).await()
    }

    suspend fun updateTodo(
        userId: String,
        todo: Todo,
    ) {
        getTodosRef(userId).child(todo.id).setValue(todo).await()
    }

    suspend fun deleteTodo(
        userId: String,
        todoId: String,
    ) {
        getTodosRef(userId).child(todoId).removeValue().await()
    }
}
