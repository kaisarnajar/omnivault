package app.taskvault.ui.fitness

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.DirectionsRun
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.SelfImprovement
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.taskvault.data.local.FitnessActivityEntity
import app.taskvault.ui.components.FeatureHeaderCard
import app.taskvault.ui.components.GlassCard
import app.taskvault.ui.components.OmniVaultBackground
import app.taskvault.ui.components.pressScale
import app.taskvault.ui.components.gradientBackground
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

val fitnessFilters = listOf("All", "Workout", "Running", "Sports", "Other")
val muscleGroups = listOf("Chest", "Back", "Legs", "Shoulders", "Arms", "Core", "Full Body")

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FitnessScreen(
    viewModel: FitnessViewModel,
    onNavigateBack: () -> Unit
) {
    val activities by viewModel.activities.collectAsState()
    val todaySummary by viewModel.todaySummary.collectAsState()
    val selectedFilter by viewModel.selectedFilter.collectAsState()

    var showAddDialog by remember { mutableStateOf(false) }

    OmniVaultBackground {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Fitness Tracker", fontWeight = FontWeight.Bold) },
                    navigationIcon = {
                        IconButton(onClick = onNavigateBack) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
                )
            },
            floatingActionButton = {
                FloatingActionButton(
                    onClick = { showAddDialog = true },
                    containerColor = MaterialTheme.colorScheme.primary
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Add Fitness Activity", tint = Color.White)
                }
            },
            containerColor = Color.Transparent
        ) { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            ) {
                // Today's Fitness Summary Header
                FeatureHeaderCard(
                    title = "Today's Fitness Overview",
                    modifier = Modifier.padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        SummaryStatItem(
                            label = "Active Mins",
                            value = "${todaySummary.totalActiveMinutes}m"
                        )
                        SummaryStatItem(
                            label = "Distance",
                            value = String.format(Locale.getDefault(), "%.1f km", todaySummary.totalDistanceKm)
                        )
                        SummaryStatItem(
                            label = "Workouts",
                            value = "${todaySummary.totalWorkouts}"
                        )
                    }
                }

                // Filter Chips
                LazyRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(fitnessFilters) { filter ->
                        FilterChip(
                            selected = selectedFilter == filter,
                            onClick = { viewModel.selectFilter(filter) },
                            label = { Text(filter) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MaterialTheme.colorScheme.primary,
                                selectedLabelColor = Color.White
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                // Activity List
                if (activities.isEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No fitness activities recorded yet.\nTap + to log your workout or run!",
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                } else {
                    LazyColumn(
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(activities, key = { it.id }) { item ->
                            FitnessItemCard(
                                activity = item,
                                onDelete = { viewModel.deleteActivity(item.id) }
                            )
                        }
                    }
                }
            }
        }

        if (showAddDialog) {
            AddFitnessDialog(
                onDismiss = { showAddDialog = false },
                onSave = { type, title, muscle, dist, dur, cal, notes ->
                    viewModel.addActivity(type, title, muscle, dist, dur, cal, notes)
                    showAddDialog = false
                }
            )
        }
    }
}

@Composable
fun SummaryStatItem(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = Color.White.copy(alpha = 0.8f)
        )
    }
}

