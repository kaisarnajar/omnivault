package app.taskvault.ui.tools

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Bedtime
import androidx.compose.material.icons.filled.Bookmark
import app.taskvault.ui.components.CategoryBottomBar
import app.taskvault.ui.components.ToolCategory
import app.taskvault.ui.components.pressScale
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.taskvault.ui.components.OmniVaultBackground
import app.taskvault.ui.theme.*

data class ToolItem(
    val title: String,
    val description: String,
    val details: String,
    val icon: ImageVector,
    val route: String,
    val accentColor: Color,
    val category: ToolCategory,
    val isVault: Boolean = false
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ToolsScreen(
    onNavigateToTool: (String) -> Unit,
    onNavigateToProfile: () -> Unit
) {
    var selectedCategory by remember { mutableStateOf(ToolCategory.ALL) }

    val tools = remember {
        listOf(
            ToolItem(
                title = "Tasks",
                description = "Manage your to-dos",
                details = "Organize tasks with Eisenhower matrix and track deadlines.",
                icon = Icons.Default.CheckCircle,
                route = "todo_list",
                accentColor = TasksAccent,
                category = ToolCategory.PRODUCTIVITY
            ),
            ToolItem(
                title = "Calendar",
                description = "Schedule events",
                details = "View upcoming events and organize your daily schedule.",
                icon = Icons.Default.DateRange,
                route = "calendar",
                accentColor = CalendarAccent,
                category = ToolCategory.PRODUCTIVITY
            ),
            ToolItem(
                title = "Pomodoro",
                description = "Focus mode",
                details = "Boost productivity with timed focus sessions and breaks.",
                icon = Icons.Default.Timer,
                route = "pomodoro",
                accentColor = PomodoroAccent,
                category = ToolCategory.PRODUCTIVITY
            ),
            ToolItem(
                title = "Notes",
                description = "Quick thoughts",
                details = "Jot down ideas, meeting minutes, and personal reflections.",
                icon = Icons.Default.Edit,
                route = "notes_list",
                accentColor = NotesAccent,
                category = ToolCategory.PRODUCTIVITY
            ),
            ToolItem(
                title = "Expenses",
                description = "Track spending",
                details = "Monitor your daily expenses and categorize your financial outflow.",
                icon = Icons.Default.Build,
                route = "expense_tracker",
                accentColor = ExpensesAccent,
                category = ToolCategory.FINANCE
            ),
            ToolItem(
                title = "Secret Vault",
                description = "Encrypted",
                details = "Securely store sensitive passwords, API keys, and private data.",
                icon = Icons.Default.Lock,
                route = "vault_list",
                accentColor = VaultAccent,
                category = ToolCategory.FINANCE,
                isVault = true
            ),
            ToolItem(
                title = "QR Scanner",
                description = "Scan Codes",
                details = "Instantly scan and read QR codes and barcodes.",
                icon = Icons.Default.QrCodeScanner,
                route = "qr_scanner",
                accentColor = QRAccent,
                category = ToolCategory.UTILITIES
            ),
            ToolItem(
                title = "Ledger",
                description = "Credit & Debit",
                details = "Track who owes you and who you owe with transaction history.",
                icon = Icons.Default.AccountBalanceWallet,
                route = "ledger_list",
                accentColor = LedgerAccent,
                category = ToolCategory.FINANCE
            ),
            ToolItem(
                title = "Mood Journal",
                description = "Daily Reflections",
                details = "Track your daily moods with emojis, notes, and timeline logs.",
                icon = Icons.Default.Face,
                route = "mood_journal",
                accentColor = MoodAccent,
                category = ToolCategory.HEALTH
            ),
            ToolItem(
                title = "Bookmarks",
                description = "Save & Categorize",
                details = "Save web links, articles, and URLs to read or reference later.",
                icon = Icons.Default.Bookmark,
                route = "bookmarks",
                accentColor = BookmarkAccent,
                category = ToolCategory.UTILITIES
            ),
            ToolItem(
                title = "Fitness Tracker",
                description = "Workouts & Sports",
                details = "Log workouts with target muscles, running distance, and sports.",
                icon = Icons.Default.FitnessCenter,
                route = "fitness",
                accentColor = FitnessAccent,
                category = ToolCategory.HEALTH
            ),
            ToolItem(
                title = "Sleep Log",
                description = "Bedtime & Quality",
                details = "Track sleep schedules, nightly duration, and sleep quality ratings.",
                icon = Icons.Default.Bedtime,
                route = "sleep",
                accentColor = SleepAccent,
                category = ToolCategory.HEALTH
            )
        )
    }

    val filteredTools = remember(tools, selectedCategory) {
        if (selectedCategory == ToolCategory.ALL) tools
        else tools.filter { it.category == selectedCategory }
    }

    val categoryCounts = remember(tools) {
        ToolCategory.values().associateWith { cat ->
            if (cat == ToolCategory.ALL) tools.size
            else tools.count { it.category == cat }
        }
    }

    OmniVaultBackground {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            "OmniVault",
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 26.sp
                        )
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color.Transparent,
                        titleContentColor = MaterialTheme.colorScheme.onSurface,
                        actionIconContentColor = MaterialTheme.colorScheme.onSurface
                    ),
                    actions = {
                        Box(
                            modifier = Modifier
                                .padding(end = 12.dp)
                                .size(42.dp)
                                .clip(CircleShape)
                                .background(
                                    Brush.linearGradient(
                                        colors = listOf(
                                            Color(0xFF6366F1), // Indigo
                                            Color(0xFF8B5CF6)  // Violet
                                        )
                                    )
                                )
                                .pressScale()
                                .clickable(onClick = onNavigateToProfile),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = "Profile",
                                tint = Color.White,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                    }
                )
            },
            bottomBar = {
                CategoryBottomBar(
                    selectedCategory = selectedCategory,
                    onCategorySelected = { selectedCategory = it },
                    categoryCounts = categoryCounts
                )
            },
            containerColor = Color.Transparent
        ) { padding ->
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(14.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp),
                contentPadding = PaddingValues(bottom = 16.dp)
            ) {
                items(filteredTools.size) { index ->
                    val tool = filteredTools[index]
                    ToolCard(tool = tool, onClick = { onNavigateToTool(tool.route) })
                }
            }
        }
    }
}

