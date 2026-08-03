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
import app.taskvault.data.repository.AuthRepositoryImpl
import app.taskvault.data.repository.ProfileRepositoryImpl
import app.taskvault.data.repository.TodoRepositoryImpl
import app.taskvault.ui.auth.AuthViewModel
import app.taskvault.ui.auth.LoginScreen
import app.taskvault.ui.auth.RegisterScreen
import app.taskvault.ui.profile.ProfileScreen
import app.taskvault.ui.profile.ProfileViewModel
import app.taskvault.ui.todos.AddTodoScreen
import app.taskvault.ui.todos.TodoListScreen
import app.taskvault.ui.todos.TodoViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        val authRepository = AuthRepositoryImpl(FirebaseAuth.getInstance())
        val profileRepository = ProfileRepositoryImpl(FirebaseAuth.getInstance())
        val repository = createTodoRepository(authRepository)

        setContent {
            var isDarkTheme by remember { mutableStateOf<Boolean?>(null) }
            val systemTheme = androidx.compose.foundation.isSystemInDarkTheme()
            val currentTheme = isDarkTheme ?: systemTheme

            app.taskvault.ui.theme.TaskVaultTheme(darkTheme = currentTheme) {
                TaskVaultApp(
                    repository = repository,
                    authRepository = authRepository,
                    profileRepository = profileRepository,
                    currentTheme = isDarkTheme,
                    onThemeChange = { isDarkTheme = it }
                )
            }
        }
    }

    private fun createTodoRepository(authRepository: AuthRepositoryImpl): TodoRepositoryImpl {
        val database = Room.databaseBuilder(
            applicationContext,
            TodoDatabase::class.java,
            "todo_database"
        ).fallbackToDestructiveMigration().build()

        val firebaseDatabase = FirebaseDatabase.getInstance().apply {
            try { setPersistenceEnabled(true) } catch (e: Exception) {}
        }
        
        val alarmScheduler = app.taskvault.worker.AlarmScheduler(applicationContext)
        return TodoRepositoryImpl(database.todoDao, TodoRemoteDataSource(firebaseDatabase), authRepository, alarmScheduler)
    }
}

@Composable
fun TaskVaultApp(
    repository: TodoRepositoryImpl,
    authRepository: AuthRepositoryImpl,
    profileRepository: ProfileRepositoryImpl,
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
                    return TodoViewModel(repository, authRepository) as T
                }
            }
        )

        val authViewModel: AuthViewModel = androidx.lifecycle.viewmodel.compose.viewModel(
            factory = object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return AuthViewModel(authRepository) as T
                }
            }
        )
        
        val profileViewModel: ProfileViewModel = androidx.lifecycle.viewmodel.compose.viewModel(
            factory = object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return ProfileViewModel(profileRepository) as T
                }
            }
        )

        NavHost(
            navController = navController, 
            startDestination = if (FirebaseAuth.getInstance().currentUser != null) "todo_list" else "login"
        ) {
            composable("login") {
                LoginScreen(
                    viewModel = authViewModel,
                    onNavigateToRegister = { navController.navigate("register") },
                    onLoginSuccess = { 
                        navController.navigate("todo_list") {
                            popUpTo("login") { inclusive = true }
                        }
                    }
                )
            }
            composable("register") {
                RegisterScreen(
                    viewModel = authViewModel,
                    onNavigateBack = { navController.popBackStack() },
                    onRegisterSuccess = { 
                        navController.navigate("todo_list") {
                            popUpTo("login") { inclusive = true }
                        }
                    }
                )
            }
            composable("todo_list") {
                TodoListScreen(
                    viewModel = viewModel,
                    profileViewModel = profileViewModel,
                    onNavigateToAddTodo = { navController.navigate("add_todo") },
                    onNavigateToProfile = { navController.navigate("profile") },
                    onLogout = { 
                        viewModel.logout()
                        navController.navigate("login") {
                            popUpTo("todo_list") { inclusive = true }
                        }
                    },
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
            composable("profile") {
                ProfileScreen(
                    viewModel = profileViewModel,
                    onNavigateBack = { navController.popBackStack() }
                )
            }
        }
    }
}
