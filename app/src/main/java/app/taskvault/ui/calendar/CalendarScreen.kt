package app.taskvault.ui.calendar

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Today
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.taskvault.ui.components.GlassCard
import app.taskvault.ui.components.OmniVaultBackground
import app.taskvault.ui.components.TodoItemCard
import app.taskvault.ui.components.gradientBackground
import app.taskvault.ui.components.pressScale
import app.taskvault.ui.todos.TodoViewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
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
    var selectedTab by remember { mutableStateOf("Tasks") } // "Tasks" or "Events"
    var showAddEventDialog by remember { mutableStateOf(false) }

    // Filter items for selected date
    val itemsForSelectedDate = remember(todos, selectedDate) {
        todos.filter { todo ->
            todo.dueDate != null && isSameDay(todo.dueDate, selectedDate.timeInMillis)
        }
    }

    val tasksForDate = remember(itemsForSelectedDate) {
        itemsForSelectedDate.filter { it.category != "Event" && it.category != "Meeting" && it.category != "Appointment" }
    }

    val eventsForDate = remember(itemsForSelectedDate) {
        itemsForSelectedDate.filter { it.category == "Event" || it.category == "Meeting" || it.category == "Appointment" }
    }

    val currentList = if (selectedTab == "Tasks") tasksForDate else eventsForDate

    if (showAddEventDialog) {
        AddEventDialog(
            selectedDate = selectedDate,
            onDismiss = { showAddEventDialog = false },
            onSave = { title, description, priority, category, eisenhowerTag ->
                viewModel.addTodo(
                    title = title,
                    description = description,
                    dueDate = selectedDate.timeInMillis,
                    remindMe = null,
                    priority = priority,
                    category = category,
                    eisenhowerTag = eisenhowerTag
                )
                showAddEventDialog = false
            }
        )
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
                    actions = {
                        // Reset to today button
                        IconButton(
                            onClick = {
                                val today = Calendar.getInstance()
                                currentMonth = today.clone() as Calendar
                                selectedDate = today.clone() as Calendar
                            }
                        ) {
                            Icon(Icons.Default.Today, contentDescription = "Today", tint = MaterialTheme.colorScheme.primary)
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
                        .size(60.dp)
                        .gradientBackground(CircleShape)
                        .pressScale()
                        .clickable { showAddEventDialog = true },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Add Event", tint = Color.White)
                }
            },
            containerColor = Color.Transparent
        ) { padding ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(horizontal = 20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Header item: Month Title & Navigation Controls
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp, bottom = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "${currentMonth.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault())} ${currentMonth.get(Calendar.YEAR)}",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 22.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            IconButton(
                                onClick = {
                                    val prev = currentMonth.clone() as Calendar
                                    prev.add(Calendar.MONTH, -1)
                                    currentMonth = prev
                                },
                                modifier = Modifier.size(36.dp)
                            ) {
                                Icon(Icons.Default.ChevronLeft, contentDescription = "Previous Month")
                            }

                            IconButton(
                                onClick = {
                                    val next = currentMonth.clone() as Calendar
                                    next.add(Calendar.MONTH, 1)
                                    currentMonth = next
                                },
                                modifier = Modifier.size(36.dp)
                            ) {
                                Icon(Icons.Default.ChevronRight, contentDescription = "Next Month")
                            }
                        }
                    }
                }

                // Month Grid Card (Swipes up smoothly with content & supports horizontal drag gesture)
                item {
                    var totalDragOffset by remember { mutableFloatStateOf(0f) }

                    GlassCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .pointerInput(Unit) {
                                detectHorizontalDragGestures(
                                    onDragEnd = {
                                        if (totalDragOffset < -80f) {
                                            // Swipe Left -> Next Month
                                            val next = currentMonth.clone() as Calendar
                                            next.add(Calendar.MONTH, 1)
                                            currentMonth = next
                                        } else if (totalDragOffset > 80f) {
                                            // Swipe Right -> Prev Month
                                            val prev = currentMonth.clone() as Calendar
                                            prev.add(Calendar.MONTH, -1)
                                            currentMonth = prev
                                        }
                                        totalDragOffset = 0f
                                    },
                                    onHorizontalDrag = { _, dragAmount ->
                                        totalDragOffset += dragAmount
                                    }
                                )
                            },
                        shape = RoundedCornerShape(24.dp)
                    ) {
                        Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
                            // Days of week header
                            val daysOfWeek = listOf("S", "M", "T", "W", "T", "F", "S")
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceAround
                            ) {
                                daysOfWeek.forEach { day ->
                                    Text(
                                        text = day,
                                        style = MaterialTheme.typography.labelMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            // Calendar Grid calculation
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
                                                    .background(
                                                        if (isSelected) MaterialTheme.colorScheme.primary
                                                        else Color.Transparent
                                                    )
                                                    .pressScale()
                                                    .clickable { selectedDate = cellDate },
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                                    Text(
                                                        text = dayNumber.toString(),
                                                        color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface,
                                                        style = MaterialTheme.typography.bodyMedium,
                                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                                    )
                                                    if (hasTasks) {
                                                        Box(
                                                            modifier = Modifier
                                                                .size(4.dp)
                                                                .clip(CircleShape)
                                                                .background(
                                                                    if (isSelected) MaterialTheme.colorScheme.onPrimary
                                                                    else MaterialTheme.colorScheme.primary
                                                                )
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
                }

                // Two Tab Buttons: Tasks vs Events
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Glass Segmented Tab Controls
                        Row(
                            modifier = Modifier
                                .clip(RoundedCornerShape(16.dp))
                                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f))
                                .padding(4.dp),
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            val tabs = listOf(
                                "Tasks" to tasksForDate.size,
                                "Events" to eventsForDate.size
                            )

                            tabs.forEach { (tabName, count) ->
                                val isTabSelected = selectedTab == tabName
                                val tabBgColor by animateColorAsState(
                                    targetValue = if (isTabSelected) MaterialTheme.colorScheme.primary else Color.Transparent,
                                    label = "tabBg"
                                )
                                val tabTextColor by animateColorAsState(
                                    targetValue = if (isTabSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                                    label = "tabText"
                                )

                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(tabBgColor)
                                        .pressScale()
                                        .clickable { selectedTab = tabName }
                                        .padding(horizontal = 14.dp, vertical = 8.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "$tabName ($count)",
                                        style = MaterialTheme.typography.labelMedium,
                                        fontWeight = if (isTabSelected) FontWeight.Bold else FontWeight.Medium,
                                        color = tabTextColor
                                    )
                                }
                            }
                        }
                    }
                }

                // Render Active List (Tasks or Events) for selected date
                if (currentList.isEmpty()) {
                    item {
                        GlassCard(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 24.dp, horizontal = 16.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Text(
                                    text = if (selectedTab == "Tasks") "No tasks scheduled for this day." else "No events scheduled for this day.",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Spacer(modifier = Modifier.height(12.dp))
                                Button(
                                    onClick = { showAddEventDialog = true },
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Icon(Icons.Default.Add, contentDescription = null)
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(if (selectedTab == "Tasks") "Add Task" else "Add Event")
                                }
                            }
                        }
                    }
                } else {
                    items(currentList, key = { it.id }) { todo ->
                        TodoItemCard(
                            todo = todo,
                            onToggleCompletion = { viewModel.toggleTodoCompletion(todo) },
                            onEdit = { /* Edit action handled inside dialog */ },
                            onDelete = { viewModel.deleteTodo(todo.id) }
                        )
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(80.dp))
                }
            }
        }
    }
}

