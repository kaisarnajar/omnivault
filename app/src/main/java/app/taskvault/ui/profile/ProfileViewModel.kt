package app.taskvault.ui.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.taskvault.domain.ProfileRepository
import app.taskvault.domain.TodoRepository
import app.taskvault.domain.UserProfile
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

import app.taskvault.domain.NoteRepository
import app.taskvault.domain.PomodoroHistoryRepository

class ProfileViewModel(
    private val profileRepository: ProfileRepository,
    private val todoRepository: TodoRepository,
    private val noteRepository: NoteRepository,
    private val pomodoroHistoryRepository: PomodoroHistoryRepository,
) : ViewModel() {
    private val _userProfile = MutableStateFlow<UserProfile?>(null)
    val userProfile: StateFlow<UserProfile?> = _userProfile.asStateFlow()

    private val _uiState = MutableStateFlow<ProfileUiState>(ProfileUiState.Idle)
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    init {
        loadProfile()
    }

    fun loadProfile() {
        viewModelScope.launch {
            _userProfile.value = profileRepository.getUserProfile()
        }
    }

    fun updateProfile(
        displayName: String,
        email: String,
    ) {
        _uiState.value = ProfileUiState.Loading
        viewModelScope.launch {
            try {
                if (displayName.isNotBlank()) {
                    val nameResult = profileRepository.updateDisplayName(displayName)
                    if (nameResult.isFailure) {
                        _uiState.value = ProfileUiState.Error("Failed to update name")
                        return@launch
                    }
                }

                if (email.isNotBlank() && email != _userProfile.value?.email) {
                    val emailResult = profileRepository.updateEmail(email)
                    if (emailResult.isFailure) {
                        val errorMsg = emailResult.exceptionOrNull()?.message ?: "Failed to update email"
                        _uiState.value = ProfileUiState.Error("Email update failed. (You may need to re-login): $errorMsg")
                        return@launch
                    }
                }

                // Reload profile
                loadProfile()
                _uiState.value = ProfileUiState.Success
            } catch (e: Exception) {
                _uiState.value = ProfileUiState.Error(e.message ?: "An error occurred")
            }
        }
    }

    fun seedTasks() {
        viewModelScope.launch {
            todoRepository.seedSampleData()
        }
    }

    fun seedNotes() {
        viewModelScope.launch {
            noteRepository.seedSampleData()
        }
    }

    fun seedPomodoro() {
        viewModelScope.launch {
            pomodoroHistoryRepository.seedSampleData()
        }
    }

    fun resetState() {
        _uiState.value = ProfileUiState.Idle
    }
}

sealed class ProfileUiState {
    object Idle : ProfileUiState()

    object Loading : ProfileUiState()

    object Success : ProfileUiState()

    data class Error(val message: String) : ProfileUiState()
}
