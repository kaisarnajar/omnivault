package app.taskvault.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Undo
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.Undo
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
fun TodoItemCard(
    modifier: Modifier = Modifier,
    todo: Todo,
    onToggleCompletion: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    val dateString =
        todo.dueDate?.let {
            SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(Date(it))
        } ?: "No Due Date"

    val timeString =
        todo.dueDate?.let {
            SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date(it))
        } ?: ""

    val displayDate = if (timeString.isNotEmpty()) "$dateString at $timeString" else dateString
    val opacity = if (todo.isCompleted) 0.6f else 1.0f

    var showDeleteDialog by remember { mutableStateOf(false) }
    var showDetailsDialog by remember { mutableStateOf(false) }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Delete Task") },
            text = { Text("Do you want to delete this task?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDeleteDialog = false
                        onDelete()
                    }
                ) {
                    Text("Delete", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    if (showDetailsDialog) {
        TaskDetailsDialog(
            todo = todo,
            displayDate = displayDate,
            onDismiss = { showDetailsDialog = false },
            onToggleCompletion = onToggleCompletion,
            onEdit = {
                showDetailsDialog = false
                onEdit()
            },
            onDelete = {
                showDetailsDialog = false
                onDelete()
            }
        )
    }

    val interactionSource = remember { MutableInteractionSource() }

    GlassCard(
        modifier =
        modifier
            .fillMaxWidth()
            .pressScale(interactionSource = interactionSource)
            .clickable(interactionSource = interactionSource, indication = null) { showDetailsDialog = true }
            .padding(bottom = 8.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth().padding(16.dp)
        ) {
            // Custom Checkbox for quick completion
            Box(
                modifier =
                Modifier
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
                    text = "${todo.priority} Priority • $displayDate",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = opacity)
                )

                Spacer(modifier = Modifier.height(4.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = todo.category,
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier
                            .background(MaterialTheme.colorScheme.primaryContainer, RoundedCornerShape(4.dp))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    )

                    if (todo.eisenhowerTag.isNotEmpty()) {
                        val (tagColor, tagBgColor) = when (todo.eisenhowerTag) {
                            "Do" -> Color(0xFFD32F2F) to Color(0xFFFFEBEE)
                            "Schedule" -> Color(0xFF1976D2) to Color(0xFFE3F2FD)
                            "Delegate" -> Color(0xFF388E3C) to Color(0xFFE8F5E9)
                            "Delete" -> Color(0xFF616161) to Color(0xFFF5F5F5)
                            else -> MaterialTheme.colorScheme.secondary to MaterialTheme.colorScheme.secondaryContainer
                        }
                        Text(
                            text = todo.eisenhowerTag,
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = tagColor,
                            modifier = Modifier
                                .background(tagBgColor, RoundedCornerShape(4.dp))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
            }

            IconButton(onClick = onEdit) {
                Icon(
                    Icons.Default.Edit,
                    contentDescription = "Edit",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = opacity)
                )
            }

            IconButton(onClick = { showDeleteDialog = true }) {
                Icon(
                    Icons.Default.Delete,
                    contentDescription = "Delete",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = opacity)
                )
            }
        }
    }
}

@Composable
fun TaskDetailsDialog(
    todo: Todo,
    displayDate: String,
    onDismiss: () -> Unit,
    onToggleCompletion: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Column {
                Text(
                    text = todo.title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(6.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "${todo.priority} Priority",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier
                            .background(MaterialTheme.colorScheme.primaryContainer, RoundedCornerShape(4.dp))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                    Text(
                        text = todo.category,
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.secondary,
                        modifier = Modifier
                            .background(MaterialTheme.colorScheme.secondaryContainer, RoundedCornerShape(4.dp))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                    if (todo.eisenhowerTag.isNotEmpty()) {
                        val (tagColor, tagBgColor) = when (todo.eisenhowerTag) {
                            "Do" -> Color(0xFFD32F2F) to Color(0xFFFFEBEE)
                            "Schedule" -> Color(0xFF1976D2) to Color(0xFFE3F2FD)
                            "Delegate" -> Color(0xFF388E3C) to Color(0xFFE8F5E9)
                            "Delete" -> Color(0xFF616161) to Color(0xFFF5F5F5)
                            else -> MaterialTheme.colorScheme.secondary to MaterialTheme.colorScheme.secondaryContainer
                        }
                        Text(
                            text = todo.eisenhowerTag,
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = tagColor,
                            modifier = Modifier
                                .background(tagBgColor, RoundedCornerShape(4.dp))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
            }
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                if (todo.description.isNotBlank()) {
                    Text(
                        text = "Description",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = todo.description,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Event,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Due: $displayDate",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))

                // Explicit Mark as Complete Prompt & Button
                Text(
                    text = if (todo.isCompleted) "Status: Completed" else "Do you want to mark this task as complete?",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Button(
                    onClick = {
                        onToggleCompletion()
                        onDismiss()
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (todo.isCompleted) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.primary
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(
                        imageVector = if (todo.isCompleted) Icons.AutoMirrored.Filled.Undo else Icons.Default.Check,
                        contentDescription = null
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (todo.isCompleted) "Mark as Incomplete" else "Yes, Mark Task Complete",
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        },
        confirmButton = {
            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                TextButton(onClick = onDelete) {
                    Icon(Icons.Default.Delete, contentDescription = null, tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Delete", color = MaterialTheme.colorScheme.error)
                }
                TextButton(onClick = onEdit) {
                    Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Edit")
                }
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Close")
            }
        }
    )
}
