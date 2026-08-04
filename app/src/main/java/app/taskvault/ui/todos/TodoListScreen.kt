package app.taskvault.ui.todos

import app.taskvault.util.DateUtils
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import app.taskvault.ui.components.DashboardStats
import app.taskvault.ui.components.DashboardTopAppBar
import app.taskvault.ui.components.TaskListHeader
import app.taskvault.ui.components.TaskSearchBar
import app.taskvault.ui.components.TodoItemCard
import app.taskvault.ui.profile.ProfileViewModel

@Composable
fun TodoListScreen(
    viewModel: TodoViewModel,
    profileViewModel: ProfileViewModel,
    onNavigateToAddTodo: () -> Unit,
    onNavigateToProfile: () -> Unit,
    onNavigateToPomodoro: () -> Unit,
    onLogout: () -> Unit,
    onThemeChange: (Boolean?) -> Unit,
) {
    val todos by viewModel.todos.collectAsState()
    val userProfile by profileViewModel.userProfile.collectAsState()
    var searchQuery by remember { mutableStateOf("") }
    var selectedFilter by remember { mutableStateOf("Today") }

    // Refresh profile on screen entry to get latest
    LaunchedEffect(Unit) {
        profileViewModel.loadProfile()
    }

    val filteredTodos = todos.filter { todo ->
        val matchesSearch = todo.title.contains(searchQuery, ignoreCase = true)
        
        val matchesFilter = when (selectedFilter) {
            "Today" -> !todo.isCompleted && (todo.dueDate == null || DateUtils.isToday(todo.dueDate))
            "This Week" -> !todo.isCompleted && todo.dueDate != null && DateUtils.isThisWeek(todo.dueDate)
            "This Month" -> !todo.isCompleted && todo.dueDate != null && DateUtils.isThisMonth(todo.dueDate)
            "Completed" -> todo.isCompleted
            else -> true
        }

        matchesSearch && matchesFilter
    }

    Scaffold(
        topBar = {
            DashboardTopAppBar(
                userProfile = userProfile,
                onThemeChange = onThemeChange,
                onLogout = onLogout,
                onNavigateToProfile = onNavigateToProfile,
                onNavigateToPomodoro = onNavigateToPomodoro,
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    viewModel.selectTodoForEdit(null)
                    onNavigateToAddTodo()
                },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Task")
            }
        },
        containerColor = MaterialTheme.colorScheme.background,
    ) { padding ->
        LazyColumn(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            item {
                Spacer(modifier = Modifier.height(16.dp))
                TaskSearchBar(
                    query = searchQuery,
                    onQueryChange = { searchQuery = it },
                )
            }
            item {
                DashboardStats(todos = todos)
            }
            item {
                TaskListHeader(
                    selectedFilter = selectedFilter,
                    onFilterSelected = { selectedFilter = it }
                )
            }
            items(filteredTodos, key = { it.id }) { todo ->
                TodoItemCard(
                    todo = todo,
                    onToggleCompletion = { viewModel.toggleTodoCompletion(todo) },
                    onEdit = {
                        viewModel.selectTodoForEdit(todo)
                        onNavigateToAddTodo()
                    },
                    onDelete = { viewModel.deleteTodo(todo.id) },
                )
            }
            item {
                Spacer(modifier = Modifier.height(80.dp)) // padding for FAB
            }
        }
    }
}