@Composable
fun AddEventDialog(
    selectedDate: Calendar,
    onDismiss: () -> Unit,
    onSave: (title: String, description: String, priority: String, category: String, eisenhowerTag: String) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var priority by remember { mutableStateOf("Medium") }
    var category by remember { mutableStateOf("Event") }
    var eisenhowerTag by remember { mutableStateOf("Do") }

    val formattedDate = SimpleDateFormat("EEE, MMM dd, yyyy", Locale.getDefault()).format(Date(selectedDate.timeInMillis))

    AlertDialog(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(24.dp),
        containerColor = MaterialTheme.colorScheme.surface,
        title = {
            Column {
                Text(
                    text = "Add Event / Task",
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "Scheduled for $formattedDate",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Title") },
                    placeholder = { Text("e.g. Doctor Appointment, Team Sync") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true
                )

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Notes / Description") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    maxLines = 3
                )

                // Category Selection with horizontalScroll
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text("Category", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf("Event", "Meeting", "Appointment", "General", "Work").forEach { cat ->
                            FilterChip(
                                selected = category == cat,
                                onClick = { category = cat },
                                label = { Text(cat, fontSize = 12.sp, fontWeight = if (category == cat) FontWeight.Bold else FontWeight.Normal) },
                                shape = RoundedCornerShape(10.dp)
                            )
                        }
                    }
                }

                // Priority Selection with horizontalScroll
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text("Priority", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf("High", "Medium", "Low").forEach { prio ->
                            FilterChip(
                                selected = priority == prio,
                                onClick = { priority = prio },
                                label = { Text(prio, fontSize = 12.sp, fontWeight = if (priority == prio) FontWeight.Bold else FontWeight.Normal) },
                                shape = RoundedCornerShape(10.dp)
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (title.isNotBlank()) {
                        onSave(title.trim(), description.trim(), priority, category, eisenhowerTag)
                    }
                },
                enabled = title.isNotBlank(),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Text("Save Event", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    )
}

private fun isSameDay(time1: Long, time2: Long): Boolean {
    val cal1 = Calendar.getInstance().apply { timeInMillis = time1 }
    val cal2 = Calendar.getInstance().apply { timeInMillis = time2 }
    return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
        cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR)
}
