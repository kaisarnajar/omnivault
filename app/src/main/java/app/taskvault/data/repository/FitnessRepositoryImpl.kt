package app.taskvault.data.repository

import app.taskvault.data.local.FitnessActivityEntity
import app.taskvault.data.local.FitnessDao
import app.taskvault.domain.AuthRepository
import app.taskvault.domain.AuthState
import app.taskvault.domain.FitnessRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import java.util.UUID
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
class FitnessRepositoryImpl @Inject constructor(
    private val fitnessDao: FitnessDao,
    private val authRepository: AuthRepository
) : FitnessRepository {

    override fun getActivities(): Flow<List<FitnessActivityEntity>> {
        return authRepository.authState.flatMapLatest { authState ->
            if (authState is AuthState.Authenticated) {
                val userId = authRepository.getCurrentUserId()
                if (userId != null) {
                    fitnessDao.getActivitiesForUser(userId)
                } else {
                    flowOf(emptyList())
                }
            } else {
                flowOf(emptyList())
            }
        }
    }

    override suspend fun addActivity(
        activityType: String,
        title: String,
        targetMuscle: String,
        distanceKm: Double,
        durationMinutes: Int,
        caloriesBurned: Int,
        notes: String
    ) {
        val userId = authRepository.getCurrentUserId() ?: return
        val entity = FitnessActivityEntity(
            id = UUID.randomUUID().toString(),
            userId = userId,
            activityType = activityType,
            title = title.ifBlank { activityType },
            targetMuscle = targetMuscle,
            distanceKm = distanceKm,
            durationMinutes = durationMinutes,
            caloriesBurned = caloriesBurned,
            notes = notes,
            timestamp = System.currentTimeMillis()
        )
        fitnessDao.insertActivity(entity)
    }

    override suspend fun deleteActivity(id: String) {
        val userId = authRepository.getCurrentUserId() ?: return
        fitnessDao.deleteActivity(id, userId)
    }

    override suspend fun seedSampleData() {
        val sampleActivities = listOf(
            FitnessSeed("Workout", "Chest & Triceps Day", "Chest", 0.0, 45, 320, "Bench press 4x10, Incline Dumbbell 3x12"),
            FitnessSeed("Running", "Morning Jog", "", 5.2, 32, 410, "Pace: 6:09/km. Felt great!"),
            FitnessSeed("Sports", "Badminton Match", "", 0.0, 60, 480, "Won 2 out of 3 sets with friends"),
            FitnessSeed("Workout", "Leg Day & Core", "Legs", 0.0, 50, 380, "Squats 4x8, Leg press 3x15, Planks"),
            FitnessSeed("Sports", "Swimming Laps", "", 1.5, 40, 350, "Freestyle and backstroke laps")
        )
        for (item in sampleActivities) {
            addActivity(
                item.activityType,
                item.title,
                item.targetMuscle,
                item.distanceKm,
                item.durationMinutes,
                item.caloriesBurned,
                item.notes
            )
        }
    }
}

private data class FitnessSeed(
    val activityType: String,
    val title: String,
    val targetMuscle: String,
    val distanceKm: Double,
    val durationMinutes: Int,
    val caloriesBurned: Int,
    val notes: String
)
