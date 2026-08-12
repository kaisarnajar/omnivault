package app.taskvault.domain

import app.taskvault.data.local.PomodoroSessionEntity
import kotlinx.coroutines.flow.Flow

interface PomodoroHistoryRepository {
    fun getSessions(): Flow<List<PomodoroSessionEntity>>
    suspend fun saveSession(durationInMinutes: Int)
    suspend fun seedSampleData()
}
