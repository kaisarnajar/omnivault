package app.taskvault.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface MoodDao {
    @Query("SELECT * FROM mood_entries WHERE userId = :userId ORDER BY timestamp DESC")
    fun getMoodsForUser(userId: String): Flow<List<MoodEntryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMood(mood: MoodEntryEntity)

    @Query("DELETE FROM mood_entries WHERE id = :id AND userId = :userId")
    suspend fun deleteMood(id: String, userId: String)
}
