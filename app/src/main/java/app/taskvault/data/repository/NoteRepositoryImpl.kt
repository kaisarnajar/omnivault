package app.taskvault.data.repository

import app.taskvault.data.local.NoteDao
import app.taskvault.data.local.NoteEntity
import app.taskvault.domain.AuthRepository
import app.taskvault.domain.AuthState
import app.taskvault.domain.NoteRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import java.util.UUID
import javax.inject.Inject

@OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
class NoteRepositoryImpl @Inject constructor(
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

    override suspend fun seedSampleData() {
        val userId = authRepository.getCurrentUserId() ?: return
        val currentTime = System.currentTimeMillis()

        for (i in 1..20) {
            val entity = NoteEntity(
                id = UUID.randomUUID().toString(),
                userId = userId,
                title = "Sample Note $i",
                content = "This is a randomly generated sample note for testing purposes. It contains some dummy text to fill the description.",
                lastUpdated = currentTime - (Math.random() * 86400000 * 5).toLong()
            )
            noteDao.insertOrUpdate(entity)
        }
    }
}
