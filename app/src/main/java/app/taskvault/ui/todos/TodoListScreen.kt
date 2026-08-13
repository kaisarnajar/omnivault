package app.taskvault.ui.todos

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import app.taskvault.ui.components.OmniVaultBackground
import app.taskvault.ui.components.TaskListHeader
import app.taskvault.ui.components.TaskSearchBar
import app.taskvault.ui.components.TodoItemCard
import app.taskvault.ui.components.gradientBackground
import app.taskvault.util.DateUtils

@Composable
fun TodoListScreen(
    viewModel: TodoViewModel,
    onNavigateToAddTodo: () -> Unit,
    onNavigateBack: () -> Unit
) {
    val todos by viewModel.todos.collectAsState()
    var searchQuery by remember { mutableStateOf("") }
    var selectedFilter by remember { mutableStateOf("Today") }

    val filteredTodos = todos.filter { todo ->
        val matchesSearch = todo.title.contains(searchQuery, ignoreCase = true)

        val matchesFilter = when (selectedFilter) {
            "All Tasks" -> !todo.isCompleted
            "Today" -> !todo.isCompleted && (todo.dueDate == null || DateUtils.isToday(todo.dueDate))
            "This Week" -> !todo.isCompleted && todo.dueDate != null && DateUtils.isThisWeek(todo.dueDate)
            "This Month" -> !todo.isCompleted && todo.dueDate != null && DateUtils.isThisMonth(todo.dueDate)
            "Completed" -> todo.isCompleted
            else -> true
        }

        matchesSearch && matchesFilter
    }

    @OptIn(ExperimentalMaterial3Api::class)
    OmniVaultBackground {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Tasks", fontWeight = androidx.compose.ui.text.font.FontWeight.Bold) },
                    navigationIcon = {
                        IconButton(onClick = onNavigateBack) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color.Transparent
                    )
                )
            },
            floatingActionButton = {
                Box(
                    modifier = Modifier
                        .padding(bottom = 16.dp, end = 16.dp)
                        .size(64.dp)
                        .gradientBackground(androidx.compose.foundation.shape.CircleShape)
                        .clickable {
                            viewModel.selectTodoForEdit(null)
                            onNavigateToAddTodo()
                        },
                    contentAlignment = androidx.compose.ui.Alignment.Center
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Add Task", tint = androidx.compose.ui.graphics.Color.White)
                }
            },
            containerColor = Color.Transparent
        ) { padding ->
            LazyColumn(
                modifier =
                Modifier
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
                        onDelete = { viewModel.deleteTodo(todo.id) }
                    )
                }
                item {
                    Spacer(modifier = Modifier.height(80.dp)) // padding for FAB
                }
            }
        }
    }
}
