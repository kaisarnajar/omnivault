package app.taskvault.domain

import app.taskvault.data.local.SleepEntryEntity
import kotlinx.coroutines.flow.Flow

interface SleepRepository {
    fun getSleepEntries(): Flow<List<SleepEntryEntity>>
    suspend fun insertSleepEntry(entry: SleepEntryEntity)
    suspend fun deleteSleepEntry(id: String)
    suspend fun seedSampleData()
}
