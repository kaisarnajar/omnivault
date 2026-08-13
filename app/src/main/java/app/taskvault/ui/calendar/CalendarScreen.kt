package app.taskvault.ui.calendar

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import app.taskvault.ui.components.OmniVaultBackground
import app.taskvault.ui.components.TodoItemCard
import app.taskvault.ui.todos.TodoViewModel
import java.util.Calendar
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalendarScreen(
    viewModel: TodoViewModel,
    onNavigateBack: () -> Unit
) {
    val todos by viewModel.todos.collectAsState()

    // Calendar State
    var currentMonth by remember { mutableStateOf(Calendar.getInstance()) }
    var selectedDate by remember { mutableStateOf(Calendar.getInstance()) }

    // Filter tasks for selected date
    val tasksForSelectedDate = todos.filter { todo ->
        todo.dueDate != null && isSameDay(todo.dueDate, selectedDate.timeInMillis)
    }

    OmniVaultBackground {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Calendar", fontWeight = FontWeight.Bold) },
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
            containerColor = Color.Transparent
        ) { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(horizontal = 20.dp)
            ) {
                // Calendar Header
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "${currentMonth.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault())} ${currentMonth.get(Calendar.YEAR)}",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Row {
                        TextButton(onClick = {
                            val prev = currentMonth.clone() as Calendar
                            prev.add(Calendar.MONTH, -1)
                            currentMonth = prev
                        }) {
                            Text("<")
                        }
                        TextButton(onClick = {
                            val next = currentMonth.clone() as Calendar
                            next.add(Calendar.MONTH, 1)
                            currentMonth = next
                        }) {
                            Text(">")
                        }
                    }
                }

                app.taskvault.ui.components.GlassCard(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
                ) {
                    Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
                        // Days of week header
                        val daysOfWeek = listOf("S", "M", "T", "W", "T", "F", "S")
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceAround) {
                            daysOfWeek.forEach { day ->
                                Text(
                                    text = day,
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Calendar Grid
                        val daysInMonth = currentMonth.getActualMaximum(Calendar.DAY_OF_MONTH)
                        val firstDayOfMonth = currentMonth.clone() as Calendar
                        firstDayOfMonth.set(Calendar.DAY_OF_MONTH, 1)
                        val startDayOfWeek = firstDayOfMonth.get(Calendar.DAY_OF_WEEK) - 1 // 0-indexed (Sunday = 0)

                        val totalCells = startDayOfWeek + daysInMonth
                        val rows = Math.ceil(totalCells / 7.0).toInt()

                        for (i in 0 until rows) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                horizontalArrangement = Arrangement.SpaceAround
                            ) {
                                for (j in 0..6) {
                                    val cellIndex = i * 7 + j
                                    val dayNumber = cellIndex - startDayOfWeek + 1

                                    if (dayNumber in 1..daysInMonth) {
                                        val cellDate = currentMonth.clone() as Calendar
                                        cellDate.set(Calendar.DAY_OF_MONTH, dayNumber)

                                        val isSelected = isSameDay(selectedDate.timeInMillis, cellDate.timeInMillis)
                                        val hasTasks = todos.any { it.dueDate != null && isSameDay(it.dueDate, cellDate.timeInMillis) }

                                        Box(
                                            modifier = Modifier
                                                .size(40.dp)
                                                .clip(CircleShape)
                                                .background(if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent)
                                                .clickable { selectedDate = cellDate },
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                                Text(
                                                    text = dayNumber.toString(),
                                                    color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface,
                                                    style = MaterialTheme.typography.bodyMedium
                                                )
                                                if (hasTasks) {
                                                    Box(
                                                        modifier = Modifier
                                                            .size(4.dp)
                                                            .clip(CircleShape)
                                                            .background(if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.primary)
                                                    )
                                                }
                                            }
                                        }
                                    } else {
                                        Spacer(modifier = Modifier.size(40.dp))
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Tasks for selected day
                Text(
                    text = "Tasks for ${selectedDate.get(Calendar.DAY_OF_MONTH)} ${selectedDate.getDisplayName(Calendar.MONTH, Calendar.SHORT, Locale.getDefault())}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                if (tasksForSelectedDate.isEmpty()) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("No tasks for this day.", color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                } else {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(tasksForSelectedDate, key = { it.id }) { todo ->
                            TodoItemCard(
                                todo = todo,
                                onToggleCompletion = { viewModel.toggleTodoCompletion(todo) },
                                onEdit = { /* Can't edit from calendar easily without nav */ },
                                onDelete = { viewModel.deleteTodo(todo.id) }
                            )
                        }
                    }
                }
            }
        }
    }
}

private fun isSameDay(time1: Long, time2: Long): Boolean {
    val cal1 = Calendar.getInstance().apply { timeInMillis = time1 }
    val cal2 = Calendar.getInstance().apply { timeInMillis = time2 }
    return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
        cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR)
}
