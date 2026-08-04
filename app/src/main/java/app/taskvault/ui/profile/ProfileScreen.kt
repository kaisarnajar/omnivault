package app.taskvault.ui.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel,
    onNavigateBack: () -> Unit,
) {
    val userProfile by viewModel.userProfile.collectAsState()
    val uiState by viewModel.uiState.collectAsState()

    var isEditing by remember { mutableStateOf(false) }
    var displayName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }

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

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (isEditing) "Edit Profile" else "Profile") },
                navigationIcon = {
                    IconButton(onClick = {
                        if (isEditing) {
                            isEditing = false
                            // Reset fields
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
                colors =
                    TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.background,
                    ),
            )
        },
    ) { paddingValues ->
        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(24.dp),
        ) {
            Box(
                modifier =
                    Modifier
                        .size(120.dp)
                        .clip(androidx.compose.foundation.shape.CircleShape)
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .border(2.dp, MaterialTheme.colorScheme.primary, androidx.compose.foundation.shape.CircleShape),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = androidx.compose.material.icons.Icons.Default.Person,
                    contentDescription = null,
                    modifier = Modifier.size(64.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }

            if (isEditing) {
                OutlinedTextField(
                    value = displayName,
                    onValueChange = { displayName = it },
                    label = { Text("Display Name") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true,
                )

                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true,
                )
            } else {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    Text(
                        text = userProfile?.displayName ?: "No Name",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                    )
                    Text(
                        text = userProfile?.email ?: "No Email",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }

            if (uiState is ProfileUiState.Error) {
                Text(
                    text = (uiState as ProfileUiState.Error).message,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(top = 8.dp),
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            if (isEditing) {
                Button(
                    onClick = { viewModel.updateProfile(displayName, email) },
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                    shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp),
                    enabled = uiState !is ProfileUiState.Loading && displayName.isNotBlank() && email.isNotBlank(),
                ) {
                    if (uiState is ProfileUiState.Loading) {
                        CircularProgressIndicator(modifier = Modifier.size(24.dp), color = MaterialTheme.colorScheme.onPrimary)
                    } else {
                        Text("Save Profile", fontWeight = FontWeight.Bold)
                    }
                }
            } else {
                Button(
                    onClick = { isEditing = true },
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                    shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp),
                ) {
                    Text("Edit Profile", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
