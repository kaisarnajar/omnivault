package app.taskvault.domain

import app.taskvault.data.local.NoteEntity
import kotlinx.coroutines.flow.Flow

interface NoteRepository {
    fun getNotes(): Flow<List<NoteEntity>>
    suspend fun getNoteById(id: String): NoteEntity?
    suspend fun saveNote(id: String, title: String, content: String)
    suspend fun deleteNote(id: String)
    suspend fun seedSampleData()
}
