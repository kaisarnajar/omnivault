package app.taskvault.ui.vault

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.taskvault.data.local.SecretEntity
import app.taskvault.data.repository.SecretRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class VaultViewModel @Inject constructor(
    private val repository: SecretRepository
) : ViewModel() {

    private val _secrets = MutableStateFlow<List<SecretEntity>>(emptyList())
    val secrets: StateFlow<List<SecretEntity>> = _secrets.asStateFlow()

    private val _selectedSecret = MutableStateFlow<SecretEntity?>(null)
    val selectedSecret: StateFlow<SecretEntity?> = _selectedSecret.asStateFlow()

    init {
        loadSecrets()
    }

    private fun loadSecrets() {
        viewModelScope.launch {
            repository.getAllSecrets()
                .catch { e ->
                    // Handle error (e.g., log it)
                }
                .collect { list ->
                    _secrets.value = list
                }
        }
    }

    fun selectSecretForEdit(secret: SecretEntity?) {
        _selectedSecret.value = secret
    }

    fun addOrUpdateSecret(title: String, username: String, secretValue: String, notes: String) {
        viewModelScope.launch {
            val current = _selectedSecret.value
            if (current == null) {
                repository.insertSecret(
                    SecretEntity(
                        title = title,
                        username = username,
                        secretValue = secretValue,
                        notes = notes
                    )
                )
            } else {
                repository.updateSecret(
                    current.copy(
                        title = title,
                        username = username,
                        secretValue = secretValue,
                        notes = notes,
                        timestamp = System.currentTimeMillis()
                    )
                )
            }
            _selectedSecret.value = null
        }
    }

    fun deleteSecret(secret: SecretEntity) {
        viewModelScope.launch {
            repository.deleteSecret(secret)
        }
    }
}