@Composable
fun FitnessItemCard(
    activity: FitnessActivityEntity,
    onDelete: () -> Unit
) {
    val icon: ImageVector = when (activity.activityType.lowercase()) {
        "workout" -> Icons.Default.FitnessCenter
        "running" -> Icons.AutoMirrored.Filled.DirectionsRun
        "sports" -> Icons.Default.EmojiEvents
        else -> Icons.Default.SelfImprovement
    }

    val dateStr = SimpleDateFormat("MMM dd, yyyy • hh:mm a", Locale.getDefault()).format(Date(activity.timestamp))

    GlassCard(modifier = Modifier.fillMaxWidth().pressScale()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = activity.activityType,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = activity.title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = MaterialTheme.colorScheme.secondaryContainer
                    ) {
                        Text(
                            text = activity.activityType,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSecondaryContainer,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                // Activity Metrics Badges
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (activity.targetMuscle.isNotBlank()) {
                        Text(
                            text = "💪 ${activity.targetMuscle}",
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                    if (activity.distanceKm > 0) {
                        Text(
                            text = "🏃 ${activity.distanceKm} km",
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                    if (activity.durationMinutes > 0) {
                        Text(
                            text = "⏱️ ${activity.durationMinutes} mins",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    if (activity.caloriesBurned > 0) {
                        Text(
                            text = "🔥 ${activity.caloriesBurned} kcal",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }

                if (activity.notes.isNotBlank()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = activity.notes,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 2
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = dateStr,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                )
            }

            IconButton(onClick = onDelete) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Delete",
                    tint = MaterialTheme.colorScheme.outline
                )
            }
        }
    }
}

@Composable
fun AddFitnessDialog(
    onDismiss: () -> Unit,
    onSave: (String, String, String, Double, Int, Int, String) -> Unit
) {
    var selectedType by remember { mutableStateOf("Workout") }
    var title by remember { mutableStateOf("") }
    var selectedMuscle by remember { mutableStateOf("Chest") }
    var distanceKm by remember { mutableStateOf("") }
    var durationMinutes by remember { mutableStateOf("") }
    var calories by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }

    val activityTypes = listOf("Workout", "Running", "Sports", "Other")

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Log Fitness Activity") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                Text("Activity Type", style = MaterialTheme.typography.labelMedium)

                // Activity Type Selector Row
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(activityTypes) { type ->
                        FilterChip(
                            selected = selectedType == type,
                            onClick = {
                                selectedType = type
                                if (title.isBlank()) {
                                    title = when (type) {
                                        "Workout" -> "Gym Session"
                                        "Running" -> "Running"
                                        "Sports" -> "Badminton Match"
                                        else -> "Activity"
                                    }
                                }
                            },
                            label = { Text(type) }
                        )
                    }
                }

                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Title / Activity Name") },
                    placeholder = {
                        Text(
                            when (selectedType) {
                                "Workout" -> "e.g. Chest & Triceps Day"
                                "Running" -> "e.g. Morning Run"
                                "Sports" -> "e.g. Badminton Match"
                                else -> "Activity"
                            }
                        )
                    },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                // Dynamic fields based on Activity Type
                when (selectedType) {
                    "Workout" -> {
                        Text("Target Muscle Group", style = MaterialTheme.typography.labelMedium)
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            items(muscleGroups) { muscle ->
                                FilterChip(
                                    selected = selectedMuscle == muscle,
                                    onClick = { selectedMuscle = muscle },
                                    label = { Text(muscle) }
                                )
                            }
                        }

                        OutlinedTextField(
                            value = durationMinutes,
                            onValueChange = { durationMinutes = it },
                            label = { Text("Duration (Minutes)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )
                    }

                    "Running" -> {
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedTextField(
                                value = distanceKm,
                                onValueChange = { distanceKm = it },
                                label = { Text("Distance (Km)") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                modifier = Modifier.weight(1f),
                                singleLine = true
                            )
                            OutlinedTextField(
                                value = durationMinutes,
                                onValueChange = { durationMinutes = it },
                                label = { Text("Duration (Mins)") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                modifier = Modifier.weight(1f),
                                singleLine = true
                            )
                        }
                    }

                    "Sports", "Other" -> {
                        OutlinedTextField(
                            value = durationMinutes,
                            onValueChange = { durationMinutes = it },
                            label = { Text("Duration (Minutes)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )
                    }
                }

                OutlinedTextField(
                    value = calories,
                    onValueChange = { calories = it },
                    label = { Text("Calories Burned (Optional)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("Notes / Sets / Reflections (Optional)") },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 3
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    val dist = distanceKm.toDoubleOrNull() ?: 0.0
                    val dur = durationMinutes.toIntOrNull() ?: 0
                    val cal = calories.toIntOrNull() ?: 0
                    val muscle = if (selectedType == "Workout") selectedMuscle else ""
                    val activityTitle = title.ifBlank { selectedType }

                    onSave(selectedType, activityTitle, muscle, dist, dur, cal, notes.trim())
                }
            ) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
