package app.taskvault.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ScratchpadDao {
    @Query("SELECT * FROM scratchpad WHERE userId = :userId")
    fun getScratchpad(userId: String): Flow<ScratchpadEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(scratchpad: ScratchpadEntity)
}
