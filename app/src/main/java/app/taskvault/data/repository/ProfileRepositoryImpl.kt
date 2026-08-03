package app.taskvault.data.repository

import app.taskvault.domain.ProfileRepository
import app.taskvault.domain.UserProfile
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.userProfileChangeRequest
import kotlinx.coroutines.tasks.await

class ProfileRepositoryImpl(
    private val firebaseAuth: FirebaseAuth
) : ProfileRepository {

    override suspend fun getUserProfile(): UserProfile? {
        val user = firebaseAuth.currentUser ?: return null
        return UserProfile(
            displayName = user.displayName,
            photoUrl = user.photoUrl?.toString()
        )
    }

    override suspend fun updateDisplayName(name: String): Result<Unit> {
        val user = firebaseAuth.currentUser ?: return Result.failure(Exception("User not logged in"))
        return try {
            val profileUpdates = userProfileChangeRequest {
                displayName = name
            }
            user.updateProfile(profileUpdates).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
