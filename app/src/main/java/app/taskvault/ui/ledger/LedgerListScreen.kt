package app.taskvault.ui.ledger

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import app.taskvault.data.local.LedgerPersonEntity
import app.taskvault.data.local.LedgerTransactionEntity
import app.taskvault.ui.components.FeatureHeaderCard
import app.taskvault.ui.components.GlassCard
import app.taskvault.ui.components.OmniVaultBackground
import app.taskvault.ui.components.pressScale
import app.taskvault.ui.components.gradientBackground
import java.text.NumberFormat
import kotlin.math.abs

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LedgerListScreen(
    viewModel: LedgerViewModel,
    onNavigateBack: () -> Unit,
    onNavigateToPerson: (String) -> Unit
) {
    val persons by viewModel.persons.collectAsState()
    val allTransactions by viewModel.allTransactions.collectAsState()
    val grandTotal by viewModel.grandTotal.collectAsState()

    var showAddDialog by remember { mutableStateOf(false) }

    OmniVaultBackground {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Credit / Debit", fontWeight = FontWeight.Bold) },
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
                    Icon(Icons.Default.Add, contentDescription = "Add Person", tint = Color.White)
                }
            },
            containerColor = Color.Transparent
        ) { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            ) {
                // Grand Total Header
                FeatureHeaderCard(
                    title = if (grandTotal >= 0) "Others Owe You" else "You Owe Others",
                    subtitle = "${persons.size} people",
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = NumberFormat.getCurrencyInstance().format(abs(grandTotal)),
                        style = MaterialTheme.typography.displaySmall,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Persons List
                if (persons.isEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            "No people added yet.\nTap + to add someone.",
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                } else {
                    LazyColumn(
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(persons, key = { it.id }) { person ->
                            val balance = viewModel.getBalanceForPerson(person.id, allTransactions)
                            PersonItem(
                                person = person,
                                balance = balance,
                                onClick = { onNavigateToPerson(person.id) },
                                onDelete = { viewModel.deletePerson(person.id) }
                            )
                        }
                    }
                }
            }
        }

        if (showAddDialog) {
            AddPersonDialog(
                onDismiss = { showAddDialog = false },
                onSave = { name ->
                    viewModel.addPerson(name)
                    showAddDialog = false
                }
            )
        }
    }
}

@Composable
fun PersonItem(
    person: LedgerPersonEntity,
    balance: Double,
    onClick: () -> Unit,
    onDelete: () -> Unit
) {
    val balanceColor = if (balance >= 0) Color(0xFF10B981) else Color(0xFFEF4444) // Green / Red
    val balanceLabel = if (balance >= 0) "owes you" else "you owe"

    GlassCard(modifier = Modifier.fillMaxWidth().pressScale().clickable { onClick() }) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.Person,
                    contentDescription = person.name,
                    tint = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = person.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = balanceLabel,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Text(
                text = NumberFormat.getCurrencyInstance().format(abs(balance)),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = balanceColor
            )

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
fun AddPersonDialog(onDismiss: () -> Unit, onSave: (String) -> Unit) {
    var name by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Person") },
        text = {
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Person Name") },
                modifier = Modifier.fillMaxWidth()
            )
        },
        confirmButton = {
            TextButton(onClick = { if (name.isNotBlank()) onSave(name.trim()) }) {
                Text("Add")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}
