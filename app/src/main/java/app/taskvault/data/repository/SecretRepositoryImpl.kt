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
}
