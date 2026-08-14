package app.taskvault.ui.sleep

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Bedtime
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.NightsStay
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.taskvault.data.local.SleepEntryEntity
import app.taskvault.ui.components.FeatureHeaderCard
import app.taskvault.ui.components.GlassCard
import app.taskvault.ui.components.OmniVaultBackground
import app.taskvault.ui.components.pressScale
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

val sleepQualities = listOf(
    SleepQualityOption("Excellent", "😴", Color(0xFF10B981)),
    SleepQualityOption("Good", "😊", Color(0xFF3B82F6)),
    SleepQualityOption("Fair", "😐", Color(0xFFF59E0B)),
    SleepQualityOption("Poor", "😫", Color(0xFFEF4444))
)

data class SleepQualityOption(
    val name: String,
    val emoji: String,
    val color: Color
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SleepScreen(
    onNavigateBack: () -> Unit,
    viewModel: SleepViewModel
) {
    val sleepEntries by viewModel.sleepEntries.collectAsState()
    val sleepSummary by viewModel.sleepSummary.collectAsState()

    var selectedFilter by remember { mutableStateOf("All") }
    var showAddDialog by remember { mutableStateOf(false) }

    val filteredEntries = remember(sleepEntries, selectedFilter) {
        if (selectedFilter == "All") sleepEntries
        else sleepEntries.filter { it.sleepQuality.equals(selectedFilter, ignoreCase = true) }
    }

    OmniVaultBackground {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Bedtime,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Sleep Log", fontWeight = FontWeight.Bold)
                        }
                    },
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
                    Icon(Icons.Default.Add, contentDescription = "Log Sleep", tint = Color.White)
                }
            },
            containerColor = Color.Transparent
        ) { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            ) {
                // Header Analytics Card
                FeatureHeaderCard(
                    title = "Average Nightly Sleep",
                    modifier = Modifier.padding(16.dp)
                ) {
                    val hours = sleepSummary.avgDurationHours.toInt()
                    val mins = ((sleepSummary.avgDurationHours - hours) * 60).toInt()
                    val displayAvg = if (sleepSummary.totalLogs == 0) "--" else "${hours}h ${mins}m / night"

                    Text(
                        text = displayAvg,
                        style = MaterialTheme.typography.displaySmall,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "${sleepSummary.totalLogs}",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Text(
                                text = "Total Logs",
                                style = MaterialTheme.typography.labelSmall,
                                color = Color.White.copy(alpha = 0.7f)
                            )
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "${sleepSummary.excellentCount + sleepSummary.goodCount}",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Text(
                                text = "Restful Nights",
                                style = MaterialTheme.typography.labelSmall,
                                color = Color.White.copy(alpha = 0.7f)
                            )
                        }
                    }
                }

                // Filter Chips Row
                LazyRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val filters = listOf("All", "Excellent", "Good", "Fair", "Poor")
                    items(filters) { filter ->
                        FilterChip(
                            selected = selectedFilter == filter,
                            onClick = { selectedFilter = filter },
                            label = { Text(filter) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                                selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Sleep Entries Feed List
                if (filteredEntries.isEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            "No sleep logs recorded yet.",
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                } else {
                    LazyColumn(
                        contentPadding = PaddingValues(bottom = 80.dp, start = 16.dp, end = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(filteredEntries, key = { it.id }) { entry ->
                            SleepItemCard(
                                entry = entry,
                                onDelete = { viewModel.deleteSleepEntry(entry.id) }
                            )
                        }
                    }
                }
            }
        }

        if (showAddDialog) {
            AddSleepDialog(
                onDismiss = { showAddDialog = false },
                onSave = { bedtime, wakeTime, quality, notes ->
                    viewModel.addSleepEntry(bedtime, wakeTime, quality, notes)
                    showAddDialog = false
                }
            )
        }
    }
}

