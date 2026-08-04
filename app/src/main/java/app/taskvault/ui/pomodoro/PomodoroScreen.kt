package app.taskvault.ui.pomodoro

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.util.Locale
import app.taskvault.ui.components.gradientBackground
import app.taskvault.domain.PomodoroMode
import androidx.compose.material.icons.filled.Settings
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PomodoroScreen(
    viewModel: PomodoroViewModel,
    onNavigateBack: () -> Unit
) {
    val timeRemaining by viewModel.timeRemaining.collectAsState()
    val isPlaying by viewModel.isPlaying.collectAsState()
    val currentMode by viewModel.currentMode.collectAsState()
    
    var showSettingsDialog by remember { mutableStateOf(false) }

    // Hardcode total time per mode for progress animation
    val totalTime = when (currentMode) {
        PomodoroMode.POMODORO -> viewModel.pomodoroDuration.collectAsState(initial = 25).value * 60f
        PomodoroMode.SHORT_BREAK -> viewModel.shortBreakDuration.collectAsState(initial = 5).value * 60f
        PomodoroMode.LONG_BREAK -> viewModel.longBreakDuration.collectAsState(initial = 15).value * 60f
    }
    val progress by animateFloatAsState(
        targetValue = timeRemaining / totalTime,
        label = "progress"
    )

    val minutes = timeRemaining / 60
    val seconds = timeRemaining % 60
    val timeString = String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Focus Time", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { showSettingsDialog = true }) {
                        Icon(Icons.Default.Settings, contentDescription = "Settings")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            // Mode Selector
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                PomodoroMode.values().forEach { mode ->
                    val isSelected = currentMode == mode
                    FilterChip(
                        selected = isSelected,
                        onClick = { viewModel.setMode(mode) },
                        label = { Text(mode.displayName) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                            selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(48.dp))

            // Circular Timer
            Box(
                modifier = Modifier
                    .size(280.dp),
                contentAlignment = Alignment.Center
            ) {
                val backgroundColor = MaterialTheme.colorScheme.surfaceVariant
                val primaryColor = MaterialTheme.colorScheme.primary

                Canvas(modifier = Modifier.fillMaxSize()) {
                    drawArc(
                        color = backgroundColor,
                        startAngle = 0f,
                        sweepAngle = 360f,
                        useCenter = false,
                        style = Stroke(width = 16.dp.toPx(), cap = StrokeCap.Round)
                    )
                    drawArc(
                        color = primaryColor,
                        startAngle = -90f,
                        sweepAngle = 360f * progress,
                        useCenter = false,
                        style = Stroke(width = 16.dp.toPx(), cap = StrokeCap.Round)
                    )
                }

                Text(
                    text = timeString,
                    style = MaterialTheme.typography.displayLarge.copy(
                        fontSize = 64.sp,
                        fontWeight = FontWeight.Bold
                    ),
                    color = MaterialTheme.colorScheme.onBackground
                )
            }

            Spacer(modifier = Modifier.height(64.dp))

            // Controls
            Row(
                horizontalArrangement = Arrangement.spacedBy(24.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = { viewModel.resetTimer() },
                    modifier = Modifier
                        .size(56.dp)
                        .background(MaterialTheme.colorScheme.surfaceVariant, CircleShape)
                ) {
                    Icon(
                        Icons.Default.Refresh,
                        contentDescription = "Reset",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(28.dp)
                    )
                }

                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .gradientBackground(CircleShape)
                        .clickable { viewModel.toggleTimer() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                        contentDescription = if (isPlaying) "Pause" else "Play",
                        modifier = Modifier.size(40.dp),
                        tint = androidx.compose.ui.graphics.Color.White
                    )
                }
            }
        }
    }

    if (showSettingsDialog) {
        val currentPomo by viewModel.pomodoroDuration.collectAsState(initial = 25)
        val currentShort by viewModel.shortBreakDuration.collectAsState(initial = 5)
        val currentLong by viewModel.longBreakDuration.collectAsState(initial = 15)

        var pomoInput by remember { mutableStateOf(currentPomo.toString()) }
        var shortInput by remember { mutableStateOf(currentShort.toString()) }
        var longInput by remember { mutableStateOf(currentLong.toString()) }

        AlertDialog(
            onDismissRequest = { showSettingsDialog = false },
            title = { Text("Timer Settings") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = pomoInput,
                        onValueChange = { pomoInput = it },
                        label = { Text("Pomodoro (minutes)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )
                    OutlinedTextField(
                        value = shortInput,
                        onValueChange = { shortInput = it },
                        label = { Text("Short Break (minutes)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )
                    OutlinedTextField(
                        value = longInput,
                        onValueChange = { longInput = it },
                        label = { Text("Long Break (minutes)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    val p = pomoInput.toIntOrNull() ?: 25
                    val s = shortInput.toIntOrNull() ?: 5
                    val l = longInput.toIntOrNull() ?: 15
                    viewModel.saveSettings(p, s, l)
                    showSettingsDialog = false
                }) {
                    Text("Save")
                }
            },
            dismissButton = {
                TextButton(onClick = { showSettingsDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}
