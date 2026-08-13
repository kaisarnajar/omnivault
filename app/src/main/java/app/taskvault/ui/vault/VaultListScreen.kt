package app.taskvault.ui.vault

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.fragment.app.FragmentActivity
import app.taskvault.data.local.SecretEntity
import app.taskvault.ui.components.OmniVaultBackground
import app.taskvault.util.BiometricHelper

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VaultListScreen(
    viewModel: VaultViewModel,
    onNavigateToAddEdit: () -> Unit,
    onNavigateBack: () -> Unit
) {
    val secrets by viewModel.secrets.collectAsState()
    val context = LocalContext.current
    val activity = context as? FragmentActivity
    var isAuthenticated by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        if (!isAuthenticated && activity != null) {
            if (BiometricHelper.isBiometricReady(activity)) {
                BiometricHelper.authenticate(
                    activity = activity,
                    title = "Unlock Vault",
                    subtitle = "Verify your identity to access secrets",
                    onSuccess = { isAuthenticated = true },
                    onError = { msg ->
                        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                        onNavigateBack() // Boot them out if they fail
                    }
                )
            } else {
                // If biometrics not set up, let them in (or we could show an error)
                // For MVP, we let them in or require a PIN. The API falls back to PIN usually.
                isAuthenticated = true
            }
        }
    }

    OmniVaultBackground {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Secret Vault", fontWeight = FontWeight.Bold) },
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
            floatingActionButton = {
                if (isAuthenticated) {
                    FloatingActionButton(
                        onClick = {
                            viewModel.selectSecretForEdit(null)
                            onNavigateToAddEdit()
                        },
                        containerColor = MaterialTheme.colorScheme.primary
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "Add Secret", tint = Color.White)
                    }
                }
            },
            containerColor = Color.Transparent
        ) { padding ->
            if (isAuthenticated) {
                if (secrets.isEmpty()) {
                    Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                        Text("No secrets stored.", color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(padding),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(secrets) { secret ->
                            SecretItemCard(
                                secret = secret,
                                onClick = {
                                    viewModel.selectSecretForEdit(secret)
                                    onNavigateToAddEdit()
                                },
                                onDelete = {
                                    viewModel.deleteSecret(secret)
                                }
                            )
                        }
                    }
                }
            } else {
                Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                    Text("Authenticating...")
                }
            }
        }
    }
}

@Composable
fun SecretItemCard(
    secret: SecretEntity,
    onClick: () -> Unit,
    onDelete: () -> Unit
) {
    var showSecret by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = secret.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                IconButton(onClick = onDelete) {
                    Icon(Icons.Default.Delete, contentDescription = "Delete", tint = MaterialTheme.colorScheme.error)
                }
            }
            if (secret.username.isNotBlank()) {
                Text(
                    text = "User: ${secret.username}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (showSecret) secret.secretValue else "••••••••",
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.weight(1f)
                )
                IconButton(onClick = { showSecret = !showSecret }) {
                    Icon(
                        imageVector = if (showSecret) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                        contentDescription = "Toggle Visibility"
                    )
                }
            }
        }
    }
}
