package app.taskvault.data.repository

import android.net.Uri
import app.taskvault.domain.ProfileRepository
import app.taskvault.domain.UserProfile
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.userProfileChangeRequest
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await
import java.util.UUID

class ProfileRepositoryImpl(
    private val firebaseAuth: FirebaseAuth,
    private val firebaseStorage: FirebaseStorage
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

    override suspend fun uploadProfileImage(uri: Uri): Result<String> {
        val user = firebaseAuth.currentUser ?: return Result.failure(Exception("User not logged in"))
        return try {
            val storageRef = firebaseStorage.reference.child("profile_images/${user.uid}/${UUID.randomUUID()}")
            val uploadTask = storageRef.putFile(uri).await()
            val downloadUrl = storageRef.downloadUrl.await().toString()
            Result.success(downloadUrl)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updatePhotoUrl(url: String): Result<Unit> {
        val user = firebaseAuth.currentUser ?: return Result.failure(Exception("User not logged in"))
        return try {
            val profileUpdates = userProfileChangeRequest {
                photoUri = Uri.parse(url)
            }
            user.updateProfile(profileUpdates).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
