package app.taskvault.ui.profile

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.taskvault.domain.ProfileRepository
import app.taskvault.domain.UserProfile
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ProfileViewModel(
    private val profileRepository: ProfileRepository
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

    fun updateProfile(displayName: String, newImageUri: Uri?) {
        _uiState.value = ProfileUiState.Loading
        viewModelScope.launch {
            try {
                // Update display name
                if (displayName.isNotBlank()) {
                    val nameResult = profileRepository.updateDisplayName(displayName)
                    if (nameResult.isFailure) {
                        _uiState.value = ProfileUiState.Error("Failed to update name")
                        return@launch
                    }
                }

                // Update image if selected
                if (newImageUri != null) {
                    val uploadResult = profileRepository.uploadProfileImage(newImageUri)
                    if (uploadResult.isSuccess) {
                        val downloadUrl = uploadResult.getOrNull()!!
                        val urlResult = profileRepository.updatePhotoUrl(downloadUrl)
                        if (urlResult.isFailure) {
                            _uiState.value = ProfileUiState.Error("Failed to save image URL")
                            return@launch
                        }
                    } else {
                        _uiState.value = ProfileUiState.Error("Failed to upload image")
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
