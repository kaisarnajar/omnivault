package app.taskvault.ui.profile

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Bedtime
import androidx.compose.material.icons.filled.Bookmark
import app.taskvault.ui.theme.SleepAccent
import androidx.compose.material.icons.filled.BugReport
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PlayArrow
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
import app.taskvault.ui.theme.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel,
    onLogout: () -> Unit,
    onThemeChange: (Boolean?) -> Unit,
    onNavigateBack: () -> Unit
) {
    val userProfile by viewModel.userProfile.collectAsState()
    val uiState by viewModel.uiState.collectAsState()

    var isEditing by remember { mutableStateOf(false) }
    var displayName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var showDebugDialog by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    // Initialize fields when profile is loaded
    LaunchedEffect(userProfile) {
        if (!isEditing) {
            displayName = userProfile?.displayName ?: ""
            email = userProfile?.email ?: ""
        }
    }

    LaunchedEffect(uiState) {
        if (uiState is ProfileUiState.Success) {
            isEditing = false
            viewModel.resetState()
        }
    }

    OmniVaultBackground {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(if (isEditing) "Edit Profile" else "Profile", fontWeight = FontWeight.Bold) },
                    navigationIcon = {
                        IconButton(onClick = {
                            if (isEditing) {
                                isEditing = false
                                displayName = userProfile?.displayName ?: ""
                                email = userProfile?.email ?: ""
                                viewModel.resetState()
                            } else {
                                onNavigateBack()
                            }
                        }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color.Transparent
                    )
                )
            },
            containerColor = Color.Transparent
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(24.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                // Avatar
                Box(
                    modifier = Modifier
                        .size(110.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f))
                        .border(2.dp, MaterialTheme.colorScheme.primary, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = null,
                        modifier = Modifier.size(56.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                }

                if (isEditing) {
                    OutlinedTextField(
                        value = displayName,
                        onValueChange = { displayName = it },
                        label = { Text("Display Name") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = { Text("Email") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true
                    )
                } else {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = userProfile?.displayName ?: "No Name",
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = userProfile?.email ?: "No Email",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                if (uiState is ProfileUiState.Error) {
                    Text(
                        text = (uiState as ProfileUiState.Error).message,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                if (isEditing) {
                    app.taskvault.ui.components.GradientButton(
                        text = "Save Profile",
                        onClick = { viewModel.updateProfile(displayName, email) },
                        modifier = Modifier.fillMaxWidth().height(52.dp),
                        enabled = uiState !is ProfileUiState.Loading && displayName.isNotBlank() && email.isNotBlank(),
                        isLoading = uiState is ProfileUiState.Loading
                    )
                } else {
                    app.taskvault.ui.components.GradientButton(
                        text = "Edit Profile",
                        onClick = { isEditing = true },
                        modifier = Modifier.fillMaxWidth().height(52.dp)
                    )

                    // Theme selector buttons
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        OutlinedButton(
                            onClick = { onThemeChange(null) },
                            modifier = Modifier.weight(1f).height(48.dp),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(Icons.Default.Palette, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(Modifier.width(6.dp))
                            Text("Auto", fontSize = 13.sp)
                        }
                        OutlinedButton(
                            onClick = { onThemeChange(false) },
                            modifier = Modifier.weight(1f).height(48.dp),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("Light", fontSize = 13.sp)
                        }
                        OutlinedButton(
                            onClick = { onThemeChange(true) },
                            modifier = Modifier.weight(1f).height(48.dp),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("Dark", fontSize = 13.sp)
                        }
                    }

                    // Logout Button
                    Button(
                        onClick = onLogout,
                        modifier = Modifier.fillMaxWidth().height(52.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        @Suppress("DEPRECATION")
                        Icon(Icons.Default.ExitToApp, contentDescription = "Logout")
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Logout", fontWeight = FontWeight.Bold)
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Separate Button for Debug Tools
                    GlassCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { showDebugDialog = true },
                        shape = RoundedCornerShape(16.dp)
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
                                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.BugReport,
                                    contentDescription = "Debug Tools",
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }
                            Spacer(modifier = Modifier.width(16.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Debug Tools",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "Generate sample data for testing",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Icon(
                                imageVector = Icons.Default.ChevronRight,
                                contentDescription = "Open",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }

        // Dedicated Debug Tools Dialog / Panel
        if (showDebugDialog) {
            DebugToolsDialog(
                onDismiss = { showDebugDialog = false },
                onSeed = { featureName, seedAction ->
                    scope.launch {
                        Toast.makeText(context, "Started injecting $featureName data...", Toast.LENGTH_SHORT).show()
                        seedAction()
                        Toast.makeText(context, "Data injection completed for $featureName!", Toast.LENGTH_SHORT).show()
                    }
                },
                viewModel = viewModel
            )
        }
    }
}

data class DebugSeedItem(
    val name: String,
    val icon: ImageVector,
    val color: Color,
    val seedAction: suspend () -> Unit
)

@Composable
fun DebugToolsDialog(
    onDismiss: () -> Unit,
    onSeed: (String, suspend () -> Unit) -> Unit,
    viewModel: ProfileViewModel
) {
    val seedItems = listOf(
        DebugSeedItem("Tasks", Icons.Default.CheckCircle, TasksAccent) { viewModel.seedTasks() },
        DebugSeedItem("Notes", Icons.Default.Edit, NotesAccent) { viewModel.seedNotes() },
        DebugSeedItem("Pomodoro", Icons.Default.PlayArrow, PomodoroAccent) { viewModel.seedPomodoro() },
        DebugSeedItem("Expenses", Icons.Default.ShoppingCart, ExpensesAccent) { viewModel.seedExpenses() },
        DebugSeedItem("Secret Vault", Icons.Default.Lock, VaultAccent) { viewModel.seedSecrets() },
        DebugSeedItem("Credit/Debit", Icons.Default.AccountBalanceWallet, LedgerAccent) { viewModel.seedLedger() },
        DebugSeedItem("Mood Journal", Icons.Default.Face, MoodAccent) { viewModel.seedMood() },
        DebugSeedItem("Bookmarks", Icons.Default.Bookmark, BookmarkAccent) { viewModel.seedBookmarks() },
        DebugSeedItem("Fitness Tracker", Icons.Default.FitnessCenter, FitnessAccent) { viewModel.seedFitness() },
        DebugSeedItem("Sleep Log", Icons.Default.Bedtime, SleepAccent) { viewModel.seedSleep() }
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Default.BugReport,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(28.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text("Debug Tools", fontWeight = FontWeight.Bold, fontSize = 20.sp)
                    Text(
                        "Tap a feature to inject sample data",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 420.dp)
                    .verticalScroll(rememberScrollState())
                    .padding(vertical = 4.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                seedItems.forEach { item ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                onSeed(item.name, item.seedAction)
                            },
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = item.color.copy(alpha = 0.12f)
                        )
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(item.color.copy(alpha = 0.25f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = item.icon,
                                    contentDescription = item.name,
                                    tint = item.color,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(14.dp))
                            Text(
                                text = "Inject ${item.name} Data",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Close", fontWeight = FontWeight.Bold)
            }
        }
    )
}
