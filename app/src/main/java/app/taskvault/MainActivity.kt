package app.taskvault

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.currentBackStackEntryAsState
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
import app.taskvault.ui.components.BottomNavBar
import app.taskvault.ui.calendar.CalendarScreen
import app.taskvault.ui.tools.ToolsScreen
import app.taskvault.ui.pomodoro.PomodoroScreen
import app.taskvault.ui.pomodoro.PomodoroViewModel
import app.taskvault.ui.profile.ProfileScreen
import app.taskvault.ui.profile.ProfileViewModel
import app.taskvault.ui.todos.AddTodoScreen
import app.taskvault.ui.todos.TodoListScreen
import app.taskvault.ui.todos.TodoViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class MainActivity : ComponentActivity() {
    private lateinit var database: TodoDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val authRepository = AuthRepositoryImpl(FirebaseAuth.getInstance())
        val profileRepository = ProfileRepositoryImpl(FirebaseAuth.getInstance())
        
        database = Room.databaseBuilder(
            applicationContext,
            TodoDatabase::class.java,
            "todo_database",
        ).fallbackToDestructiveMigration().build()
        
        val repository = createTodoRepository(authRepository)
        val scratchpadRepository = app.taskvault.data.repository.ScratchpadRepositoryImpl(database.scratchpadDao, authRepository)

        setContent {
            var isDarkTheme by remember { mutableStateOf<Boolean?>(null) }
            val systemTheme = androidx.compose.foundation.isSystemInDarkTheme()
            val currentTheme = isDarkTheme ?: systemTheme

            app.taskvault.ui.theme.TaskVaultTheme(darkTheme = currentTheme) {
                TaskVaultApp(
                    repository = repository,
                    authRepository = authRepository,
                    profileRepository = profileRepository,
                    scratchpadRepository = scratchpadRepository,
                    onThemeChange = { isDarkTheme = it }
                )
            }
        }
    }

    private fun createTodoRepository(authRepository: AuthRepositoryImpl): TodoRepositoryImpl {

        val firebaseDatabase =
            FirebaseDatabase.getInstance().apply {
                try {
                    setPersistenceEnabled(true)
                } catch (e: Exception) {
                }
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
    scratchpadRepository: app.taskvault.data.repository.ScratchpadRepositoryImpl,
    onThemeChange: (Boolean?) -> Unit,
) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background,
    ) {
        val navController = rememberNavController()

        val viewModel: TodoViewModel =
            androidx.lifecycle.viewmodel.compose.viewModel(
                factory =
                    object : ViewModelProvider.Factory {
                        @Suppress("UNCHECKED_CAST")
                        override fun <T : ViewModel> create(modelClass: Class<T>): T {
                            return TodoViewModel(repository, authRepository) as T
                        }
                    },
            )

        val authViewModel: AuthViewModel =
            androidx.lifecycle.viewmodel.compose.viewModel(
                factory =
                    object : ViewModelProvider.Factory {
                        @Suppress("UNCHECKED_CAST")
                        override fun <T : ViewModel> create(modelClass: Class<T>): T {
                            return AuthViewModel(authRepository) as T
                        }
                    },
            )

        val profileViewModel: ProfileViewModel =
            androidx.lifecycle.viewmodel.compose.viewModel(
                factory =
                    object : ViewModelProvider.Factory {
                        @Suppress("UNCHECKED_CAST")
                        override fun <T : ViewModel> create(modelClass: Class<T>): T {
                            return ProfileViewModel(profileRepository) as T
                        }
                    },
            )

        val context = androidx.compose.ui.platform.LocalContext.current
        val pomodoroPreferencesRepository = remember(context) { app.taskvault.data.repository.PomodoroPreferencesRepository(context) }

        val pomodoroViewModel: PomodoroViewModel =
            androidx.lifecycle.viewmodel.compose.viewModel(
                factory =
                    object : ViewModelProvider.Factory {
                        @Suppress("UNCHECKED_CAST")
                        override fun <T : ViewModel> create(modelClass: Class<T>): T {
                            return PomodoroViewModel(pomodoroPreferencesRepository) as T
                        }
                    },
            )

        val scratchpadViewModel: app.taskvault.ui.scratchpad.ScratchpadViewModel =
            androidx.lifecycle.viewmodel.compose.viewModel(
                factory =
                    object : ViewModelProvider.Factory {
                        @Suppress("UNCHECKED_CAST")
                        override fun <T : ViewModel> create(modelClass: Class<T>): T {
                            return app.taskvault.ui.scratchpad.ScratchpadViewModel(scratchpadRepository) as T
                        }
                    },
            )

        val currentBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = currentBackStackEntry?.destination?.route

        val showBottomNav = currentRoute in listOf("todo_list", "calendar", "tools", "profile")

        Scaffold(
            bottomBar = {
                if (showBottomNav) {
                    BottomNavBar(
                        currentRoute = currentRoute,
                        onNavigate = { route ->
                            navController.navigate(route) {
                                popUpTo(navController.graph.startDestinationId) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                }
            }
        ) { padding ->
            NavHost(
                navController = navController,
                startDestination = if (FirebaseAuth.getInstance().currentUser != null) "todo_list" else "login",
                modifier = Modifier.padding(padding)
            ) {
            composable("login") {
                LoginScreen(
                    viewModel = authViewModel,
                    onNavigateToRegister = { navController.navigate("register") },
                    onLoginSuccess = {
                        navController.navigate("todo_list") {
                            popUpTo("login") { inclusive = true }
                        }
                    },
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
                    },
                )
            }
            composable("todo_list") {
                TodoListScreen(
                    viewModel = viewModel,
                    profileViewModel = profileViewModel,
                    onNavigateToAddTodo = { navController.navigate("add_todo") },
                    onLogout = {
                        viewModel.logout()
                        navController.navigate("login") {
                            popUpTo("todo_list") { inclusive = true }
                        }
                    },
                    onThemeChange = onThemeChange,
                )
            }
            composable("add_todo") {
                AddTodoScreen(
                    viewModel = viewModel,
                    onNavigateBack = { navController.popBackStack() },
                )
            }
            composable("profile") {
                ProfileScreen(
                    viewModel = profileViewModel
                )
            }
            composable("pomodoro") {
                PomodoroScreen(
                    viewModel = pomodoroViewModel,
                    onNavigateBack = { navController.popBackStack() }
                )
            }
            composable("calendar") {
                CalendarScreen(
                    viewModel = viewModel
                )
            }
            composable("tools") {
                ToolsScreen(
                    onNavigateToTool = { route -> navController.navigate(route) }
                )
            }
            composable("scratchpad") {
                app.taskvault.ui.scratchpad.ScratchpadScreen(
                    viewModel = scratchpadViewModel,
                    onNavigateBack = { navController.popBackStack() }
                )
            }
            }
        }
    }
}
