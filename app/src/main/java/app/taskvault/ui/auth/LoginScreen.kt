package app.taskvault.ui.auth

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.taskvault.R
import app.taskvault.ui.components.OmniVaultBackground
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException

@Composable
fun LoginScreen(
    viewModel: AuthViewModel,
    onNavigateToRegister: () -> Unit,
    onLoginSuccess: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var showResetDialog by remember { mutableStateOf(false) }
    val context = LocalContext.current

    val uiState by viewModel.uiState.collectAsState()
    val resetState by viewModel.resetPasswordState.collectAsState()

    val googleSignInLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
        try {
            val account = task.getResult(ApiException::class.java)
            account?.idToken?.let { idToken ->
                viewModel.signInWithGoogle(idToken)
            }
        } catch (e: ApiException) {
            // Handle error, e.g., show a toast or let ViewModel handle
        }
    }

    LaunchedEffect(uiState) {
        if (uiState is AuthUiState.Success) {
            viewModel.resetState()
            onLoginSuccess()
        }
    }

    OmniVaultBackground {
        Box(
            modifier =
            Modifier
                .fillMaxSize()
                .background(Color.Transparent)
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "OmniVault",
                    style =
                    MaterialTheme.typography.displayMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                )

                Text(
                    text = "Sign in to continue",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(32.dp))

                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email") },
                    leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true
                )

                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Password") },
                    leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
                    visualTransformation = PasswordVisualTransformation(),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true
                )

                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.CenterEnd) {
                    Text(
                        text = "Forgot Password?",
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.clickable { showResetDialog = true }.padding(vertical = 8.dp)
                    )
                }

                if (uiState is AuthUiState.Error) {
                    Text(
                        text = (uiState as AuthUiState.Error).message,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                app.taskvault.ui.components.GradientButton(
                    text = "Login",
                    onClick = { viewModel.login(email, password) },
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    enabled = uiState !is AuthUiState.Loading && email.isNotBlank() && password.isNotBlank(),
                    isLoading = uiState is AuthUiState.Loading
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedButton(
                    onClick = {
                        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                            .requestIdToken(context.getString(R.string.default_web_client_id))
                            .requestEmail()
                            .build()
                        val googleSignInClient = GoogleSignIn.getClient(context, gso)
                        googleSignInLauncher.launch(googleSignInClient.signInIntent)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Continue with Google", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "Don't have an account? ", color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(
                        text = "Sign up",
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.clickable { onNavigateToRegister() }
                    )
                }
            }
        }

        if (showResetDialog) {
            var resetEmail by remember { mutableStateOf(email) }

            AlertDialog(
                onDismissRequest = {
                    showResetDialog = false
                    viewModel.resetPasswordState()
                },
                title = { Text("Reset Password") },
                text = {
                    Column {
                        if (resetState is ResetPasswordState.Success) {
                            Text("Reset link sent! Check your inbox.", color = MaterialTheme.colorScheme.primary)
                        } else {
                            Text("Enter your email address and we will send you a link to reset your password.", modifier = Modifier.padding(bottom = 16.dp))
                            OutlinedTextField(
                                value = resetEmail,
                                onValueChange = { resetEmail = it },
                                label = { Text("Email") },
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true
                            )
                            if (resetState is ResetPasswordState.Error) {
                                Text(
                                    text = (resetState as ResetPasswordState.Error).message,
                                    color = MaterialTheme.colorScheme.error,
                                    style = MaterialTheme.typography.bodySmall,
                                    modifier = Modifier.padding(top = 8.dp)
                                )
                            }
                        }
                    }
                },
                confirmButton = {
                    if (resetState !is ResetPasswordState.Success) {
                        Button(
                            onClick = { viewModel.sendPasswordResetEmail(resetEmail) },
                            enabled = resetEmail.isNotBlank() && resetState !is ResetPasswordState.Loading
                        ) {
                            if (resetState is ResetPasswordState.Loading) {
                                CircularProgressIndicator(modifier = Modifier.size(16.dp), color = MaterialTheme.colorScheme.onPrimary)
                            } else {
                                Text("Send Link")
                            }
                        }
                    } else {
                        Button(onClick = {
                            showResetDialog = false
                            viewModel.resetPasswordState()
                        }) {
                            Text("OK")
                        }
                    }
                },
                dismissButton = {
                    if (resetState !is ResetPasswordState.Success) {
                        TextButton(onClick = {
                            showResetDialog = false
                            viewModel.resetPasswordState()
                        }) {
                            Text("Cancel")
                        }
                    }
                }
            )
        }
    }
}