@Composable
fun SleepItemCard(
    entry: SleepEntryEntity,
    onDelete: () -> Unit
) {
    var showDeleteConfirmDialog by remember { mutableStateOf(false) }

    if (showDeleteConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirmDialog = false },
            shape = RoundedCornerShape(20.dp),
            containerColor = MaterialTheme.colorScheme.surface,
            title = { Text("Delete Sleep Log", fontWeight = FontWeight.Bold) },
            text = { Text("Do you want to delete this sleep log entry?") },
            confirmButton = {
                Button(
                    onClick = {
                        showDeleteConfirmDialog = false
                        onDelete()
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Delete", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirmDialog = false }) {
                    Text("Cancel", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        )
    }
    val qualityOpt = sleepQualities.find { it.name.equals(entry.sleepQuality, ignoreCase = true) }
        ?: sleepQualities[1]

    val timeFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())
    val dateFormat = SimpleDateFormat("EEE, MMM dd, yyyy", Locale.getDefault())

    val bedtimeStr = timeFormat.format(Date(entry.bedtime))
    val wakeTimeStr = timeFormat.format(Date(entry.wakeTime))
    val dateStr = dateFormat.format(Date(entry.timestamp))

    val hours = entry.durationMinutes / 60
    val mins = entry.durationMinutes % 60
    val durationStr = if (hours > 0) "${hours}h ${mins}m" else "${mins}m"

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
                    .background(qualityOpt.color.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Text(text = qualityOpt.emoji, fontSize = 24.sp)
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "$bedtimeStr → $wakeTimeStr",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f, fill = false)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = qualityOpt.color.copy(alpha = 0.18f)
                    ) {
                        Text(
                            text = durationStr,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = qualityOpt.color,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                            maxLines = 1,
                            softWrap = false
                        )
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "${qualityOpt.emoji} ${qualityOpt.name}",
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.SemiBold,
                        color = qualityOpt.color
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "•  $dateStr",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                if (entry.notes.isNotBlank()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = entry.notes,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.85f),
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            IconButton(onClick = { showDeleteConfirmDialog = true }) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Delete Log",
                    tint = MaterialTheme.colorScheme.error.copy(alpha = 0.7f)
                )
            }
        }
    }
}

@Composable
fun AddSleepDialog(
    onDismiss: () -> Unit,
    onSave: (bedtime: Long, wakeTime: Long, quality: String, notes: String) -> Unit
) {
    var hoursSleptText by remember { mutableStateOf("7.5") }
    var selectedQuality by remember { mutableStateOf(sleepQualities[0]) }
    var notesText by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.NightsStay, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Log Sleep Session", fontWeight = FontWeight.Bold)
            }
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(14.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = hoursSleptText,
                    onValueChange = { hoursSleptText = it },
                    label = { Text("Hours Slept (e.g. 7.5)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )

                Text(
                    "Sleep Quality",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    sleepQualities.forEach { option ->
                        val isSelected = selectedQuality.name == option.name
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = if (isSelected) option.color.copy(alpha = 0.25f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                            border = if (isSelected) androidx.compose.foundation.BorderStroke(1.5.dp, option.color) else null,
                            modifier = Modifier
                                .weight(1f)
                                .padding(horizontal = 2.dp)
                                .pressScale()
                                .clickable { selectedQuality = option }
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.padding(vertical = 8.dp)
                            ) {
                                Text(text = option.emoji, fontSize = 22.sp)
                                Text(
                                    text = option.name,
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isSelected) option.color else MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }
                }

                OutlinedTextField(
                    value = notesText,
                    onValueChange = { notesText = it },
                    label = { Text("Notes (optional)") },
                    placeholder = { Text("e.g. Woke up feeling energized!") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    maxLines = 3
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val hours = hoursSleptText.toDoubleOrNull() ?: 7.5
                    val durationMins = (hours * 60).toInt()
                    val wakeTime = System.currentTimeMillis()
                    val bedtime = wakeTime - (durationMins * 60 * 1000L)
                    onSave(bedtime, wakeTime, selectedQuality.name, notesText)
                }
            ) {
                Text("Save Log")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
