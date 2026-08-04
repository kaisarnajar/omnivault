package app.taskvault.ui.todos

import android.Manifest
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddTask
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.taskvault.ui.components.AddTodoTopAppBar
import app.taskvault.ui.components.DateTimeSelectors
import app.taskvault.ui.components.PrioritySelector

@Composable
fun AddTodoScreen(
    viewModel: TodoViewModel,
    onNavigateBack: () -> Unit,
) {
    val selectedTodo by viewModel.selectedTodo.collectAsState()
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var dueDate by remember { mutableStateOf<Long?>(null) }
    var priority by remember { mutableStateOf("Medium") }

    // Pre-fill if editing
    LaunchedEffect(selectedTodo) {
        selectedTodo?.let {
            title = it.title
            description = it.description
            dueDate = it.dueDate
            priority = it.priority
        }
    }

    val permissionLauncher =
        rememberLauncherForActivityResult(
            ActivityResultContracts.RequestPermission(),
        ) { _ ->
            // We can handle the result if needed
        }

    LaunchedEffect(Unit) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }

    Scaffold(
        topBar = {
            AddTodoTopAppBar(
                isEditing = selectedTodo != null,
                onNavigateBack = onNavigateBack,
            )
        },
        containerColor = MaterialTheme.colorScheme.background,
        bottomBar = {
            Box(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.surface)
                        .padding(20.dp),
                contentAlignment = Alignment.Center,
            ) {
                Button(
                    onClick = {
                        if (title.isNotBlank()) {
                            val computedRemindMe = dueDate?.minus(30 * 60 * 1000L)
                            if (selectedTodo != null) {
                                viewModel.updateTodoDetail(selectedTodo!!.id, title, description, dueDate, computedRemindMe, priority)
                            } else {
                                viewModel.addTodo(title, description, dueDate, computedRemindMe, priority)
                            }
                            onNavigateBack()
                        }
                    },
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                    enabled = title.isNotBlank(),
                    shape = RoundedCornerShape(12.dp),
                    colors =
                        ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.onPrimary,
                        ),
                ) {
                    Icon(Icons.Default.AddTask, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        if (selectedTodo != null) "Save Changes" else "Create Task",
                        style = MaterialTheme.typography.headlineMedium.copy(fontSize = 18.sp),
                    )
                }
            }
        },
    ) { padding ->
        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(horizontal = 20.dp)
                    .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(24.dp),
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // Task Title Input
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = "TASK TITLE",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    placeholder = { Text("What needs to be done?", color = MaterialTheme.colorScheme.outlineVariant) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    textStyle = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold, fontSize = 24.sp),
                    colors =
                        TextFieldDefaults.colors(
                            focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                            unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,
                            disabledIndicatorColor = Color.Transparent,
                        ),
                    singleLine = true,
                )
            }

            // Description Input
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = "DESCRIPTION",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    placeholder = { Text("Add more details about this task...", color = MaterialTheme.colorScheme.outlineVariant) },
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .height(120.dp),
                    shape = RoundedCornerShape(12.dp),
                    textStyle = MaterialTheme.typography.bodyMedium,
                    colors =
                        TextFieldDefaults.colors(
                            focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                            unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,
                            disabledIndicatorColor = Color.Transparent,
                        ),
                    maxLines = 4,
                )
            }

            DateTimeSelectors(
                dueDate = dueDate,
                onDueDateChange = { dueDate = it },
            )

            PrioritySelector(
                selectedPriority = priority,
                onPrioritySelect = { priority = it },
            )

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}
