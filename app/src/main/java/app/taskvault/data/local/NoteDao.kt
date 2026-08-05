package app.taskvault.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface NoteDao {
    @Query("SELECT * FROM notes WHERE userId = :userId ORDER BY lastUpdated DESC")
    fun getNotes(userId: String): Flow<List<NoteEntity>>

    @Query("SELECT * FROM notes WHERE id = :id AND userId = :userId")
    suspend fun getNoteById(id: String, userId: String): NoteEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(note: NoteEntity)

    @Query("DELETE FROM notes WHERE id = :id AND userId = :userId")
    suspend fun deleteNote(id: String, userId: String)
}
