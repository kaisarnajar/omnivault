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
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import app.taskvault.ui.auth.AuthViewModel
import app.taskvault.ui.auth.LoginScreen
import app.taskvault.ui.auth.RegisterScreen
import app.taskvault.ui.splash.SplashScreen

import app.taskvault.ui.calendar.CalendarScreen
import app.taskvault.ui.tools.ToolsScreen
import app.taskvault.ui.pomodoro.PomodoroScreen
import app.taskvault.ui.pomodoro.PomodoroViewModel
import app.taskvault.ui.profile.ProfileScreen
import app.taskvault.ui.profile.ProfileViewModel
import app.taskvault.ui.todos.AddTodoScreen
import app.taskvault.ui.todos.TodoListScreen
import app.taskvault.ui.todos.TodoViewModel
import app.taskvault.ui.expense.ExpenseScreen
import app.taskvault.ui.expense.ExpenseViewModel
import app.taskvault.ui.notes.NotesListScreen
import app.taskvault.ui.notes.NotesListViewModel
import app.taskvault.ui.notes.NoteDetailScreen
import app.taskvault.ui.notes.NoteDetailViewModel
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        setContent {
            var isDarkTheme by remember { mutableStateOf<Boolean?>(null) }
            val systemTheme = androidx.compose.foundation.isSystemInDarkTheme()
            val currentTheme = isDarkTheme ?: systemTheme

            app.taskvault.ui.theme.TaskVaultTheme(darkTheme = currentTheme) {
                TaskVaultApp(
                    onThemeChange = { isDarkTheme = it }
                )
            }
        }
    }
}

@Composable
fun TaskVaultApp(
    onThemeChange: (Boolean?) -> Unit,
) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background,
    ) {
        val navController = rememberNavController()

        val viewModel: TodoViewModel = hiltViewModel()
        val authViewModel: AuthViewModel = hiltViewModel()
        val profileViewModel: ProfileViewModel = hiltViewModel()
        val pomodoroViewModel: PomodoroViewModel = hiltViewModel()
        val notesListViewModel: NotesListViewModel = hiltViewModel()
        val expenseViewModel: ExpenseViewModel = hiltViewModel()

        Scaffold { padding ->
            NavHost(
                navController = navController,
                startDestination = "splash",
                modifier = Modifier.padding(padding)
            ) {
                composable("splash") {
                    SplashScreen(
                        onNavigateToLogin = {
                            navController.navigate("login") {
                                popUpTo("splash") { inclusive = true }
                            }
                        },
                        onNavigateToHome = {
                            navController.navigate("tools") {
                                popUpTo("splash") { inclusive = true }
                            }
                        }
                    )
                }
                composable("login") {
                    LoginScreen(
                        viewModel = authViewModel,
                        onNavigateToRegister = { navController.navigate("register") },
                        onLoginSuccess = {
                            navController.navigate("tools") {
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
                            navController.navigate("tools") {
                                popUpTo("login") { inclusive = true }
                            }
                        },
                    )
                }
                composable("todo_list") {
                    TodoListScreen(
                        viewModel = viewModel,
                        onNavigateToAddTodo = { navController.navigate("add_todo") },
                        onNavigateBack = { navController.popBackStack() }
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
                        viewModel = profileViewModel,
                        onLogout = {
                            viewModel.logout()
                            navController.navigate("login") {
                                popUpTo("tools") { inclusive = true }
                            }
                        },
                        onThemeChange = onThemeChange,
                        onNavigateBack = { navController.popBackStack() }
                    )
                }
                composable("pomodoro") {
                    PomodoroScreen(
                        viewModel = pomodoroViewModel,
                        onNavigateBack = { navController.popBackStack() },
                    )
                }
                composable("expense_tracker") {
                    ExpenseScreen(
                        viewModel = expenseViewModel,
                        onNavigateBack = { navController.popBackStack() }
                    )
                }
                composable("calendar") {
                    CalendarScreen(
                        viewModel = viewModel,
                        onNavigateBack = { navController.popBackStack() }
                    )
                }
                composable("tools") {
                    ToolsScreen(
                        onNavigateToTool = { route -> navController.navigate(route) },
                        onNavigateToProfile = { navController.navigate("profile") }
                    )
                }
                composable("notes_list") {
                    NotesListScreen(
                        viewModel = notesListViewModel,
                        onNavigateBack = { navController.popBackStack() },
                        onNavigateToNote = { noteId ->
                            val id = noteId ?: java.util.UUID.randomUUID().toString()
                            navController.navigate("note_detail/$id")
                        }
                    )
                }
                composable(
                    route = "note_detail/{noteId}",
                    arguments = listOf(androidx.navigation.navArgument("noteId") { type = androidx.navigation.NavType.StringType })
                ) { _ ->
                    val noteDetailViewModel: NoteDetailViewModel = hiltViewModel()
                    NoteDetailScreen(
                        viewModel = noteDetailViewModel,
                        onNavigateBack = { navController.popBackStack() }
                    )
                }
            }
        }
    }
}
