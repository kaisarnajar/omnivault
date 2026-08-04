package app.taskvault.domain

data class UserProfile(
    val displayName: String?,
    val email: String?,
    val photoUrl: String?,
)

interface ProfileRepository {
    suspend fun getUserProfile(): UserProfile?

    suspend fun updateDisplayName(name: String): Result<Unit>

    suspend fun updateEmail(email: String): Result<Unit>
}
