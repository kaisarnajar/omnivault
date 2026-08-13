package app.taskvault.data.repository

import app.taskvault.data.local.SecretEntity
import kotlinx.coroutines.flow.Flow

interface SecretRepository {
    fun getAllSecrets(): Flow<List<SecretEntity>>
    suspend fun insertSecret(secret: SecretEntity)
    suspend fun updateSecret(secret: SecretEntity)
    suspend fun deleteSecret(secret: SecretEntity)
    suspend fun seedSampleData()
}
