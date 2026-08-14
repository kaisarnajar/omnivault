package app.taskvault

import android.os.Bundle
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
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.fragment.app.FragmentActivity
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import app.taskvault.ui.auth.AuthViewModel
import app.taskvault.ui.auth.LoginScreen
import app.taskvault.ui.auth.RegisterScreen
import app.taskvault.ui.calendar.CalendarScreen
import app.taskvault.ui.expense.ExpenseScreen
import app.taskvault.ui.expense.ExpenseViewModel
import app.taskvault.ui.notes.NoteDetailScreen
import app.taskvault.ui.notes.NoteDetailViewModel
import app.taskvault.ui.notes.NotesListScreen
import app.taskvault.ui.notes.NotesListViewModel
import app.taskvault.ui.pomodoro.PomodoroScreen
import app.taskvault.ui.pomodoro.PomodoroViewModel
import app.taskvault.ui.profile.ProfileScreen
import app.taskvault.ui.profile.ProfileViewModel
import app.taskvault.ui.ledger.LedgerDetailScreen
import app.taskvault.ui.ledger.LedgerListScreen
import app.taskvault.ui.ledger.LedgerViewModel
import app.taskvault.ui.mood.MoodScreen
import app.taskvault.ui.mood.MoodViewModel
import app.taskvault.ui.bookmark.BookmarkScreen
import app.taskvault.ui.bookmark.BookmarkViewModel
import app.taskvault.ui.fitness.FitnessScreen
import app.taskvault.ui.fitness.FitnessViewModel
import app.taskvault.ui.sleep.SleepViewModel

import app.taskvault.ui.todos.AddTodoScreen
import app.taskvault.ui.todos.TodoListScreen
import app.taskvault.ui.todos.TodoViewModel
import app.taskvault.ui.tools.ToolsScreen
import app.taskvault.ui.vault.AddEditSecretScreen
import app.taskvault.ui.vault.VaultListScreen
import app.taskvault.ui.vault.VaultViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : FragmentActivity() {

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
    onThemeChange: (Boolean?) -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        val navController = rememberNavController()

        val viewModel: TodoViewModel = hiltViewModel()
        val authViewModel: AuthViewModel = hiltViewModel()
        val profileViewModel: ProfileViewModel = hiltViewModel()
        val pomodoroViewModel: PomodoroViewModel = hiltViewModel()
        val notesListViewModel: NotesListViewModel = hiltViewModel()
        val expenseViewModel: ExpenseViewModel = hiltViewModel()
        val vaultViewModel: VaultViewModel = hiltViewModel()
        val ledgerViewModel: LedgerViewModel = hiltViewModel()
        val moodViewModel: MoodViewModel = hiltViewModel()
        val bookmarkViewModel: BookmarkViewModel = hiltViewModel()
        val fitnessViewModel: FitnessViewModel = hiltViewModel()
        val sleepViewModel: SleepViewModel = hiltViewModel()

        val currentUser = com.google.firebase.auth.FirebaseAuth.getInstance().currentUser
        val startDest = if (currentUser != null) "tools" else "login"

        Scaffold { padding ->
            NavHost(
                navController = navController,
                startDestination = startDest,
                modifier = Modifier.padding(padding)
            ) {
                composable("login") {
                    LoginScreen(
                        viewModel = authViewModel,
                        onNavigateToRegister = { navController.navigate("register") },
                        onLoginSuccess = {
                            navController.navigate("tools") {
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
                            navController.navigate("tools") {
                                popUpTo("login") { inclusive = true }
                            }
                        }
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
                        onNavigateBack = { navController.popBackStack() }
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
                        onNavigateBack = { navController.popBackStack() }
                    )
                }
                composable("qr_scanner") {
                    app.taskvault.ui.scanner.QRScannerScreen(
                        onNavigateBack = { navController.popBackStack() }
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
                        onNavigateToNote = { noteId, isEditMode ->
                            val id = noteId ?: ""
                            navController.navigate("note_detail/$id?isEdit=$isEditMode")
                        }
                    )
                }
                composable(
                    route = "note_detail/{noteId}?isEdit={isEdit}",
                    arguments = listOf(
                        androidx.navigation.navArgument("noteId") {
                            type = androidx.navigation.NavType.StringType
                            defaultValue = ""
                        },
                        androidx.navigation.navArgument("isEdit") {
                            type = androidx.navigation.NavType.StringType
                            defaultValue = "false"
                        }
                    )
                ) { _ ->
                    val noteDetailViewModel: NoteDetailViewModel = hiltViewModel()
                    NoteDetailScreen(
                        viewModel = noteDetailViewModel,
                        onNavigateBack = { navController.popBackStack() }
                    )
                }

                composable("vault_list") {
                    VaultListScreen(
                        viewModel = vaultViewModel,
                        onNavigateToAddEdit = { navController.navigate("add_edit_secret") },
                        onNavigateBack = { navController.popBackStack() }
                    )
                }
                composable("add_edit_secret") {
                    AddEditSecretScreen(
                        viewModel = vaultViewModel,
                        onNavigateBack = { navController.popBackStack() }
                    )
                }

                composable("ledger_list") {
                    LedgerListScreen(
                        viewModel = ledgerViewModel,
                        onNavigateBack = { navController.popBackStack() },
                        onNavigateToPerson = { personId ->
                            navController.navigate("ledger_detail/$personId")
                        }
                    )
                }
                composable(
                    route = "ledger_detail/{personId}",
                    arguments = listOf(androidx.navigation.navArgument("personId") { type = androidx.navigation.NavType.StringType })
                ) { backStackEntry ->
                    val personId = backStackEntry.arguments?.getString("personId") ?: ""
                    val persons by ledgerViewModel.persons.collectAsState()
                    val personName = persons.find { it.id == personId }?.name ?: "Person"
                    LedgerDetailScreen(
                        viewModel = ledgerViewModel,
                        personId = personId,
                        personName = personName,
                        onNavigateBack = { navController.popBackStack() }
                    )
                }

                composable("mood_journal") {
                    MoodScreen(
                        viewModel = moodViewModel,
                        onNavigateBack = { navController.popBackStack() }
                    )
                }

                composable("bookmarks") {
                    BookmarkScreen(
                        viewModel = bookmarkViewModel,
                        onNavigateBack = { navController.popBackStack() }
                    )
                }

                composable("fitness") {
                    FitnessScreen(
                        viewModel = fitnessViewModel,
                        onNavigateBack = { navController.popBackStack() }
                    )
                }

                composable("sleep") {
                    app.taskvault.ui.sleep.SleepScreen(
                        viewModel = sleepViewModel,
                        onNavigateBack = { navController.popBackStack() }
                    )
                }
            }
        }
    }
}
