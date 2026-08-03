package app.taskvault.domain

import android.net.Uri

data class UserProfile(
    val displayName: String?,
    val photoUrl: String?
)

interface ProfileRepository {
    suspend fun getUserProfile(): UserProfile?
    suspend fun updateDisplayName(name: String): Result<Unit>
    suspend fun uploadProfileImage(uri: Uri): Result<String>
    suspend fun updatePhotoUrl(url: String): Result<Unit>
}