@Composable
fun ToolCard(tool: ToolItem, onClick: () -> Unit) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.96f else 1f,
        animationSpec = tween(durationMillis = 100),
        label = "cardScale"
    )

    val cardShape = RoundedCornerShape(20.dp)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(0.80f)
            .scale(scale)
            .shadow(
                elevation = 8.dp,
                shape = cardShape,
                ambientColor = tool.accentColor.copy(alpha = 0.15f),
                spotColor = tool.accentColor.copy(alpha = 0.2f)
            )
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            ),
        shape = cardShape,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            // Subtle accent gradient at the top of the card
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(4.dp)
                    .background(
                        Brush.horizontalGradient(
                            colors = listOf(
                                tool.accentColor,
                                tool.accentColor.copy(alpha = 0.4f)
                            )
                        )
                    )
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(14.dp)
            ) {
                // Icon circle
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(tool.accentColor.copy(alpha = 0.12f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = tool.icon,
                        contentDescription = tool.title,
                        tint = tool.accentColor,
                        modifier = Modifier.size(22.dp)
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Title
                Text(
                    text = tool.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1
                )

                Spacer(modifier = Modifier.height(2.dp))

                // Subtitle
                Text(
                    text = tool.description,
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = tool.accentColor,
                    maxLines = 1
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Details
                Text(
                    text = tool.details,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.55f),
                    maxLines = 3,
                    lineHeight = 16.sp
                )
            }
        }
    }
}
