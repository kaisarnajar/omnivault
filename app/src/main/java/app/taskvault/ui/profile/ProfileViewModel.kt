package app.taskvault.ui.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.taskvault.domain.NoteRepository
import app.taskvault.domain.PomodoroHistoryRepository
import app.taskvault.domain.ProfileRepository
import app.taskvault.domain.TodoRepository
import app.taskvault.domain.UserProfile
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val profileRepository: ProfileRepository,
    private val todoRepository: TodoRepository,
    private val noteRepository: NoteRepository,
    private val pomodoroHistoryRepository: PomodoroHistoryRepository,
    private val expenseRepository: app.taskvault.domain.ExpenseRepository,
    private val secretRepository: app.taskvault.data.repository.SecretRepository,
    private val ledgerRepository: app.taskvault.domain.LedgerRepository,
    private val moodRepository: app.taskvault.domain.MoodRepository,
    private val bookmarkRepository: app.taskvault.domain.BookmarkRepository,
    private val fitnessRepository: app.taskvault.domain.FitnessRepository
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
        email: String
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

    suspend fun seedTasks() {
        todoRepository.seedSampleData()
    }

    suspend fun seedNotes() {
        noteRepository.seedSampleData()
    }

    suspend fun seedPomodoro() {
        pomodoroHistoryRepository.seedSampleData()
    }

    suspend fun seedExpenses() {
        expenseRepository.seedSampleData()
    }

    suspend fun seedSecrets() {
        secretRepository.seedSampleData()
    }

    suspend fun seedLedger() {
        ledgerRepository.seedSampleData()
    }

    suspend fun seedMood() {
        moodRepository.seedSampleData()
    }

    suspend fun seedBookmarks() {
        bookmarkRepository.seedSampleData()
    }

    suspend fun seedFitness() {
        fitnessRepository.seedSampleData()
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
