package app.taskvault.data.repository

import app.taskvault.data.local.MoodDao
import app.taskvault.data.local.MoodEntryEntity
import app.taskvault.domain.AuthRepository
import app.taskvault.domain.AuthState
import app.taskvault.domain.MoodRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import java.util.UUID
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
class MoodRepositoryImpl @Inject constructor(
    private val moodDao: MoodDao,
    private val authRepository: AuthRepository
) : MoodRepository {

    override fun getMoods(): Flow<List<MoodEntryEntity>> {
        return authRepository.authState.flatMapLatest { authState ->
            if (authState is AuthState.Authenticated) {
                val userId = authRepository.getCurrentUserId()
                if (userId != null) {
                    moodDao.getMoodsForUser(userId)
                } else {
                    flowOf(emptyList())
                }
            } else {
                flowOf(emptyList())
            }
        }
    }

    override suspend fun addMood(mood: String, emoji: String, note: String) {
        val userId = authRepository.getCurrentUserId() ?: return
        val entity = MoodEntryEntity(
            id = UUID.randomUUID().toString(),
            userId = userId,
            mood = mood,
            emoji = emoji,
            note = note,
            timestamp = System.currentTimeMillis()
        )
        moodDao.insertMood(entity)
    }

    override suspend fun deleteMood(id: String) {
        val userId = authRepository.getCurrentUserId() ?: return
        moodDao.deleteMood(id, userId)
    }

    override suspend fun seedSampleData() {
        val sampleMoods = listOf(
            Triple("Amazing", "😄", "Had a productive day finishing all tasks!"),
            Triple("Good", "🙂", "Went for a nice walk and relaxed."),
            Triple("Okay", "😐", "Regular workday, nothing special."),
            Triple("Bad", "🙁", "Felt a bit tired and overwhelmed."),
            Triple("Awful", "😫", "Rough day with poor sleep.")
        )
        for ((mood, emoji, note) in sampleMoods) {
            addMood(mood, emoji, note)
        }
    }
}
