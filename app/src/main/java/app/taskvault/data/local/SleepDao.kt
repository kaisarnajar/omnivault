package app.taskvault.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface SleepDao {
    @Query("SELECT * FROM sleep_entries WHERE userId = :userId ORDER BY timestamp DESC")
    fun getSleepEntriesForUser(userId: String): Flow<List<SleepEntryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSleepEntry(entry: SleepEntryEntity)

    @Update
    suspend fun updateSleepEntry(entry: SleepEntryEntity)

    @Query("DELETE FROM sleep_entries WHERE id = :id AND userId = :userId")
    suspend fun deleteSleepEntryById(id: String, userId: String)

    @Query("DELETE FROM sleep_entries WHERE userId = :userId")
    suspend fun deleteAllSleepEntriesForUser(userId: String)
}
