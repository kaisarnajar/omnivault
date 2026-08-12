package app.taskvault.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface PomodoroDao {
    @Query("SELECT * FROM pomodoro_sessions WHERE userId = :userId ORDER BY timestamp DESC")
    fun getSessionsForUser(userId: String): Flow<List<PomodoroSessionEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSession(session: PomodoroSessionEntity)
}
