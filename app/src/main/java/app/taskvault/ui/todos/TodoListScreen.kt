package app.taskvault.ui.todos

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.AssignmentTurnedIn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import app.taskvault.domain.Todo
import app.taskvault.ui.profile.ProfileViewModel
import app.taskvault.ui.components.DashboardTopAppBar
import app.taskvault.ui.components.TaskSearchBar
import app.taskvault.ui.components.DashboardStats
import app.taskvault.ui.components.TaskListHeader
import app.taskvault.ui.components.TodoItemCard
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun TodoListScreen(
    viewModel: TodoViewModel,
    profileViewModel: ProfileViewModel,
    onNavigateToAddTodo: () -> Unit,
    onNavigateToProfile: () -> Unit,
    onLogout: () -> Unit,
    currentTheme: Boolean?,
    onThemeChange: (Boolean?) -> Unit
) {
    val todos by viewModel.todos.collectAsState()
    val userProfile by profileViewModel.userProfile.collectAsState()
    var searchQuery by remember { mutableStateOf("") }
    
    // Refresh profile on screen entry to get latest
    LaunchedEffect(Unit) {
        profileViewModel.loadProfile()
    }
    
    val filteredTodos = todos.filter { it.title.contains(searchQuery, ignoreCase = true) }

    Scaffold(
        topBar = {
            DashboardTopAppBar(
                userProfile = userProfile,
                onThemeChange = onThemeChange,
                onLogout = onLogout,
                onNavigateToProfile = onNavigateToProfile
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNavigateToAddTodo,
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Task")
            }
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(16.dp))
                TaskSearchBar(
                    query = searchQuery,
                    onQueryChange = { searchQuery = it }
                )
            }
            item {
                DashboardStats(todos = todos)
            }
            item {
                TaskListHeader()
            }
            items(filteredTodos, key = { it.id }) { todo ->
                TodoItemCard(
                    todo = todo,
                    onToggleCompletion = { viewModel.toggleTodoCompletion(todo) },
                    onDelete = { viewModel.deleteTodo(todo.id) }
                )
            }
            item {
                Spacer(modifier = Modifier.height(80.dp)) // padding for FAB
            }
        }
    }
}

