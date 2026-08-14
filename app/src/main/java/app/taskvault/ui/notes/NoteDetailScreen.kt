package app.taskvault.ui.notes

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.taskvault.ui.components.GlassCard
import app.taskvault.ui.components.OmniVaultBackground

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoteDetailScreen(
    viewModel: NoteDetailViewModel,
    onNavigateBack: () -> Unit
) {
    val title by viewModel.title.collectAsState()
    val content by viewModel.content.collectAsState()

    // If note is new (title/content initialized empty and noteId blank), start in edit mode.
    // If opening an existing note, start in view mode (isEditing = false).
    val isNewNote = remember { title.isEmpty() && content.isEmpty() }
    var isEditing by remember { mutableStateOf(isNewNote) }

    OmniVaultBackground {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            text = if (isEditing) (if (isNewNote) "New Note" else "Edit Note") else "Note Details",
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = onNavigateBack) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                        }
                    },
                    actions = {
                        if (isEditing) {
                            IconButton(onClick = {
                                viewModel.saveNote()
                                isEditing = false
                                if (isNewNote) {
                                    onNavigateBack()
                                }
                            }) {
                                Icon(
                                    Icons.Default.Check,
                                    contentDescription = "Save Note",
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }
                        } else {
                            IconButton(onClick = { isEditing = true }) {
                                Icon(
                                    Icons.Default.Edit,
                                    contentDescription = "Edit Note",
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }
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
                    .padding(horizontal = 20.dp, vertical = 8.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                if (isEditing) {
                    // EDIT MODE: Text fields for user input
                    OutlinedTextField(
                        value = title,
                        onValueChange = { viewModel.updateTitle(it) },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = {
                            Text(
                                "Note Title...",
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Bold
                            )
                        },
                        shape = RoundedCornerShape(14.dp),
                        textStyle = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 22.sp
                        )
                    )

                    OutlinedTextField(
                        value = content,
                        onValueChange = { viewModel.updateContent(it) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min = 300.dp),
                        placeholder = {
                            Text(
                                "Write your note here...",
                                fontSize = 16.sp
                            )
                        },
                        shape = RoundedCornerShape(14.dp),
                        textStyle = MaterialTheme.typography.bodyLarge.copy(fontSize = 16.sp)
                    )

                    Button(
                        onClick = {
                            viewModel.saveNote()
                            isEditing = false
                            if (isNewNote) {
                                onNavigateBack()
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Icon(Icons.Default.Check, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Save Note", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    }
                } else {
                    // VIEW MODE: Read-only display of note
                    GlassCard(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(20.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(20.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = title.ifEmpty { "Untitled Note" },
                                    style = MaterialTheme.typography.headlineSmall,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    modifier = Modifier.weight(1f)
                                )

                                IconButton(onClick = { isEditing = true }) {
                                    Icon(
                                        Icons.Default.Edit,
                                        contentDescription = "Edit Note",
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                }
                            }

                            HorizontalDivider(color = MaterialTheme.colorScheme.surfaceVariant)

                            Text(
                                text = content.ifEmpty { "No additional content." },
                                style = MaterialTheme.typography.bodyLarge,
                                fontSize = 16.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }
    }
}
