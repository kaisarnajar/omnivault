package app.taskvault.ui.ledger

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import app.taskvault.data.local.LedgerTransactionEntity
import app.taskvault.ui.components.GlassCard
import app.taskvault.ui.components.OmniVaultBackground
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.abs

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LedgerDetailScreen(
    viewModel: LedgerViewModel,
    personId: String,
    personName: String,
    onNavigateBack: () -> Unit
) {
    LaunchedEffect(personId) {
        viewModel.selectPerson(personId)
    }

    val transactions by viewModel.selectedPersonTransactions.collectAsState()
    val netBalance = transactions.sumOf { if (it.isCredit) it.amount else -it.amount }

    var showAddDialog by remember { mutableStateOf(false) }

    val balanceColor = if (netBalance >= 0) Color(0xFF10B981) else Color(0xFFEF4444)
    val balanceLabel = if (netBalance >= 0) "owes you" else "you owe"

    OmniVaultBackground {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(personName, fontWeight = FontWeight.Bold) },
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
                    Icon(Icons.Default.Add, contentDescription = "Add Transaction", tint = Color.White)
                }
            },
            containerColor = Color.Transparent
        ) { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            ) {
                // Balance Header
                GlassCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = personName,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = balanceLabel,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = NumberFormat.getCurrencyInstance().format(abs(netBalance)),
                            style = MaterialTheme.typography.displaySmall,
                            fontWeight = FontWeight.Bold,
                            color = balanceColor
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "${transactions.size} transactions",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Transactions List
                if (transactions.isEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            "No transactions yet.\nTap + to add one.",
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                } else {
                    LazyColumn(
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(transactions, key = { it.id }) { txn ->
                            TransactionItem(
                                transaction = txn,
                                onDelete = { viewModel.deleteTransaction(txn.id) }
                            )
                        }
                    }
                }
            }
        }

        if (showAddDialog) {
            AddTransactionDialog(
                personName = personName,
                onDismiss = { showAddDialog = false },
                onSave = { amount, description, isCredit ->
                    viewModel.addTransaction(personId, amount, description, isCredit)
                    showAddDialog = false
                }
            )
        }
    }
}

@Composable
fun TransactionItem(transaction: LedgerTransactionEntity, onDelete: () -> Unit) {
    val isCredit = transaction.isCredit
    val icon = if (isCredit) Icons.Default.ArrowDownward else Icons.Default.ArrowUpward
    val color = if (isCredit) Color(0xFF10B981) else Color(0xFFEF4444)
    val label = if (isCredit) "Credit (they owe you)" else "Debit (you owe)"
    val dateStr = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(Date(transaction.timestamp))

    GlassCard(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(color.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = label, tint = color, modifier = Modifier.size(20.dp))
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = transaction.description.ifBlank { label },
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = dateStr,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Text(
                text = (if (isCredit) "+" else "-") + NumberFormat.getCurrencyInstance().format(transaction.amount),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = color
            )

            IconButton(onClick = onDelete) {
                Icon(Icons.Default.Delete, contentDescription = "Delete", tint = MaterialTheme.colorScheme.outline)
            }
        }
    }
}

@Composable
fun AddTransactionDialog(
    personName: String,
    onDismiss: () -> Unit,
    onSave: (Double, String, Boolean) -> Unit
) {
    var amount by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var isCredit by remember { mutableStateOf(true) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Transaction") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Text(
                    text = "For: $personName",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                OutlinedTextField(
                    value = amount,
                    onValueChange = { amount = it },
                    label = { Text("Amount") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description (Optional)") },
                    modifier = Modifier.fillMaxWidth()
                )

                Text("Type", style = MaterialTheme.typography.labelMedium)

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    FilterChip(
                        selected = isCredit,
                        onClick = { isCredit = true },
                        label = { Text("They owe me") },
                        modifier = Modifier.weight(1f)
                    )
                    FilterChip(
                        selected = !isCredit,
                        onClick = { isCredit = false },
                        label = { Text("I owe them") },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    val parsedAmount = amount.toDoubleOrNull()
                    if (parsedAmount != null && parsedAmount > 0) {
                        onSave(parsedAmount, description, isCredit)
                    }
                }
            ) { Text("Save") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}
