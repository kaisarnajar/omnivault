package app.taskvault.data.repository

import app.taskvault.data.local.NoteDao
import app.taskvault.data.local.NoteEntity
import app.taskvault.domain.AuthRepository
import app.taskvault.domain.AuthState
import app.taskvault.domain.NoteRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import java.util.UUID

class NoteRepositoryImpl(
    private val noteDao: NoteDao,
    private val authRepository: AuthRepository
) : NoteRepository {

    override fun getNotes(): Flow<List<NoteEntity>> {
        return authRepository.authState.flatMapLatest { authState ->
            if (authState is AuthState.Authenticated) {
                val userId = authRepository.getCurrentUserId()
                if (userId != null) {
                    noteDao.getNotes(userId)
                } else {
                    flowOf(emptyList())
                }
            } else {
                flowOf(emptyList())
            }
        }
    }

    override suspend fun getNoteById(id: String): NoteEntity? {
        val userId = authRepository.getCurrentUserId() ?: return null
        return noteDao.getNoteById(id, userId)
    }

    override suspend fun saveNote(id: String, title: String, content: String) {
        val userId = authRepository.getCurrentUserId() ?: return
        val entity = NoteEntity(
            id = id,
            userId = userId,
            title = title,
            content = content,
            lastUpdated = System.currentTimeMillis()
        )
        noteDao.insertOrUpdate(entity)
    }

    override suspend fun deleteNote(id: String) {
        val userId = authRepository.getCurrentUserId() ?: return
        noteDao.deleteNote(id, userId)
    }
}
