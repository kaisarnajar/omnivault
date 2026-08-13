package app.taskvault.ui.tools

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.taskvault.ui.components.OmniVaultBackground
import app.taskvault.ui.theme.VaultAccent

data class ToolItem(
    val title: String,
    val description: String,
    val details: String,
    val icon: ImageVector,
    val route: String,
    val isVault: Boolean = false
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ToolsScreen(
    onNavigateToTool: (String) -> Unit,
    onNavigateToProfile: () -> Unit
) {
    val tools = listOf(
        ToolItem(
            title = "Tasks",
            description = "Manage your to-dos",
            details = "Organize tasks with Eisenhower matrix and track deadlines.",
            icon = Icons.Default.CheckCircle,
            route = "todo_list"
        ),
        ToolItem(
            title = "Calendar",
            description = "Schedule events",
            details = "View upcoming events and organize your daily schedule.",
            icon = Icons.Default.DateRange,
            route = "calendar"
        ),
        ToolItem(
            title = "Pomodoro",
            description = "Focus mode",
            details = "Boost productivity with timed focus sessions and breaks.",
            icon = Icons.Default.Timer,
            route = "pomodoro"
        ),
        ToolItem(
            title = "Notes",
            description = "Quick thoughts",
            details = "Jot down ideas, meeting minutes, and personal reflections.",
            icon = Icons.Default.Edit,
            route = "notes_list"
        ),
        ToolItem(
            title = "Expenses",
            description = "Track spending",
            details = "Monitor your daily expenses and categorize your financial outflow.",
            icon = Icons.Default.Build,
            route = "expense_tracker"
        ),
        ToolItem(
            title = "Secret Vault",
            description = "Encrypted",
            details = "Securely store sensitive passwords, API keys, and private data.",
            icon = Icons.Default.Lock,
            route = "vault_list",
            isVault = true
        ),
        ToolItem(
            title = "QR Scanner",
            description = "Scan Codes",
            details = "Instantly scan and read QR codes and barcodes.",
            icon = Icons.Default.QrCodeScanner,
            route = "qr_scanner"
        )
    )

    OmniVaultBackground {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("OmniVault", fontWeight = FontWeight.Bold) },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color.Transparent,
                        titleContentColor = MaterialTheme.colorScheme.onSurface,
                        actionIconContentColor = MaterialTheme.colorScheme.onSurface
                    ),
                    actions = {
                        IconButton(onClick = onNavigateToProfile) {
                            Icon(imageVector = Icons.Default.Person, contentDescription = "Profile")
                        }
                    }
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
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(tools.size) { index ->
                    val tool = tools[index]
                    ToolCard(tool = tool, onClick = { onNavigateToTool(tool.route) })
                }
            }
        }
    }
}

@Composable
fun ToolCard(tool: ToolItem, onClick: () -> Unit) {
    val customFill = if (tool.isVault) VaultAccent.copy(alpha = 0.15f) else null
    val customBorder = if (tool.isVault) VaultAccent.copy(alpha = 0.5f) else null

    app.taskvault.ui.components.GlassCard(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(0.85f) // Taller aspect ratio for Stitch design
            .clickable { onClick() },
        customFill = customFill,
        customBorder = customBorder
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween, // Push content to top and bottom
            horizontalAlignment = Alignment.Start // Left align text
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(
                        if (tool.isVault) {
                            VaultAccent.copy(alpha = 0.2f)
                        } else {
                            MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
                        }
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = tool.icon,
                    contentDescription = tool.title,
                    tint = if (tool.isVault) VaultAccent else MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.size(24.dp)
                )
            }

            Column {
                Text(
                    text = tool.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = tool.description,
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = tool.details,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                    maxLines = 3,
                    lineHeight = 14.sp
                )
            }
        }
    }
}
