package app.taskvault

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.room.Room
import app.taskvault.data.local.TodoDatabase
import app.taskvault.data.remote.TodoRemoteDataSource
import app.taskvault.data.repository.TodoRepositoryImpl
import app.taskvault.ui.todos.AddTodoScreen
import app.taskvault.ui.todos.TodoListScreen
import app.taskvault.ui.todos.TodoViewModel
import com.google.firebase.database.FirebaseDatabase

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Manual Dependency Injection
        val database = Room.databaseBuilder(
            applicationContext,
            TodoDatabase::class.java,
            "todo_database"
        ).build()

        val firebaseDatabase = FirebaseDatabase.getInstance()
        // Enable offline persistence for Firebase
        try {
            firebaseDatabase.setPersistenceEnabled(true)
        } catch (e: Exception) {
            // Might throw if already initialized
        }
        
        val remoteDataSource = TodoRemoteDataSource(firebaseDatabase)
        val repository = TodoRepositoryImpl(database.todoDao, remoteDataSource)
        
        val viewModelFactory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                if (modelClass.isAssignableFrom(TodoViewModel::class.java)) {
                    @Suppress("UNCHECKED_CAST")
                    return TodoViewModel(repository) as T
                }
                throw IllegalArgumentException("Unknown ViewModel class")
            }
        }

        setContent {
            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    val viewModel = androidx.lifecycle.viewmodel.compose.viewModel<TodoViewModel>(
                        factory = viewModelFactory
                    )

                    NavHost(navController = navController, startDestination = "todo_list") {
                        composable("todo_list") {
                            TodoListScreen(
                                viewModel = viewModel,
                                onNavigateToAddTodo = {
                                    navController.navigate("add_todo")
                                }
                            )
                        }
                        composable("add_todo") {
                            AddTodoScreen(
                                viewModel = viewModel,
                                onNavigateBack = {
                                    navController.popBackStack()
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}
