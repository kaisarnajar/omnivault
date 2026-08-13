package app.taskvault.domain

import app.taskvault.data.local.MoodEntryEntity
import kotlinx.coroutines.flow.Flow

interface MoodRepository {
    fun getMoods(): Flow<List<MoodEntryEntity>>
    suspend fun addMood(mood: String, emoji: String, note: String)
    suspend fun deleteMood(id: String)
    suspend fun seedSampleData()
}
