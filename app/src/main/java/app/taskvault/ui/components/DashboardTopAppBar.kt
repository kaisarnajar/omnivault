package app.taskvault.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.Timer
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import app.taskvault.domain.UserProfile

@Composable
fun DashboardTopAppBar(
    userProfile: UserProfile?,
    onThemeChange: (Boolean?) -> Unit,
    onLogout: () -> Unit,
    onNavigateToPomodoro: () -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    var themeMenuExpanded by remember { mutableStateOf(false) }

    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .height(80.dp)
                .background(MaterialTheme.colorScheme.surface)
                .padding(horizontal = 20.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Column {
                Text(
                    text = "Welcome back,",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Text(
                    text = userProfile?.displayName ?: "TaskVault User",
                    style =
                        MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface,
                        ),
                )
            }
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onNavigateToPomodoro) {
                Icon(
                    imageVector = Icons.Outlined.Timer,
                    contentDescription = "Pomodoro Timer",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            Box {
                IconButton(onClick = { expanded = true }) {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = "Settings",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
            ) {
                DropdownMenuItem(
                    text = { Text("Theme") },
                    onClick = {
                        expanded = false
                        themeMenuExpanded = true
                    },
                )
                DropdownMenuItem(
                    text = { Text("Logout", color = MaterialTheme.colorScheme.error) },
                    onClick = {
                        expanded = false
                        onLogout()
                    },
                )
            }

            // Sub-menu for Theme
            DropdownMenu(
                expanded = themeMenuExpanded,
                onDismissRequest = { themeMenuExpanded = false },
            ) {
                DropdownMenuItem(
                    text = { Text("System Default") },
                    onClick = {
                        onThemeChange(null)
                        themeMenuExpanded = false
                    },
                )
                DropdownMenuItem(
                    text = { Text("Light Mode") },
                    onClick = {
                        onThemeChange(false)
                        themeMenuExpanded = false
                    },
                )
                DropdownMenuItem(
                    text = { Text("Dark Mode") },
                    onClick = {
                        onThemeChange(true)
                        themeMenuExpanded = false
                    },
                )
                }
            }
        }
    }
}
