package app.taskvault.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface SecretDao {
    @Query("SELECT * FROM secrets ORDER BY timestamp DESC")
    fun getAllSecrets(): Flow<List<SecretEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSecret(secret: SecretEntity)

    @Update
    suspend fun updateSecret(secret: SecretEntity)

    @Delete
    suspend fun deleteSecret(secret: SecretEntity)
}
