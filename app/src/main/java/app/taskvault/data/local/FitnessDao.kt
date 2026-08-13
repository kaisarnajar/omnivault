package app.taskvault.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface FitnessDao {
    @Query("SELECT * FROM fitness_activities WHERE userId = :userId ORDER BY timestamp DESC")
    fun getActivitiesForUser(userId: String): Flow<List<FitnessActivityEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertActivity(activity: FitnessActivityEntity)

    @Query("DELETE FROM fitness_activities WHERE id = :id AND userId = :userId")
    suspend fun deleteActivity(id: String, userId: String)
}
