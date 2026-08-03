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
import androidx.compose.material.icons.filled.CloudDone
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
import androidx.compose.ui.unit.sp
import app.taskvault.domain.Todo
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun TodoListScreen(
    viewModel: TodoViewModel,
    onNavigateToAddTodo: () -> Unit,
    onLogout: () -> Unit,
    currentTheme: Boolean?,
    onThemeChange: (Boolean?) -> Unit
) {
    val todos by viewModel.todos.collectAsState()
    var searchQuery by remember { mutableStateOf("") }
    val filteredTodos = todos.filter { it.title.contains(searchQuery, ignoreCase = true) }

    Scaffold(
        topBar = {
            DashboardTopAppBar(
                currentTheme = currentTheme,
                onThemeChange = onThemeChange,
                onLogout = onLogout
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

@Composable
fun DashboardTopAppBar(
    currentTheme: Boolean?,
    onThemeChange: (Boolean?) -> Unit,
    onLogout: () -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(64.dp)
            .background(MaterialTheme.colorScheme.surface)
            .padding(horizontal = 20.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Icon(
                imageVector = Icons.Default.CloudDone,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(24.dp)
            )
            Text(
                text = "TaskVault",
                style = MaterialTheme.typography.headlineMedium.copy(
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
            )
        }
        Box {
            IconButton(onClick = { expanded = true }) {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = "Settings",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                DropdownMenuItem(
                    text = { Text("System Default") },
                    onClick = {
                        onThemeChange(null)
                        expanded = false
                    }
                )
                DropdownMenuItem(
                    text = { Text("Light Mode") },
                    onClick = {
                        onThemeChange(false)
                        expanded = false
                    }
                )
                DropdownMenuItem(
                    text = { Text("Dark Mode") },
                    onClick = {
                        onThemeChange(true)
                        expanded = false
                    }
                )
                DropdownMenuItem(
                    text = { Text("Logout", color = MaterialTheme.colorScheme.error) },
                    onClick = {
                        expanded = false
                        onLogout()
                    }
                )
            }
        }
    }
}

@Composable
fun TaskSearchBar(
    query: String,
    onQueryChange: (String) -> Unit
) {
    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        placeholder = { Text("Find a task...", color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)) },
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "Search",
                tint = MaterialTheme.colorScheme.outline
            )
        },
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp),
        shape = RoundedCornerShape(12.dp),
        colors = TextFieldDefaults.colors(
            focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
            unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            disabledIndicatorColor = Color.Transparent
        ),
        singleLine = true
    )
}

@Composable
fun DashboardStats(todos: List<Todo>) {
    val totalTasks = todos.size
    val completedTasks = todos.count { it.isCompleted }

    // Weekly Progress
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(128.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.primaryContainer)
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Icon(
                imageVector = Icons.Outlined.AssignmentTurnedIn,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onPrimaryContainer,
                modifier = Modifier.size(32.dp)
            )
            Column {
                Text(
                    text = "WEEKLY PROGRESS",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                )
                Text(
                    text = "$completedTasks / $totalTasks Tasks",
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
        }
    }
}

@Composable
fun TaskListHeader() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp, bottom = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "Today's Tasks",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
fun TodoItemCard(
    todo: Todo,
    onToggleCompletion: () -> Unit,
    onDelete: () -> Unit
) {
    val dateFormat = SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault())
    val dateString = dateFormat.format(Date(todo.timestamp))
    
    val opacity = if (todo.isCompleted) 0.6f else 1.0f

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(if (todo.isCompleted) MaterialTheme.colorScheme.surface.copy(alpha = 0.5f) else MaterialTheme.colorScheme.surface)
            .border(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.1f), RoundedCornerShape(12.dp))
            .clickable { onToggleCompletion() }
            .padding(16.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            // Custom Checkbox
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .background(if (todo.isCompleted) MaterialTheme.colorScheme.secondary else Color.Transparent)
                    .border(
                        2.dp,
                        if (todo.isCompleted) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.primary,
                        RoundedCornerShape(6.dp)
                    )
                    .clickable { onToggleCompletion() },
                contentAlignment = Alignment.Center
            ) {
                if (todo.isCompleted) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSecondary,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = todo.title,
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = opacity),
                    textDecoration = if (todo.isCompleted) TextDecoration.LineThrough else TextDecoration.None
                )
                Text(
                    text = "Architecture • $dateString", // Hardcoded category as in design
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = opacity)
                )
            }
            
            IconButton(onClick = onDelete) {
                Icon(
                    Icons.Default.Delete,
                    contentDescription = "Delete",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = opacity)
                )
            }
        }
    }
}
