package app.taskvault.data.repository

import app.taskvault.data.local.SecretDao
import app.taskvault.data.local.SecretEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SecretRepositoryImpl @Inject constructor(
    private val secretDao: SecretDao
) : SecretRepository {

    override fun getAllSecrets(): Flow<List<SecretEntity>> {
        return secretDao.getAllSecrets()
    }

    override suspend fun insertSecret(secret: SecretEntity) {
        secretDao.insertSecret(secret)
    }

    override suspend fun updateSecret(secret: SecretEntity) {
        secretDao.updateSecret(secret)
    }

    override suspend fun deleteSecret(secret: SecretEntity) {
        secretDao.deleteSecret(secret)
    }

    override suspend fun seedSampleData() {
        for (i in 1..5) {
            val secret = SecretEntity(
                title = "Sample Secret $i",
                username = "user$i",
                secretValue = "P@ssw0rd$i",
                notes = "Sample notes for secret $i",
                timestamp = System.currentTimeMillis()
            )
            insertSecret(secret)
        }
    }
}
