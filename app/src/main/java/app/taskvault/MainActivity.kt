package app.taskvault

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
        
        val repository = createTodoRepository()

        setContent {
            var isDarkTheme by remember { mutableStateOf<Boolean?>(null) }
            val systemTheme = androidx.compose.foundation.isSystemInDarkTheme()
            val currentTheme = isDarkTheme ?: systemTheme

            app.taskvault.ui.theme.TaskVaultTheme(darkTheme = currentTheme) {
                TaskVaultApp(
                    repository = repository,
                    currentTheme = isDarkTheme,
                    onThemeChange = { isDarkTheme = it }
                )
            }
        }
    }

    private fun createTodoRepository(): TodoRepositoryImpl {
        val database = Room.databaseBuilder(
            applicationContext,
            TodoDatabase::class.java,
            "todo_database"
        ).fallbackToDestructiveMigration().build()

        val firebaseDatabase = FirebaseDatabase.getInstance().apply {
            try { setPersistenceEnabled(true) } catch (e: Exception) {}
        }
        
        return TodoRepositoryImpl(database.todoDao, TodoRemoteDataSource(firebaseDatabase))
    }
}

@Composable
fun TaskVaultApp(
    repository: TodoRepositoryImpl,
    currentTheme: Boolean?,
    onThemeChange: (Boolean?) -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        val navController = rememberNavController()
        
        val viewModel: TodoViewModel = androidx.lifecycle.viewmodel.compose.viewModel(
            factory = object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return TodoViewModel(repository) as T
                }
            }
        )

        NavHost(navController = navController, startDestination = "todo_list") {
            composable("todo_list") {
                TodoListScreen(
                    viewModel = viewModel,
                    onNavigateToAddTodo = { navController.navigate("add_todo") },
                    currentTheme = currentTheme,
                    onThemeChange = onThemeChange
                )
            }
            composable("add_todo") {
                AddTodoScreen(
                    viewModel = viewModel,
                    onNavigateBack = { navController.popBackStack() }
                )
            }
        }
    }
}
