package app.taskvault.data.repository

import app.taskvault.domain.ProfileRepository
import app.taskvault.domain.UserProfile
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.userProfileChangeRequest
import kotlinx.coroutines.tasks.await


import javax.inject.Inject

class ProfileRepositoryImpl @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
) : ProfileRepository {
    override suspend fun getUserProfile(): UserProfile? {
        val user = firebaseAuth.currentUser ?: return null
        return UserProfile(
            displayName = user.displayName,
            email = user.email,
            photoUrl = user.photoUrl?.toString(),
        )
    }

    override suspend fun updateDisplayName(name: String): Result<Unit> {
        val user = firebaseAuth.currentUser ?: return Result.failure(Exception("User not logged in"))
        return try {
            val profileUpdates =
                userProfileChangeRequest {
                    displayName = name
                }
            user.updateProfile(profileUpdates).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateEmail(email: String): Result<Unit> {
        val user = firebaseAuth.currentUser ?: return Result.failure(Exception("User not logged in"))
        return try {
            user.verifyBeforeUpdateEmail(email).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
