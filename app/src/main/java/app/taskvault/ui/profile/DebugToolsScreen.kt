package app.taskvault.ui.profile

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Bedtime
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BugReport
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.RocketLaunch
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.taskvault.ui.components.GlassCard
import app.taskvault.ui.components.OmniVaultBackground
import app.taskvault.ui.components.pressScale
import app.taskvault.ui.theme.*
import kotlinx.coroutines.launch

data class DebugToolCategory(
    val id: String,
    val name: String,
    val description: String,
    val icon: ImageVector,
    val color: Color,
    val seedAction: suspend () -> Unit
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DebugToolsScreen(
    viewModel: ProfileViewModel,
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var isSeedingAll by remember { mutableStateOf(false) }

    val categories = remember {
        listOf(
            DebugToolCategory("tasks", "Tasks & To-Dos", "Inject sample priorities & due dates", Icons.Default.CheckCircle, TasksAccent) { viewModel.seedTasks() },
            DebugToolCategory("calendar", "Calendar Events", "Inject sample schedule & meetings", Icons.Default.CalendarMonth, TasksAccent) { viewModel.seedTasks() },
            DebugToolCategory("notes", "Notes", "Inject sample notes & reflections", Icons.Default.Edit, NotesAccent) { viewModel.seedNotes() },
            DebugToolCategory("pomodoro", "Pomodoro Focus History", "Inject focus sessions & stats", Icons.Default.PlayArrow, PomodoroAccent) { viewModel.seedPomodoro() },
            DebugToolCategory("expenses", "Expense Tracker", "Inject daily spending logs & categories", Icons.Default.ShoppingCart, ExpensesAccent) { viewModel.seedExpenses() },
            DebugToolCategory("vault", "Secret Vault", "Inject sample password accounts & keys", Icons.Default.Lock, VaultAccent) { viewModel.seedSecrets() },
            DebugToolCategory("ledger", "Credit / Debit Ledger", "Inject debts, loans & transactions", Icons.Default.AccountBalanceWallet, LedgerAccent) { viewModel.seedLedger() },
            DebugToolCategory("mood", "Mood Journal", "Inject mood entries & reflections", Icons.Default.Face, MoodAccent) { viewModel.seedMood() },
            DebugToolCategory("bookmarks", "Bookmarks", "Inject web links & reading articles", Icons.Default.Bookmark, BookmarkAccent) { viewModel.seedBookmarks() },
            DebugToolCategory("fitness", "Fitness Tracker", "Inject workout logs & running activities", Icons.Default.FitnessCenter, FitnessAccent) { viewModel.seedFitness() },
            DebugToolCategory("sleep", "Sleep Log", "Inject sleep schedules & quality ratings", Icons.Default.Bedtime, SleepAccent) { viewModel.seedSleep() }
        )
    }

    OmniVaultBackground {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Debug Tools", fontWeight = FontWeight.Bold) },
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
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Top Batch Inject Card
                item {
                    GlassCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .pressScale(),
                        shape = RoundedCornerShape(20.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(18.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(44.dp)
                                        .clip(CircleShape)
                                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.BugReport,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                }

                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "Sample Data Generator",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = "Inject test data across all 11 features with 1 tap",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }

                            Button(
                                onClick = {
                                    isSeedingAll = true
                                    Toast.makeText(context, "Populating sample data across all 11 features...", Toast.LENGTH_SHORT).show()
                                    viewModel.seedAllSampleData { success ->
                                        isSeedingAll = false
                                        if (success) {
                                            Toast.makeText(context, "✅ All 11 features populated successfully!", Toast.LENGTH_LONG).show()
                                        } else {
                                            Toast.makeText(context, "❌ Error injecting sample data", Toast.LENGTH_SHORT).show()
                                        }
                                    }
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(48.dp),
                                enabled = !isSeedingAll,
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                if (isSeedingAll) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(20.dp),
                                        color = MaterialTheme.colorScheme.onPrimary,
                                        strokeWidth = 2.dp
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Injecting Data...", fontWeight = FontWeight.Bold)
                                } else {
                                    Icon(Icons.Default.RocketLaunch, contentDescription = null, modifier = Modifier.size(18.dp))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Inject All Sample Data", fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }

                // Header Label
                item {
                    Text(
                        text = "INDIVIDUAL FEATURE GENERATORS (11 TOOLS)",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        letterSpacing = 1.sp,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }

                // 11 Feature Items
                items(categories, key = { it.id }) { cat ->
                    GlassCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .pressScale()
                            .clickable {
                                scope.launch {
                                    Toast.makeText(context, "Injecting ${cat.name} data...", Toast.LENGTH_SHORT).show()
                                    cat.seedAction()
                                    Toast.makeText(context, "✅ ${cat.name} sample data added!", Toast.LENGTH_SHORT).show()
                                }
                            },
                        shape = RoundedCornerShape(18.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(44.dp)
                                    .clip(CircleShape)
                                    .background(cat.color.copy(alpha = 0.2f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = cat.icon,
                                    contentDescription = cat.name,
                                    tint = cat.color,
                                    modifier = Modifier.size(22.dp)
                                )
                            }

                            Spacer(modifier = Modifier.width(14.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = cat.name,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = cat.description,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            Button(
                                onClick = {
                                    scope.launch {
                                        Toast.makeText(context, "Injecting ${cat.name} data...", Toast.LENGTH_SHORT).show()
                                        cat.seedAction()
                                        Toast.makeText(context, "✅ ${cat.name} sample data added!", Toast.LENGTH_SHORT).show()
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = cat.color.copy(alpha = 0.15f),
                                    contentColor = cat.color
                                ),
                                shape = RoundedCornerShape(10.dp),
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                            ) {
                                Text("Inject", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            }
                        }
                    }
                }
            }
        }
    }
}
