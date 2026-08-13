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
import app.taskvault.ui.components.GlassCard
import app.taskvault.ui.components.OmniVaultBackground
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
                // Header Card: How are you feeling today?
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .clip(RoundedCornerShape(24.dp))
                        .gradientBackground()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = if (todayMood != null) "Today's Mood" else "How are you feeling today?",
                            style = MaterialTheme.typography.titleMedium,
                            color = Color.White.copy(alpha = 0.9f)
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        if (todayMood != null) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
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
                                            color = Color.White.copy(alpha = 0.8f),
                                            maxLines = 2
                                        )
                                    }
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
                initialOption = selectedMoodOption ?: moodOptions.first(),
                onDismiss = { showAddDialog = false },
                onSave = { mood, emoji, note ->
                    viewModel.addMood(mood, emoji, note)
                    showAddDialog = false
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
    val dateStr = SimpleDateFormat("EEE, MMM dd, yyyy • hh:mm a", Locale.getDefault()).format(Date(entry.timestamp))

    GlassCard(modifier = Modifier.fillMaxWidth()) {
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

            IconButton(onClick = onDelete) {
                Icon(
                    Icons.Default.Delete,
                    contentDescription = "Delete",
                    tint = MaterialTheme.colorScheme.outline
                )
            }
        }
    }
}

@Composable
fun AddMoodDialog(
    initialOption: MoodOption,
    onDismiss: () -> Unit,
    onSave: (String, String, String) -> Unit
) {
    var selectedOption by remember { mutableStateOf(initialOption) }
    var note by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Log Your Mood") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Text("Select your current mood:", style = MaterialTheme.typography.bodyMedium)

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
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 3
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onSave(selectedOption.label, selectedOption.emoji, note.trim())
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
