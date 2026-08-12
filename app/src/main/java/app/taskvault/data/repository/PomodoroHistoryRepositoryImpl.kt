package app.taskvault.data.repository

import app.taskvault.data.local.PomodoroDao
import app.taskvault.data.local.PomodoroSessionEntity
import app.taskvault.domain.AuthRepository
import app.taskvault.domain.AuthState
import app.taskvault.domain.PomodoroHistoryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import java.util.UUID

class PomodoroHistoryRepositoryImpl(
    private val pomodoroDao: PomodoroDao,
    private val authRepository: AuthRepository
) : PomodoroHistoryRepository {

    override fun getSessions(): Flow<List<PomodoroSessionEntity>> {
        return authRepository.authState.flatMapLatest { authState ->
            if (authState is AuthState.Authenticated) {
                val userId = authRepository.getCurrentUserId()
                if (userId != null) {
                    pomodoroDao.getSessionsForUser(userId)
                } else {
                    flowOf(emptyList())
                }
            } else {
                flowOf(emptyList())
            }
        }
    }

    override suspend fun saveSession(durationInMinutes: Int) {
        val userId = authRepository.getCurrentUserId() ?: return
        val entity = PomodoroSessionEntity(
            id = UUID.randomUUID().toString(),
            userId = userId,
            durationInMinutes = durationInMinutes,
            timestamp = System.currentTimeMillis()
        )
        pomodoroDao.insertSession(entity)
    }
}
