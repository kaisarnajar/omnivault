package app.taskvault.ui.mood

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
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.taskvault.data.local.MoodEntryEntity
import app.taskvault.ui.components.FeatureHeaderCard
import app.taskvault.ui.components.GlassCard
import app.taskvault.ui.components.OmniVaultBackground
import app.taskvault.ui.components.pressScale
import app.taskvault.ui.components.gradientBackground
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class MoodOption(val label: String, val emoji: String)

val moodOptions = listOf(
    MoodOption("Amazing", "😄"),
    MoodOption("Good", "🙂"),
    MoodOption("Okay", "😐"),
    MoodOption("Bad", "🙁"),
    MoodOption("Awful", "😫")
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MoodScreen(
    viewModel: MoodViewModel,
    onNavigateBack: () -> Unit
) {
    val moods by viewModel.moods.collectAsState()
    val todayMood by viewModel.todayMood.collectAsState()

    var showAddDialog by remember { mutableStateOf(false) }
    var selectedMoodOption by remember { mutableStateOf<MoodOption?>(null) }

    OmniVaultBackground {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Mood Journal", fontWeight = FontWeight.Bold) },
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
                    onClick = {
                        selectedMoodOption = moodOptions.first()
                        showAddDialog = true
                    },
                    containerColor = MaterialTheme.colorScheme.primary
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Log Mood", tint = Color.White)
                }
            },
            containerColor = Color.Transparent
        ) { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            ) {
                FeatureHeaderCard(
                    title = if (todayMood != null) "Today's Mood" else "How are you feeling today?",
                    modifier = Modifier.padding(16.dp)
                ) {
                    if (todayMood != null) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(text = todayMood!!.emoji, fontSize = 44.sp)
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(
                                        text = todayMood!!.mood,
                                        style = MaterialTheme.typography.headlineSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                    if (todayMood!!.note.isNotBlank()) {
                                        Text(
                                            text = todayMood!!.note,
                                            style = MaterialTheme.typography.bodySmall,
                                            color = Color.White.copy(alpha = 0.85f),
                                            maxLines = 2
                                        )
                                    }
                                }
                            }

                            Button(
                                onClick = {
                                    selectedMoodOption = moodOptions.find { it.label == todayMood?.mood } ?: moodOptions.first()
                                    showAddDialog = true
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color.White.copy(alpha = 0.25f),
                                    contentColor = Color.White
                                ),
                                shape = RoundedCornerShape(12.dp),
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                            ) {
                                Text("Update", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            }
                        }
                    } else {
                        // Quick Selector Row
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            modifier = Modifier.fillMaxWidth(),
                            contentPadding = PaddingValues(horizontal = 8.dp)
                        ) {
                            items(moodOptions) { option ->
                                Box(
                                    modifier = Modifier
                                        .size(54.dp)
                                        .clip(CircleShape)
                                        .background(Color.White.copy(alpha = 0.25f))
                                        .pressScale()
                                        .clickable {
                                            selectedMoodOption = option
                                            showAddDialog = true
                                        },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(text = option.emoji, fontSize = 28.sp)
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // History Section Header
                PaddingValues(horizontal = 16.dp).let {
                    Text(
                        text = "Mood Timeline",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(horizontal = 20.dp, vertical = 4.dp)
                    )
                }

                // Mood Entries List
                if (moods.isEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No mood entries logged yet.\nTap an emoji or + to record how you feel!",
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                } else {
                    LazyColumn(
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(moods, key = { it.id }) { item ->
                            MoodItemCard(
                                entry = item,
                                onDelete = { viewModel.deleteMood(item.id) }
                            )
                        }
                    }
                }
            }
        }

        if (showAddDialog) {
            AddMoodDialog(
                initialOption = selectedMoodOption ?: moodOptions.find { it.label == todayMood?.mood } ?: moodOptions.first(),
                existingTodayNote = if (todayMood != null && selectedMoodOption == null) todayMood?.note ?: "" else "",
                isUpdate = todayMood != null,
                onDismiss = {
                    showAddDialog = false
                    selectedMoodOption = null
                },
                onSave = { mood, emoji, note ->
                    viewModel.addMood(mood, emoji, note)
                    showAddDialog = false
                    selectedMoodOption = null
                }
            )
        }
    }
}

@Composable
fun MoodItemCard(
    entry: MoodEntryEntity,
    onDelete: () -> Unit
) {
    var showDeleteConfirmDialog by remember { mutableStateOf(false) }

    if (showDeleteConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirmDialog = false },
            shape = RoundedCornerShape(20.dp),
            containerColor = MaterialTheme.colorScheme.surface,
            title = { Text("Delete Mood Log", fontWeight = FontWeight.Bold) },
            text = { Text("Do you want to delete this ${entry.mood} mood log?") },
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

    val dateStr = SimpleDateFormat("EEE, MMM dd, yyyy • hh:mm a", Locale.getDefault()).format(Date(entry.timestamp))

    GlassCard(modifier = Modifier.fillMaxWidth().pressScale()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = entry.emoji, fontSize = 36.sp)

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = entry.mood,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                if (entry.note.isNotBlank()) {
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = entry.note,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = dateStr,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                )
            }

            IconButton(onClick = { showDeleteConfirmDialog = true }) {
                Icon(
                    Icons.Default.Delete,
                    contentDescription = "Delete",
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}

@Composable
fun AddMoodDialog(
    initialOption: MoodOption,
    existingTodayNote: String = "",
    isUpdate: Boolean = false,
    onDismiss: () -> Unit,
    onSave: (String, String, String) -> Unit
) {
    var selectedOption by remember { mutableStateOf(initialOption) }
    var note by remember { mutableStateOf(existingTodayNote) }

    AlertDialog(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(24.dp),
        containerColor = MaterialTheme.colorScheme.surface,
        title = {
            Text(
                if (isUpdate) "Update Today's Mood" else "Log Your Mood",
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Text(
                    if (isUpdate) "Update your feeling for today:" else "Select your current mood:",
                    style = MaterialTheme.typography.bodyMedium
                )

                // Emoji Selection Row
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    moodOptions.forEach { option ->
                        val isSelected = option == selectedOption
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(
                                    if (isSelected) MaterialTheme.colorScheme.primaryContainer
                                    else Color.Transparent
                                )
                                .clickable { selectedOption = option },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = option.emoji, fontSize = 26.sp)
                        }
                    }
                }

                Text(
                    text = "Selected: ${selectedOption.emoji} ${selectedOption.label}",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.primary
                )

                OutlinedTextField(
                    value = note,
                    onValueChange = { note = it },
                    label = { Text("Note / Reflection (Optional)") },
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 3
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onSave(selectedOption.label, selectedOption.emoji, note.trim())
                },
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(if (isUpdate) "Update Mood" else "Save Mood", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    )
}
