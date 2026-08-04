package app.taskvault.data.repository

import app.taskvault.data.local.ScratchpadDao
import app.taskvault.data.local.ScratchpadEntity
import app.taskvault.domain.AuthRepository
import app.taskvault.domain.ScratchpadRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map

class ScratchpadRepositoryImpl(
    private val scratchpadDao: ScratchpadDao,
    private val authRepository: AuthRepository
) : ScratchpadRepository {

    override fun getScratchpadContent(): Flow<String> {
        return authRepository.authState.flatMapLatest { authState ->
            if (authState is app.taskvault.domain.AuthState.Authenticated) {
                val userId = authRepository.getCurrentUserId()
                if (userId != null) {
                    scratchpadDao.getScratchpad(userId).map { it?.content ?: "" }
                } else {
                    flowOf("")
                }
            } else {
                flowOf("")
            }
        }
    }

    override suspend fun saveScratchpadContent(content: String) {
        val userId = authRepository.getCurrentUserId()
        if (userId != null) {
            val entity = ScratchpadEntity(
                userId = userId,
                content = content,
                lastUpdated = System.currentTimeMillis()
            )
            scratchpadDao.insertOrUpdate(entity)
        }
    }
}
